package com.github.snailycy.androidhybridlib;

import com.github.snailycy.hybridlib.bridge.BaseJSPlugin;

import org.json.JSONObject;

/**
 * 业务相关的jsApi
 *
 * @author snailycy
 */

public class JSLocationPlugin extends BaseJSPlugin {

    @Override
    public void jsCallNative(String callbackId, String requestParams) {
        // do something ...

        JSONObject rspJson = new JSONObject();
        try {
            rspJson.put("latitude", 33.33);
            rspJson.put("longitude", 66.66);
        } catch (Exception e) {
            reportFail(callbackId);
            return;
        }
        reportSuccess(callbackId, rspJson.toString());
    }
}
