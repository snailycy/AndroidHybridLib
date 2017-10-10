package com.github.snailycy.androidhybridlib;

import android.app.Activity;

import com.github.snailycy.hybridlib.webview.IWebViewClient;
import com.google.gson.JsonObject;
import com.tencent.smtt.sdk.WebView;

/**
 * Created by ycy on 2017/9/30.
 */

public class MyWebViewClient implements IWebViewClient {
    private Activity activity;

    public MyWebViewClient(Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return false;
    }

    @Override
    public boolean interceptRequestNetworkResult(JsonObject result) {
        return false;
    }
}
