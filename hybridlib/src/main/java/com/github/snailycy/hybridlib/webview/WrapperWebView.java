package com.github.snailycy.hybridlib.webview;

import android.app.Activity;
import android.content.Context;
import android.content.MutableContextWrapper;
import android.os.Build;
import android.support.annotation.AttrRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.snailycy.hybridlib.R;
import com.github.snailycy.hybridlib.util.HybridConstant;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;

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
    /**
     * 是否为白名单
     */
    private boolean mIsWhiteList;
    private X5WebViewClient mX5WebViewClient;

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
        initWebViewClient();
    }

    public void setIsWhiteList(boolean isWhiteList) {
        this.mIsWhiteList = isWhiteList;
        mX5WebViewClient.setIsWhiteList(mIsWhiteList);
    }

    private void initWebViewClient() {
        X5WebChromeClient x5WebChromeClient = new X5WebChromeClient(this);
        mWebView.setWebChromeClient(x5WebChromeClient);
        mX5WebViewClient = new X5WebViewClient(this);
        mWebView.setWebViewClient(mX5WebViewClient);
    }

    private void initWrapperWebView(Context context) {
        View contentView = LayoutInflater.from(context).inflate(R.layout.layout_wrapper_webview, this, true);
        mWebView = new WebView(context);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
        mWebView.setLayoutParams(layoutParams);
        FrameLayout webviewContainer = (FrameLayout) contentView.findViewById(R.id.fl_webview_container);
        webviewContainer.addView(mWebView, 0);
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
        ws.setPluginsEnabled(true);
        ws.setDomStorageEnabled(true);
        ws.setRenderPriority(com.tencent.smtt.sdk.WebSettings.RenderPriority.HIGH);
        ws.setAllowFileAccess(true);
        ws.setAllowContentAccess(true);
        ws.setAppCacheEnabled(false);
        ws.setCacheMode(com.tencent.smtt.sdk.WebSettings.LOAD_NO_CACHE);
        ws.setSaveFormData(true);
        ws.setJavaScriptCanOpenWindowsAutomatically(true);
        ws.setLoadsImagesAutomatically(true);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            ws.setAllowFileAccessFromFileURLs(true);
            ws.setAllowUniversalAccessFromFileURLs(true);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mWebView.setWebContentsDebuggingEnabled(true);
        }
    }

    /**
     * 设置UserAgent
     *
     * @param appId
     * @param versionCode
     * @param language
     */
    public void setUserAgent(String appId, int versionCode, String language) {
        if (mIsWhiteList) {
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
            if (getContext() instanceof MutableContextWrapper) {
                Context baseContext = ((MutableContextWrapper) getContext()).getBaseContext();
                if (baseContext instanceof Activity) {
                    ((Activity) baseContext).finish();
                }
            } else if (getContext() instanceof Activity) {
                ((Activity) getContext()).finish();
            }
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

    public void setWebViewClient(IWebViewClient webViewClient) {
        mX5WebViewClient.setBizWebViewClient(webViewClient);
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

    public void loadDataWithBaseURL(String baseUrl, String data, String mimeType, String encoding, String historyUrl) {
        mWebView.loadDataWithBaseURL(baseUrl, data, mimeType, encoding, historyUrl);
    }

    /**
     * 绑定新的Context
     *
     * @param context
     */
    public void bindNewContext(Context context) {
        if (getContext() instanceof MutableContextWrapper) {
            ((MutableContextWrapper) getContext()).setBaseContext(context);
        }
    }

    /**
     * 重置WebView
     */
    public void reset() {
        if (mWebView != null) {
            mWebView.stopLoading();
            mWebView.clearCache(true);
            mWebView.clearHistory();
        }
    }

    /**
     * 销毁WebView
     */
    public void destroy() {
        try {
            if (mWebView != null) {
                mWebView.stopLoading();
                mWebView.clearCache(true);
                mWebView.clearHistory();
                ViewParent parent = mWebView.getParent();
                if (parent != null) {
                    ((ViewGroup) parent).removeView(mWebView);
                }
                mWebView.removeAllViews();
                mWebView.destroy();
                mWebView = null;
            }
        } catch (Exception e) {
            // ignore
        }
    }

}
