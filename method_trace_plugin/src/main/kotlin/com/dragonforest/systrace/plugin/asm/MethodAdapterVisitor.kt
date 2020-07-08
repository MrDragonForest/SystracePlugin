package com.dragonforest.systrace.plugin.asm

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.commons.AdviceAdapter

/**
 *
 * create by DragonForest at 2020/7/3
 */
class MethodAdapterVisitor(
    api: Int,
    var methodVisitor: MethodVisitor?,
    access: Int,
    var methodName: String?,
    descriptor: String?,
    var clsName: String?
) : AdviceAdapter(api, methodVisitor, access, methodName, descriptor) {

    override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
        return super.visitAnnotation(descriptor, visible)
    }

    /**
     * 方法开始
     *
    L2
    LINENUMBER 13 L2
    LDC "test"
    INVOKESTATIC android/os/Trace.beginSection (Ljava/lang/String;)V

     */
    override fun onMethodEnter() {
//        super.onMethodEnter()
        if (filterInject(clsName, methodName)) {
            // 插入 Trace.beginSection()
            var sectionName = clsName + "_" + methodName
            if (sectionName.length > 80) {
                sectionName =
                    "..." + sectionName.substring(sectionName.length - 80, sectionName.length)
            }
            println("sectionName->$sectionName")
            methodVisitor?.visitLdcInsn(sectionName)
            methodVisitor?.visitMethodInsn(
                INVOKESTATIC,
                "android/os/Trace",
                "beginSection",
                "(Ljava/lang/String;)V",
                false
            )
        }
    }

    /**
     * 方法结束
     *
     * L4
    LINENUMBER 15 L4
    INVOKESTATIC android/os/Trace.endSection ()V
     */
    override fun onMethodExit(opcode: Int) {
//        super.onMethodExit(opcode)
        if (filterInject(clsName, methodName)) {
            // 插入Trace.endSection()
            methodVisitor?.visitMethodInsn(
                INVOKESTATIC,
                "android/os/Trace",
                "endSection",
                "()V",
                false
            );
        }
    }

    private fun filterInject(clsName: String?, methodName: String?): Boolean {
        if (clsName == null || methodName == null) return false
        return true
    }

}