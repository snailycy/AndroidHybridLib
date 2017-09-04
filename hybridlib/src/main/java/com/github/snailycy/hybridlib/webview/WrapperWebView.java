package com.github.snailycy.hybridlib.webview;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.snailycy.hybridlib.R;
import com.github.snailycy.hybridlib.util.HybridConstant;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

/**
 * 包装WebView，带进度条
 *
 * @author snailycy
 */

public class WrapperWebView extends FrameLayout implements View.OnClickListener {
    private WebView mWebView;
    private ProgressBar mProgressBar;
    private TextView mTitleTV;
    private View mTopNavigationBar;

    public WrapperWebView(@NonNull Context context) {
        this(context, null);
    }

    public WrapperWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WrapperWebView(@NonNull Context context, @Nullable AttributeSet attrs, @AttrRes int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initWrapperWebView(context);
        initWebViewSettings();
    }

    private void initWrapperWebView(Context context) {
        if (!(context instanceof Activity)) {
            throw new RuntimeException("context should be Activity.");
        }
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_wrapper_webview, this, true);
        mWebView = (WebView) contentView.findViewById(R.id.hybrid_x5_webview);
        mProgressBar = (ProgressBar) contentView.findViewById(R.id.progress_bar);
        mTitleTV = (TextView) contentView.findViewById(R.id.tv_title);
        mTopNavigationBar = contentView.findViewById(R.id.rl_top_navigation_bar);
        contentView.findViewById(R.id.btn_close).setOnClickListener(this);
        contentView.findViewById(R.id.btn_refresh).setOnClickListener(this);
    }

    private void initWebViewSettings() {
        WebSettings ws = mWebView.getSettings();
        ws.setDefaultTextEncodingName("utf-8");
        ws.setJavaScriptEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setRenderPriority(com.tencent.smtt.sdk.WebSettings.RenderPriority.HIGH);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setAppCacheEnabled(false);
        ws.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_NO_CACHE);
        ws.setSaveFormData(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ws.setAllowFileAccessFromFileURLs(true);
            ws.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * 设置UserAgent
     *
     * @param isWhiteList
     * @param appId
     * @param versionCode
     * @param language
     */
    public void setUserAgent(boolean isWhiteList, String appId, int versionCode, String language) {
        if (isWhiteList) {
            //  restapp.client.android.5637 2dfire/zh_CN
            WebSettings ws = mWebView.getSettings();
            StringBuilder uaSB = new StringBuilder();
            uaSB.append(ws.getUserAgentString());
            uaSB.append(appId);
            uaSB.append(".");
            uaSB.append(versionCode);
            uaSB.append(HybridConstant.USER_AGENT_LAN);
            uaSB.append(language);
            ws.setUserAgentString(uaSB.toString());
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btn_close) {
            ((Activity) getContext()).finish();
        } else if (view.getId() == R.id.btn_refresh) {
            reload();
        }
    }

    /**
     * 设置网页标题
     *
     * @param title
     */
    public void setTitle(String title) {
        if (!TextUtils.isEmpty(title) && !title.contains(".com")) {
            mTitleTV.setText(title);
        }
    }

    /**
     * 设置进度
     *
     * @param progress
     */
    public void setProgress(int progress) {
        mProgressBar.setProgress(progress);
    }

    public ProgressBar getProgressBar() {
        return mProgressBar;
    }

    /**
     * 是否显示顶部导航栏，默认显示
     *
     * @param isShow
     */
    public void showTopNavigationBar(boolean isShow) {
        mTopNavigationBar.setVisibility(isShow ? VISIBLE : GONE);
    }

    /**
     * ================================================
     * =============== 包装WebView接口 ================
     * ================================================
     */
    public void loadUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            mWebView.loadUrl(url);
        }
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        mWebView.setWebViewClient(webViewClient);
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        mWebView.setWebChromeClient(webChromeClient);
    }

    public boolean canGoBack() {
        return mWebView.canGoBack();
    }

    public void goBack() {
        mWebView.goBack();
    }

    public boolean canGoForward() {
        return mWebView.canGoForward();
    }

    public boolean canGoBackOrForward(int i) {
        return mWebView.canGoBackOrForward(i);
    }

    public void goForward() {
        mWebView.goForward();
    }

    public void goBackOrForward(int i) {
        mWebView.goBackOrForward(i);
    }

    public void addJavascriptInterface(Object o, String s) {
        mWebView.addJavascriptInterface(o, s);
    }

    public void reload() {
        mWebView.reload();
    }

    public void removeAllViews() {
        mWebView.removeAllViews();
    }

    public void clearCache(boolean b) {
        mWebView.clearCache(b);
    }

    public void clearHistory() {
        mWebView.clearHistory();
    }

    public void clearSslPreferences() {
        mWebView.clearSslPreferences();
    }

    public void evaluateJavascript(String s, ValueCallback valueCallback) {
        mWebView.evaluateJavascript(s, valueCallback);
    }

    public String getOriginalUrl() {
        return mWebView.getOriginalUrl();
    }

    public void destroy() {
        mWebView.destroy();
    }
}
