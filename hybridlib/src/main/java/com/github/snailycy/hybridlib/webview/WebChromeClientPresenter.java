package com.github.snailycy.hybridlib.webview;

import android.widget.ProgressBar;

import com.github.snailycy.hybridlib.util.AnimationUtils;

/**
 * WebChromeClient逻辑处理
 */

public class WebChromeClientPresenter {

    private WrapperWebView mWebView;
    private ProgressBar mProgressBar;

    public WebChromeClientPresenter(WrapperWebView webView) {
        this.mWebView = webView;
        this.mProgressBar = webView.getProgressBar();
    }

    public void onReceivedTitle(String title) {
        // 设置标题
        mWebView.setTitle(title);
    }

    public void onProgressChanged(int newProgress) {
        int currentProgress = mProgressBar.getProgress();
        if (newProgress >= 100) {
            mProgressBar.setProgress(newProgress);
            // 开启属性动画让进度条平滑消失
            AnimationUtils.startDismissAnimation(mProgressBar, mProgressBar.getProgress());
        } else {
            // 开启属性动画让进度条平滑递增
            AnimationUtils.startProgressAnimation(mProgressBar, currentProgress, newProgress);
        }
    }
}
