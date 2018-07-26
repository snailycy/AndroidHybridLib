package com.github.snailycy.hybridlib.webview;

import android.net.Uri;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;

import com.github.snailycy.hybridlib.util.HybridCacheUtils;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * WebViewClient逻辑处理
 */

public class WebViewClientPresenter {

    private ProgressBar mProgressBar;
    /**
     * 是否是白名单
     */
    private boolean mIsWhiteList;
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(3);
    private IWebViewClient mBizWebViewClient;

    public WebViewClientPresenter(WrapperWebView webView) {
        this.mProgressBar = webView.getProgressBar();
    }

    public void setBizWebViewClient(IWebViewClient bizWebViewClient) {
        this.mBizWebViewClient = bizWebViewClient;
    }

    public void setIsWhiteList(boolean isWhiteList) {
        this.mIsWhiteList = isWhiteList;
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
    public WebResourceResponse shouldInterceptRequest(Uri uri) {
        if (null == uri || null == uri.getPath()) {
            return null;
        }
        // 判断请求是以下["css","js","jpg","jpeg","png","gif"]的资源，走本地缓存逻辑
        if (HybridCacheUtils.mountedSDCard() && HybridCacheUtils.needCache(uri)) {
            WebResourceResponse webResourceResponse = insteadOfCache(uri);
            return webResourceResponse;
        }
        return null;
    }


    /**
     * 检查是否有缓存，如有则读取本地缓存，否则缓存资源到本地
     *
     * @param uri 需要走缓存逻辑的网络请求URI
     * @return 若本地有缓存，返回缓存资源；否则返回null
     */
    private WebResourceResponse insteadOfCache(final Uri uri) {
        if (null == uri) {
            return null;
        }

        String uriPath = uri.getPath();
        if (TextUtils.isEmpty(uriPath)) return null;

        String localCachePath = HybridCacheUtils.convertUriToFilePath(uri);
        // 如果缓存存在，则取缓存
        if (HybridCacheUtils.checkPathExist(localCachePath)) {
            try {
                InputStream is = new FileInputStream(new File(localCachePath));
                return new WebResourceResponse(HybridCacheUtils.getResourceType(uriPath), "UTF-8", is);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 不存在，缓存在本地
            HybridCacheUtils.saveResource(uri, HybridCacheUtils.convertUriToFilePath(uri), null);
        }
        return null;
    }
}
