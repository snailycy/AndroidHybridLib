package com.github.snailycy.androidhybridlib;

import android.widget.Toast;

import com.github.snailycy.hybridlib.bridge.BaseJSPluginSync;

import org.json.JSONObject;

/**
 * Created by ycy on 2017/9/27.
 */

public class JSGetCachePlugin extends BaseJSPluginSync {
    @Override
    public String jsCallNative(String requestParams) {
        Toast.makeText(getContext(), "jsCallNative , requestParams = " + requestParams, Toast.LENGTH_LONG).show();
        try {
            JSONObject jsonObject1 = new JSONObject(requestParams);
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("aaa", "hahahahah");
            return jsonObject.toString();
        } catch (Exception e) {
        }
        return null;
    }
}
