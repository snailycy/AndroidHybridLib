package com.github.snailycy.hybridlib.webview;

import com.google.gson.JsonObject;
import com.tencent.smtt.sdk.WebView;

/**
 * 业务方需要重写WebViewClient需实现该接口
 */

public interface IWebViewClient {
    /**
     * 拦截html#url
     *
     * @param view
     * @param url
     * @return
     */
    boolean shouldOverrideUrlLoading(WebView view, String url);

    /**
     * 拦截处理网络请求结果
     *
     * @param result
     * @return
     */
    boolean interceptRequestNetworkResult(JsonObject result);
}
