package com.github.snailycy.hybridlib.util;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.tencent.smtt.sdk.CookieManager;
import com.tencent.smtt.sdk.CookieSyncManager;
import com.tencent.smtt.sdk.ValueCallback;
import com.tencent.smtt.sdk.WebView;

import java.util.List;
import java.util.Map;

public class CookieUtils {

    /**
     * 设置cookie
     */
    public static void configCookie(final Context context, final WebView webView, final List<String> domainList, final Map<String, String> cookieMap) {
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        if (Build.VERSION.SDK_INT < 21) {
            cookieManager.removeAllCookie();
            for (String hybridDomain : domainList) {
                synCookies(context, webView, hybridDomain, cookieMap);
            }
        } else {
            cookieManager.removeAllCookies(new ValueCallback<Boolean>() {
                @Override
                public void onReceiveValue(Boolean aBoolean) {
                    for (String hybridDomain : domainList) {
                        synCookies(context, webView, hybridDomain, cookieMap);
                    }
                }
            });
        }
    }

    private static void synCookies(Context context, WebView webView, String domain, Map<String, String> cookieMap) {
        if (cookieMap == null || TextUtils.isEmpty(domain)) {
            return;
        }
        CookieManager cookieManager = CookieManager.getInstance();
        cookieManager.setAcceptCookie(true);
        for (Map.Entry<String, String> entry : cookieMap.entrySet()) {
            String cookie = entry.getKey() + "=" + entry.getValue();
            cookieManager.setCookie(domain, cookie);
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            cookieManager.setAcceptThirdPartyCookies(webView, true);
            cookieManager.flush();
        } else {
            CookieSyncManager.createInstance(context);
            CookieSyncManager.getInstance().sync();
        }
    }
}
