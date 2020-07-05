package com.dragon.systrace.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * create by DragonForest at 2020/7/4
 */
class SystracePlugin:Plugin<Project> {
    override fun apply(project: Project) {
        println("SystracePlugin started")
        registerTransform(project)
    }

    private fun registerTransform(project: Project) {
        val app = project.extensions.getByType(AppExtension::class.java)
        app.registerTransform(MethodTraceTransform())
    }
}