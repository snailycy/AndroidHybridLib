package com.github.snailycy.hybridlib.bridge;

/**
 * Created by ycy on 2017/9/27.
 */

public abstract class BaseJSPluginSync extends BaseJSPlugin {
    @Override
    public void jsCallNative(String callbackId, String requestParams) {
        // ignore
    }

    /**
     * 所有子类在此实现js同步调native业务逻辑
     *
     * @param requestParams
     */
    public abstract String jsCallNative(String requestParams);
}
