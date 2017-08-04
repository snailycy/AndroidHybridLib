package com.github.snailycy.hybridlib.webview;

import android.util.Log;

import com.tencent.smtt.export.external.interfaces.ConsoleMessage;
import com.tencent.smtt.sdk.WebChromeClient;
import com.tencent.smtt.sdk.WebView;

/**
 * @author snailycy
 */

public class X5WebChromeClient extends WebChromeClient {
    private static final String TAG = "X5WebChromeClient";

    private WebChromeClientPresenter mWebChromeClientPresenter;

    public X5WebChromeClient(WrapperWebView webView) {
        mWebChromeClientPresenter = new WebChromeClientPresenter(webView);
    }

    @Override
    public void onReceivedTitle(WebView view, String title) {
        mWebChromeClientPresenter.onReceivedTitle(title);
        super.onReceivedTitle(view, title);
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage cm) {
        if (null != cm) {
            Log.i(TAG, cm.message() + " -- From line "
                    + cm.lineNumber() + " of "
                    + cm.sourceId());
        }
        return super.onConsoleMessage(cm);
    }

    @Override
    public void onProgressChanged(WebView view, int newProgress) {
        mWebChromeClientPresenter.onProgressChanged(newProgress);
        super.onProgressChanged(view, newProgress);
    }
}
