package com.github.snailycy.hybridlib.bridge;

import android.os.Build;
import android.text.TextUtils;
import android.webkit.JavascriptInterface;

import com.github.snailycy.hybridlib.util.HybridConstant;
import com.github.snailycy.hybridlib.webview.WrapperWebView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author snailycy
 */

public class JSBridge {
    /**
     * jsApi容器，key：js调用的方法名 value：相应的jsApi字节码
     */
    private Map<String, Class> jsApiMap;
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
     * 注册jsapi，所有hybrid交互必须注册jsapi
     *
     * @param jsFunction
     * @param javaCls
     */
    public void registerJSApi(String jsFunction, Class javaCls) {
        if (jsApiMap == null) {
            jsApiMap = new LinkedHashMap<>();
        }
        if (!TextUtils.isEmpty(jsFunction) && javaCls != null) {
            jsApiMap.put(jsFunction, javaCls);
        } else {
            throw new UnsupportedOperationException("jsFunction or javaCls is not allowed to be empty.");
        }
    }

    /**
     * 分发js请求
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
                Class cls = jsApiMap.get(functionName);
                try {
                    if (cls != null) {
                        Object instance = cls.newInstance();
                        // call setJSBridge
                        Method setJSBridgeMethod = cls.getSuperclass().getDeclaredMethod("setJSBridge", JSBridge.class);
                        setJSBridgeMethod.invoke(instance, JSBridge.this);

                        // call jsApi
                        Method jsApiMethod = cls.getDeclaredMethod(functionName, String.class,
                                JSONObject.class);
                        JSONObject requestParams;
                        try {
                            requestParams = new JSONObject(params);
                        } catch (JSONException e) {
                            requestParams = new JSONObject();
                        }
                        jsApiMethod.invoke(instance, callbackId, requestParams);
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
     * js call android接口：各业务统一从该入口分发
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
     * js call android接口：js报错统一从该入口分发
     *
     * @param callbackId
     */
    @JavascriptInterface
    public void hybridJSError(String callbackId) {

    }


}
