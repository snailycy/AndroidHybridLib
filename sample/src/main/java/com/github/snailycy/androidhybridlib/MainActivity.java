package com.github.snailycy.androidhybridlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.github.snailycy.hybridlib.bridge.JSBridge;
import com.github.snailycy.hybridlib.webview.WebViewPool;
import com.github.snailycy.hybridlib.webview.WrapperWebView;

public class MainActivity extends AppCompatActivity {

    private WrapperWebView mWebView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        FrameLayout webViewContainer = (FrameLayout) findViewById(R.id.webview_container);

        // 获取webview并绑定新的context
        mWebView = WebViewPool.getInstance().getWebView();
        mWebView.bindNewContext(this);
        webViewContainer.addView(mWebView);

        // 设置isWhiteList，userAgent和webViewClient
        mWebView.setIsWhiteList(true);
        mWebView.setUserAgent("ycyapp.client.android", 666, "zh_CN");
        mWebView.setWebViewClient(new MyWebViewClient(this));

        // 注册jsApi
        JSBridge jsBridge = new JSBridge(mWebView);
        jsBridge.registerJSPlugin("getLocation", new JSLocationPlugin());
        jsBridge.registerJSPlugin("getMemoryCache", new JSGetCachePlugin());

        // load html
        mWebView.loadUrl("file:///android_asset/YCYApp.html");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WebViewPool.getInstance().resetWebView(mWebView);
    }
}
