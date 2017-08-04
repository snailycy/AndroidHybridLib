package com.github.snailycy.hybridlib.bridge;

/**
 * @author snailycy
 */
public enum JSCallbackType {
    SUCCESS("success"), FAIL("fail") , CANCEL("cancel"), COMPLETION("completion");

    private String value;

    JSCallbackType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}