package com.github.snailycy.hybridlib.util;

import android.content.Context;

import com.github.snailycy.hybridlib.bridge.JSBridge;
import com.github.snailycy.hybridlib.bridge.JSCallbackType;
import com.github.snailycy.hybridlib.webview.WrapperWebView;

/**
 * 所有jsApi应继承该类
 *
 * @author snailycy
 */

public class HybridHandler {

    private JSBridge mJSBridge;

    public HybridHandler() {
    }

    public void setJSBridge(JSBridge jsBridge) {
        this.mJSBridge = jsBridge;
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WrapperWebView getWebView() {
        if (mJSBridge != null) {
            return mJSBridge.getWebView();
        }
        return null;
    }

    /**
     * 获取Context
     *
     * @return
     */
    public Context getContext() {
        WrapperWebView webView = getWebView();
        if (webView != null) {
            return webView.getContext();
        }
        return null;
    }

    /**
     * 报告JS成功
     *
     * @param callbackId
     */
    public void reportSuccess(String callbackId) {
        reportSuccess(callbackId, null);
    }

    /**
     * 报告JS成功
     *
     * @param callbackId
     * @param params
     */
    public void reportSuccess(String callbackId, String params) {
        mJSBridge.callbackJS(callbackId, JSCallbackType.SUCCESS, params);
    }

    /**
     * 报告JS错误
     *
     * @param callbackId
     */
    public void reportFail(String callbackId) {
        reportFail(callbackId, null);
    }

    /**
     * 报告JS错误
     *
     * @param callbackId
     * @param params
     */
    public void reportFail(String callbackId, String params) {
        mJSBridge.callbackJS(callbackId, JSCallbackType.FAIL, params);
    }

    /**
     * 报告JS取消
     *
     * @param callbackId
     */
    public void reportCancel(String callbackId) {
        reportCancel(callbackId, null);
    }

    /**
     * 报告JS取消
     *
     * @param callbackId
     * @param params
     */
    public void reportCancel(String callbackId, String params) {
        mJSBridge.callbackJS(callbackId, JSCallbackType.CANCEL, params);
    }

    /**
     * 报告JS完成
     *
     * @param callbackId
     */
    public void reportCompletion(String callbackId) {
        reportCompletion(callbackId, null);
    }

    /**
     * 报告JS完成
     *
     * @param callbackId
     * @param params
     */
    public void reportCompletion(String callbackId, String params) {
        mJSBridge.callbackJS(callbackId, JSCallbackType.COMPLETION, params);
    }
}
