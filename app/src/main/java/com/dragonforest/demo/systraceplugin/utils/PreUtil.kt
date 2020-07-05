package com.dragonforest.demo.systraceplugin.utils

import android.content.Context

/**
 *
 * create by DragonForest at 2020/7/5
 */
object PreUtil {

    val SP_NAME = "preUtil"
    fun getString1(context: Context, key: String): String? {
        var sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        return sp.getString(key, "")
    }

    fun putString1(context: Context, key: String, value: String) {
        var sp = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE)
        sp.edit().putString(key, value).commit()
    }
}