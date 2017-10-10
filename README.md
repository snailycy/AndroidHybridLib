# AndroidHybridLib

JSBridge提供了Hybrid同步和异步调用。

实现WebView复用池，减少WebView初始化消耗的时间。

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
	        compile 'com.github.snailycy:AndroidHybridLib:x.x.x'
	}

```

### How to use Hybrid

For example:

MainActivity.java

```
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

