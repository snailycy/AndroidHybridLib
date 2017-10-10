package com.github.snailycy.androidhybridlib;

import android.app.Application;
import android.util.Log;

import com.github.snailycy.hybridlib.webview.WebViewPool;
import com.tencent.smtt.sdk.QbSdk;

/**
 * Created by ycy on 2017/10/9.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        initHybrid();

    }

    private void initHybrid() {
        // 初始化WebView池
        WebViewPool.getInstance().initWebViewPool(getApplicationContext());

        // 初始化x5内核
        QbSdk.initX5Environment(getApplicationContext(), new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {
                Log.i("initX5Environment", "x5 core init finished.");
            }

            @Override
            public void onViewInitFinished(boolean b) {
                Log.i("initX5Environment", "x5 view has " + (b ? "" : "not") + " finished.");
            }
        });
    }
}
