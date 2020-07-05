package com.dragonforest.demo.app_java.util;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * create by DragonForest at 2020/7/5
 */
public class PreUtil {
    private static final String SP_NAME = "preUtil";
    public static final PreUtil INSTANCE;

    public final String getSP_NAME() {
        return SP_NAME;
    }

    public final String getString( Context context,  String key) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        return sp.getString(key, "");
    }

    public final void putString( Context context,  String key,  String value) {
        SharedPreferences sp = context.getSharedPreferences(SP_NAME, 0);
        sp.edit().putString(key, value);
    }

    private PreUtil() {
    }

    static {
        PreUtil var0 = new PreUtil();
        INSTANCE = var0;
    }
}
