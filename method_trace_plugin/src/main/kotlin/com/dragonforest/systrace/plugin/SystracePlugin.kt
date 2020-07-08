package com.dragonforest.systrace.plugin

import com.android.build.gradle.AppExtension
import com.android.build.gradle.AppPlugin
import com.dragonforest.systrace.plugin.asm.AsmUtil
import com.dragonforest.systrace.plugin.extensions.MethodTraceConfig
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 *
 * create by DragonForest at 2020/7/4
 */
class SystracePlugin : Plugin<Project> {
    override fun apply(project: Project) {
        println("SystracePlugin started")
        registerTransform(project)
        project.extensions.create("methodTraceConfig", MethodTraceConfig::class.java)
        project.afterEvaluate {
            AsmUtil.extraPackages = (project.extensions.getByName("methodTraceConfig") as MethodTraceConfig).extraTracePackages
            AsmUtil.appPackageName = (project.extensions.getByName("methodTraceConfig") as MethodTraceConfig).appPackageName
            println("get extraTracePackages-> " + AsmUtil.extraPackages)
            if (project.plugins.hasPlugin(AppPlugin::class.java)) {
                println("buildDir:"+project.buildDir)
            }
        }
        project.tasks.findByPath("clean")?.doLast {
            AsmUtil.clearRecords()
        }
    }

    private fun registerTransform(project: Project) {
        val app = project.extensions.getByType(AppExtension::class.java)
        app.registerTransform(MethodTraceTransform())
    }
}