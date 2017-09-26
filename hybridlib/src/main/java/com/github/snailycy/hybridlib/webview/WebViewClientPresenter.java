package com.github.snailycy.hybridlib.webview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.github.snailycy.hybridlib.util.CacheUtils;
import com.github.snailycy.hybridlib.util.HybridConstant;
import com.google.gson.JsonObject;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * WebViewClient逻辑处理
 */

public class WebViewClientPresenter {

    private Context mContext;
    private ProgressBar mProgressBar;
    /**
     * 是否是公司地址
     */
    private boolean mIsWhiteList;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);


    public WebViewClientPresenter(WrapperWebView webView, boolean isWhiteList) {
        this.mContext = webView.getContext();
        this.mProgressBar = webView.getProgressBar();
        this.mIsWhiteList = isWhiteList;
    }

    public boolean shouldOverrideUrlLoading(String url) {
        // 拦截支付
        if (!TextUtils.isEmpty(url)) {
            Uri uri = Uri.parse(url);
            if (uri.getScheme().equals(HybridConstant.HYBRID_FIREWAITER_SCHEME)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                if (mContext instanceof Activity)
                    ((Activity) mContext).startActivity(intent);
                return true;
            }
        }
        return false;
    }

    public void onPageStarted() {
        mProgressBar.setVisibility(View.VISIBLE);
        mProgressBar.setAlpha(1.0f);
    }

    /**
     * 判断Uri是否需要拦截
     *
     * @param uri 网络URI
     * @return 若需要拦截，返回WebResourceResponse；否则为null
     */
    public WebResourceResponse shouldInterceptRequest(Uri uri, String requestMethod, Map<String, String> maps) {
        if (null == uri || null == uri.getPath()) {
            return null;
        }
        // 判断请求是以下["css","js","jpg","jpeg","png","gif"]的资源，走本地缓存逻辑
        if (CacheUtils.mountedSDCard() && CacheUtils.needCache(uri)) {
            WebResourceResponse webResourceResponse = insteadOfCache(uri, mExecutorService);
            return webResourceResponse;
        }
        if (!TextUtils.isEmpty(requestMethod) && !requestMethod.equalsIgnoreCase("GET")) {
            // 请求方式GET时，走本地访问(POST或其他方式获取不到请求参数）
            return null;
        }

        // 其他请求走本地访问，不缓存
        WebResourceResponse webResourceResponseNet = requestNetwork(uri, maps);
        return webResourceResponseNet;
    }


    /**
     * 首先检查本地缓存有没有该资源，如有替换成本地，如没有缓存资源到本地
     *
     * @param uri 需要走缓存逻辑的网络请求URI
     * @return 若本地有缓存，返回缓存资源；否则返回null
     */
    private WebResourceResponse insteadOfCache(Uri uri, ExecutorService threadPool) {
        if (null == uri) {
            return null;
        }

        String localCachePath = CacheUtils.getLocalCache(uri, threadPool);
        if (!TextUtils.isEmpty(localCachePath)) {
            // 本地有缓存，直接替换成本地资源
            try {
                FileInputStream fileInputStream = new FileInputStream(new File(localCachePath));
                return getUtf8EncodedWebResourceResponse(uri.getPath(), fileInputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private WebResourceResponse getUtf8EncodedWebResourceResponse(String localResourcePath, InputStream data) {
        if (TextUtils.isEmpty(localResourcePath) || null == data) {
            return null;
        }
        String resourceType = CacheUtils.getResourceType(localResourcePath);
        return new WebResourceResponse(resourceType, "UTF-8", data);
    }

    private WebResourceResponse requestNetwork(Uri uri, Map<String, String> maps) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (null == url) {
            return null;
        }
        String scheme = uri.getScheme();
        if (!TextUtils.equals(scheme, "http") && !TextUtils.equals(scheme, "https")) {//不是可以跳转的网页
            return null;
        }

        HttpURLConnection connection = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 增加原来的请求头
        if (null != maps && null != maps.keySet()) {
            for (String key : maps.keySet()) {
                if (!TextUtils.isEmpty(key)) {
                    connection.addRequestProperty(key, maps.get(key));
                }
            }
        }

        // 请求访问失败，返回
        int responseCode = -1;
        try {
            responseCode = connection.getResponseCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (responseCode != 200) {
            return null;
        }

        InputStream inputStream = null;
        try {
            inputStream = connection.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (null == inputStream) {
            return null;
        }

        String contentType = connection.getContentType();
        String htmlMimeType = CacheUtils.getHtmlMimeType(contentType);
        String htmlEncoding = CacheUtils.getHtmlEncoding(contentType);
        WebResourceResponse webResourceResponse;

        if (mIsWhiteList && !TextUtils.isEmpty(contentType) && contentType.contains("application/json")) {
            //请求数据接口需判断返回code是否为-1
            ByteArrayOutputStream outputStream = CacheUtils.inputStreamCache(inputStream);
            JsonObject result = CacheUtils.getJsonObjectFromOutputStream(outputStream);
            int code = 0;
            if ((result != null && result.get("code") != null && !result.get("code").isJsonNull())) {
                try {
                    code = result.get("code").getAsInt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (code == -1) {
                // 重新登录
                Intent intent = new Intent();
                intent.setAction("android.intent.action.LOGIN");
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("___PATH___", "LoginFragment");
                if (mContext instanceof Activity) {
                    Activity activity = (Activity) this.mContext;
                    activity.startActivity(intent);
                    activity.finish();
                }
                return null;
            }
            webResourceResponse = new WebResourceResponse(htmlMimeType, htmlEncoding, CacheUtils.getInputStream(outputStream));
        } else {
            webResourceResponse = new WebResourceResponse(htmlMimeType, htmlEncoding, inputStream);
        }

        // 获取返回的请求头
        Map<String, String> webResourceResponseHeaders = new HashMap<>();
        Map<String, List<String>> headerFields = connection.getHeaderFields();
        if (null != headerFields && null != headerFields.keySet()) {
            for (String key : headerFields.keySet()) {
                List<String> headers = headerFields.get(key);
                if (null != headers && headers.size() > 0) {
                    webResourceResponseHeaders.put(key, headers.get(0));
                }
            }
            webResourceResponse.setResponseHeaders(webResourceResponseHeaders);
        }
        return webResourceResponse;
    }
}
