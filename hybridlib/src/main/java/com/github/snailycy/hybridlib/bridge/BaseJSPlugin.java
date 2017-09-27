package com.github.snailycy.hybridlib.bridge;

import android.content.Context;

import com.github.snailycy.hybridlib.webview.WrapperWebView;

/**
 * 所有jsPlugin应继承该类
 *
 * @author snailycy
 */

public abstract class BaseJSPlugin {

    private JSBridge mJSBridge;
    private String mCallbackId;
    private String mRequestParams;

    public BaseJSPlugin() {
    }

    public void setJSBridge(JSBridge jsBridge) {
        this.mJSBridge = jsBridge;
    }

    public void setCallbackId(String mCallbackId) {
        this.mCallbackId = mCallbackId;
    }

    public void setRequestParams(String mRequestParams) {
        this.mRequestParams = mRequestParams;
    }

    public JSBridge getJSBridge() {
        return mJSBridge;
    }

    public String getCallbackId() {
        return mCallbackId;
    }

    public String getRequestParams() {
        return mRequestParams;
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

    /**
     * 所有子类在此实现js调native业务逻辑，异步
     *
     * @param callbackId
     * @param requestParams
     */
    public abstract void jsCallNative(String callbackId, String requestParams);

}
