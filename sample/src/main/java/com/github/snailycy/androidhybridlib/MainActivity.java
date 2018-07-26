package com.github.snailycy.androidhybridlib;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.FrameLayout;

import com.github.snailycy.hybridlib.bridge.JSBridge;
import com.github.snailycy.hybridlib.util.CookieUtils;
import com.github.snailycy.hybridlib.webview.WebViewPool;
import com.github.snailycy.hybridlib.webview.WrapperWebView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private WrapperWebView mWebView;
    private FrameLayout mWebViewContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mWebViewContainer = (FrameLayout) findViewById(R.id.webview_container);

        // 获取webview并绑定新的context
        bindWebView();

        // 设置是否是白名单，userAgent和webViewClient
        configWebView();

        // 注册jsApi
        registerJSApi();

        // set cookie (optional)
        configCookie();

        // load html
        mWebView.loadUrl("file:///android_asset/YCYApp.html");
    }

    private void configCookie() {
        List<String> domainList = new ArrayList<>();
        domainList.add(".shuiguang.site");
        domainList.add(".baidu.com");
        Map<String, String> cookieMap = new HashMap<>();
        cookieMap.put("ycytoken", "asdfghjkl1234567890");
        cookieMap.put("ycymobile", "15958183839");
        CookieUtils.configCookie(this, mWebView.getWebView(), domainList, cookieMap);
    }

    private void registerJSApi() {
        JSBridge jsBridge = new JSBridge(mWebView);
        jsBridge.registerJSPlugin("getLocation", new JSLocationPlugin());
        jsBridge.registerJSPlugin("getMemoryCache", new JSGetCachePlugin());
    }

    private void configWebView() {
        mWebView.setIsWhiteList(true);
        mWebView.setUserAgent("ycyapp.client.android", 666, "zh_CN");
        mWebView.setWebViewClient(new MyWebViewClient(this));
    }

    private void bindWebView() {
        mWebView = WebViewPool.getInstance().getWebView();
        mWebView.bindNewContext(this);
        mWebViewContainer.removeAllViews();
        mWebViewContainer.addView(mWebView);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebViewContainer != null) {
            mWebViewContainer.removeAllViews();
        }
        WebViewPool.getInstance().resetWebView(mWebView);
    }
}
