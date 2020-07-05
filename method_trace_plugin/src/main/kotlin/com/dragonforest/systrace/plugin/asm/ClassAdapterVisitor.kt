package com.dragonforest.systrace.plugin.asm

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes;


/**
 *
 * create by DragonForest at 2020/7/3
 */
class ClassAdapterVisitor : ClassVisitor {
    var clsName: String? = null

    constructor(classVisitor: ClassVisitor, clsName: String?
    ) : super(Opcodes.ASM5, classVisitor) {
        this.clsName = clsName
    }

    override fun visitMethod(
            access: Int,
            name: String,
            descriptor: String?,
            signature: String?,
            exceptions: Array<String?>?
    ): MethodVisitor? {
        val methodVisitor: MethodVisitor =
                super.visitMethod(access, name, descriptor, signature, exceptions)
        return MethodAdapterVisitor(api, methodVisitor, access, name, descriptor,clsName)
    }
}