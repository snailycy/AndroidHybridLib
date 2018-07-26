package com.github.snailycy.androidhybridlib;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import com.github.snailycy.hybridlib.webview.IWebViewClient;
import com.tencent.smtt.sdk.WebView;

import java.lang.ref.SoftReference;

/**
 * Created by ycy on 2017/9/30.
 */

public class MyWebViewClient implements IWebViewClient {

    private SoftReference<Activity> mActivitySoftReference;

    public MyWebViewClient(Activity activity) {
        mActivitySoftReference = new SoftReference<Activity>(activity);
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        // 拦截拨打电话操作
        Uri uri = Uri.parse(url);
        if (null == uri) {
            return false;
        }
        if (TextUtils.equals("tel", uri.getScheme())) {
            // Scheme Tel 拨打电话
            return interruptTel(uri);
        }

        return false;
    }

    private boolean interruptTel(Uri uri) {
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Activity activity = mActivitySoftReference.get();
        if (activity != null) {
            activity.startActivity(intent);
        }
        return true;
    }
}
