package com.dragonforest.systrace.plugin.extensions

/**
 *
 * create by DragonForest at 2020/7/7
 */
open class MethodTraceConfig {
    var appPackageName: String? = null
    var extraTracePackages = arrayListOf<String>()
}