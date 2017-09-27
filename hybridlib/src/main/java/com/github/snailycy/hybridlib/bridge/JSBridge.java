package com.github.snailycy.hybridlib.bridge;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.github.snailycy.hybridlib.util.HybridConstant;
import com.github.snailycy.hybridlib.webview.WrapperWebView;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author snailycy
 */

public class JSBridge {
    /**
     * jsPlugin容器，key：js调用的方法名 value：相应的jsPlugin
     */
    private Map<String, BaseJSPlugin> jsPluginMap;
    private WrapperWebView mWebView;

    public JSBridge(WrapperWebView wrapperWebView) {
        mWebView = wrapperWebView;
        wrapperWebView.addJavascriptInterface(this, HybridConstant.HYBRID_BRIDGE_NAME);
    }

    /**
     * 获取WebView
     *
     * @return
     */
    public WrapperWebView getWebView() {
        return mWebView;
    }

    /**
     * 注册jsPlugin，所有hybrid交互必须先注册jsPlugin
     *
     * @param jsFunction
     * @param jsPlugin
     */
    public void registerJSPlugin(String jsFunction, BaseJSPlugin jsPlugin) {
        if (jsPluginMap == null) {
            jsPluginMap = new LinkedHashMap<>();
        }
        if (!TextUtils.isEmpty(jsFunction) && jsPlugin != null) {
            jsPluginMap.put(jsFunction, jsPlugin);
        } else {
            throw new UnsupportedOperationException("jsFunction or jsPlugin is not allowed to be empty.");
        }
    }

    /**
     * 获取jsPlugin
     *
     * @param jsFunction
     * @return
     */
    public BaseJSPlugin getJSPlugin(String jsFunction) {
        return jsPluginMap == null ? null : jsPluginMap.get(jsFunction);
    }

    /**
     * 分发js请求，异步
     *
     * @param functionName
     * @param callbackId
     * @param params
     */
    private void dispatchJSRequest(final String functionName, final String callbackId, final String params) {
        //run in ui-thread
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                BaseJSPlugin jsPlugin = jsPluginMap.get(functionName);
                try {
                    if (jsPlugin != null) {
                        jsPlugin.setCallbackId(callbackId);
                        jsPlugin.setRequestParams(params);
                        jsPlugin.setJSBridge(JSBridge.this);
                        jsPlugin.jsCallNative(callbackId, params);
                    } else {
                        callbackJS(callbackId, JSCallbackType.FAIL, null);
                    }
                } catch (Exception e) {
                    callbackJS(callbackId, JSCallbackType.FAIL, null);
                }
            }
        });
    }

    /**
     * android call js ：android调用js统一调用此方法
     *
     * @param callbackId
     * @param callbackFunctionName
     * @param params
     */
    public void callbackJS(final String callbackId, final JSCallbackType callbackFunctionName, final String params) {
        if (null == mWebView) {
            return;
        }

        final StringBuilder jsSB = new StringBuilder();
        jsSB.append("FRWCardApp.callBackFromNative('");
        jsSB.append(callbackId);
        jsSB.append("','");
        jsSB.append(callbackFunctionName.getValue());
        jsSB.append(TextUtils.isEmpty(params) ? "')" : ("','" + params + "')"));

        //run in ui-thread
        mWebView.post(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    mWebView.evaluateJavascript(jsSB.toString(), new com.tencent.smtt.sdk.ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String s) {
                            //ignore
                        }
                    });
                } else {
                    mWebView.loadUrl("javascript:" + jsSB.toString());
                }
            }
        });
    }

    /**
     * js call android接口，异步：各业务统一从该入口分发
     *
     * @param functionName
     * @param callbackId
     * @param params
     */
    @JavascriptInterface
    public void messageSend(String functionName, final String callbackId, String params) {
        //分发js请求
        dispatchJSRequest(functionName, callbackId, params);
    }

    /**
     * js call android接口，同步
     *
     * @param functionName
     * @param params
     */
    @JavascriptInterface
    public String syndMessageSend(String functionName, String params) {
        //分发js请求
        return dispatchJSRequest(functionName, params);
    }

    /**
     * 分发同步请求
     *
     * @param functionName
     * @param params
     * @return
     */
    private String dispatchJSRequest(String functionName, String params) {
        BaseJSPlugin jsPlugin = jsPluginMap.get(functionName);
        if (jsPlugin != null && (jsPlugin instanceof BaseJSPluginSync)) {
            BaseJSPluginSync jsPluginSync = (BaseJSPluginSync) jsPlugin;
            jsPluginSync.setRequestParams(params);
            jsPluginSync.setJSBridge(JSBridge.this);
            return jsPluginSync.jsCallNative(params);
        }
        return null;
    }

    /**
     * js call android接口：js报错统一从该入口分发
     *
     * @param callbackId
     */
    @JavascriptInterface
    public void hybridJSError(String callbackId) {

    }


}
