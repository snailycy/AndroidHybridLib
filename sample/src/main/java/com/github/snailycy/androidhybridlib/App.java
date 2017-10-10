package com.github.snailycy.androidhybridlib;

import android.app.Application;

import com.github.snailycy.hybridlib.webview.WebViewPool;

/**
 * Created by ycy on 2017/10/9.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initWebViewPool();
    }

    private void initWebViewPool() {
        WebViewPool.getInstance().initWebViewPool(getApplicationContext());
    }
}
