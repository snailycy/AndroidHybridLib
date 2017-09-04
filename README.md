# AndroidHybridLib

集成了腾讯x5内核，crash率低于0.06%，X5内核基于统一Blink内核，无缝隐藏系统差异，在所有Android手机平台表现一致

### gradle集成

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
	        compile 'com.github.snailycy:AndroidHybridLib:1.0.3'
	}

```

### How to use Hybrid

For example:

MainActivity.java

```
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

```

JSLocationPlugin.java

```

public class JSLocationPlugin extends HybridHandler {

    public void getLocation(String callbackId, JSONObject params) {
        // do something ...

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


activity_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    tools:context="com.github.snailycy.androidhybridlib.MainActivity">

    <com.github.snailycy.hybridlib.webview.WrapperWebView
        android:id="@+id/webview"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

</android.support.constraint.ConstraintLayout>

```
