package com.github.snailycy.androidhybridlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.snailycy.hybridlib.bridge.JSBridge;
import com.github.snailycy.hybridlib.webview.WrapperWebView;
import com.github.snailycy.hybridlib.webview.X5WebChromeClient;
import com.github.snailycy.hybridlib.webview.X5WebViewClient;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        WrapperWebView webView = (WrapperWebView) findViewById(R.id.webview);
        // 1.设置ua和WebViewClient,WebChromeClient
        webView.setUserAgent(true, "cardapp.client.android", 66666, "zh_CN");
        webView.setWebChromeClient(new X5WebChromeClient(webView));
        webView.setWebViewClient(new X5WebViewClient(webView, true));

        // 2.注册jsApi
        JSBridge jsBridge = new JSBridge(webView);
        jsBridge.registerJSApi("getLocation", JSLocationPlugin.class);

        // 3.load html
        webView.loadUrl("file:///android_asset/FRWCardApp.html");
    }
}
