package com.github.snailycy.androidhybridlib;

import com.github.snailycy.hybridlib.util.HybridHandler;

import org.json.JSONObject;

/**
 * 业务相关的jsApi
 *
 * @author snailycy
 */

public class JSLocationPlugin extends HybridHandler {

    public void getLocation(String callbackId, JSONObject params) {
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
