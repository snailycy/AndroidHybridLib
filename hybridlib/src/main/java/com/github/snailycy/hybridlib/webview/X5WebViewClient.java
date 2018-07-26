package com.github.snailycy.hybridlib.webview;

import android.graphics.Bitmap;

import com.tencent.smtt.export.external.interfaces.WebResourceRequest;
import com.tencent.smtt.export.external.interfaces.WebResourceResponse;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * @author snailycy
 */

public class X5WebViewClient extends WebViewClient {

    private WebViewClientPresenter mWebViewClientPresenter;
    /**
     * 预留处理具体业务的WebViewClient
     */
    private IWebViewClient mBizWebViewClient;

    public X5WebViewClient(WrapperWebView webView) {
        mWebViewClientPresenter = new WebViewClientPresenter(webView);
    }

    public void setBizWebViewClient(IWebViewClient bizWebViewClient) {
        this.mBizWebViewClient = bizWebViewClient;
        mWebViewClientPresenter.setBizWebViewClient(bizWebViewClient);
    }

    public void setIsWhiteList(boolean isWhiteList) {
        this.mWebViewClientPresenter.setIsWhiteList(isWhiteList);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        if (mBizWebViewClient != null) {
            return mBizWebViewClient.shouldOverrideUrlLoading(view, url);
        }
        return super.shouldOverrideUrlLoading(view, url);
    }

    @Override
    public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
        if (null != request) {
            WebResourceResponse localResource = mWebViewClientPresenter.shouldInterceptRequest(request.getUrl());
            // 不为空，则拦截
            if (null != localResource) {
                return localResource;
            }
        }
        return super.shouldInterceptRequest(view, request);
    }


    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        mWebViewClientPresenter.onPageStarted();
    }

}
