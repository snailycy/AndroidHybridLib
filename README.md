# AndroidHybridLib

JSBridge提供了Hybrid同步和异步调用。

实现WebView复用池，减少WebView初始化消耗的时间。

引入Context中间层，防止Activity内存泄漏。

集成腾讯x5内核，crash率低于0.06%，X5内核基于统一Blink内核，无缝隐藏系统差异，在所有Android手机平台表现一致。


### gradle

1. Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

```

2. Add the dependency

```
	dependencies {
	        compile 'com.github.snailycy:AndroidHybridLib:1.1.0'
	}

```

### How to use Hybrid

For example:

MainActivity.java

```
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


```

异步调用 JSLocationPlugin.java

```

public class JSLocationPlugin extends BaseJSPlugin {

    @Override
    public void jsCallNative(String callbackId, String requestParams) {
        // do something ...
        Toast.makeText(getContext(),"jsCallNative,requestParams = "+requestParams,Toast.LENGTH_LONG).show();
        JSONObject rspJson = new JSONObject();
        try {
            rspJson.put("latitude", 33.33);
            rspJson.put("longitude", 66.66);
        } catch (Exception e) {
            reportFail(callbackId);
            return;
        }
        reportSuccess(callbackId, rspJson.toString());
    }
}

```

同步调用 JSLocationPlugin.java

```

public class JSGetCachePlugin extends BaseJSPluginSync {
    @Override
    public String jsCallNative(String requestParams) {
        Toast.makeText(getContext(), "jsCallNative , requestParams = " + requestParams, Toast.LENGTH_LONG).show();
        try {
            JSONObject jsonObject1 = new JSONObject(requestParams);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("aaa", "hahahahah");
            return jsonObject.toString();
        } catch (Exception e) {
        }
        return null;
    }
}

```

