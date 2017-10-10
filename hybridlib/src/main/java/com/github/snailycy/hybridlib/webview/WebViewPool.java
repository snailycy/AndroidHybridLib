package com.github.snailycy.hybridlib.webview;

import android.content.Context;
import android.content.MutableContextWrapper;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ycy on 2017/10/9.
 */

public class WebViewPool {
    private static WebViewPool sInstance = null;
    private static List<WrapperWebView> sAvailable;
    private static List<WrapperWebView> sInUse;
    private int mPoolSize = 3;
    // 此context应该是applicationContext，生命周期和APP一致
    private Context mContext;

    private WebViewPool() {
        sAvailable = new ArrayList<>();
        sInUse = new ArrayList<>();
    }

    public static WebViewPool getInstance() {
        if (sInstance == null) {
            synchronized (WebViewPool.class) {
                if (sInstance == null) {
                    sInstance = new WebViewPool();
                }
            }
        }
        return sInstance;
    }

    public void setMaxSize(int maxSize) {
        mPoolSize = maxSize;
    }

    public void initWebViewPool(Context context) {
        this.mContext = context;
        for (int i = 0; i < mPoolSize; i++) {
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            // 引入Context中间层MutableContextWrapper
            WrapperWebView webView = new WrapperWebView(new MutableContextWrapper(mContext));
            webView.setLayoutParams(layoutParams);
            sAvailable.add(webView);
        }
    }

    public synchronized WrapperWebView getWebView() {
        WrapperWebView webView = null;
        if (sAvailable.size() > 0) {
            webView = sAvailable.get(0);
            sAvailable.remove(0);
        } else {
            // 无可用的webview时，自动扩容
            webView = new WrapperWebView(new MutableContextWrapper(mContext));
            ViewGroup.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT);
            webView.setLayoutParams(layoutParams);
        }
        sInUse.add(webView);
        webView.loadUrl("");
        return webView;
    }

    public synchronized void resetWebView(WrapperWebView webView) {
        ((MutableContextWrapper) webView.getContext()).setBaseContext(mContext);
        webView.reset();
        sInUse.remove(webView);
        if (sAvailable.size() < mPoolSize) {//保存个数不能大于池子的大小
            sAvailable.add(webView);
        } else { // 扩容出来的临时webview直接回收
            webView.destroy();
        }
    }

}
