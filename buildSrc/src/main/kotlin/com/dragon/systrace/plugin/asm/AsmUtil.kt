package com.dragon.systrace.plugin.asm

import org.apache.commons.io.FileUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.*

/**
 *
 * create by DragonForest at 2020/7/3
 */
class AsmUtil {
    companion object {
        /**
         * 使用ASM 向class中的方法插入记录代码
         */
        @JvmStatic
        fun inject(srcFile: File?, dstFile: File?) {
            var fis: FileInputStream? = null
            var fos: FileOutputStream? = null
            var className = srcFile?.name?.substring(0, srcFile?.name?.indexOf(".") ?: 0)
            try {
                /*
                1. 准备待插桩的class
             */
                fis = FileInputStream(srcFile)
                /*
                2. 执行分析与插桩
             */
                // 字节码的读取与分析引擎
                val cr = ClassReader(fis)
                // 字节码写出器，COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
                val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)
                // 分析，处理结果写入cw EXPAND_FRAMES:栈图以扩展形式进行访问
                cr.accept(ClassAdapterVisitor(cw, className), ClassReader.EXPAND_FRAMES)

                /*
                3.获得新的class字节码并写出
             */
                val newClassBytes: ByteArray = cw.toByteArray()
                fos = FileOutputStream(dstFile)
                fos.write(newClassBytes)
                fos.flush()
                println("AsmUtil inject success")
            } catch (e: Exception) {
                e.printStackTrace()
                println("AsmUtil inject fail:" + e.message)
                FileUtils.copyFile(srcFile, dstFile)
            } finally {
                try {
                    if (fis != null) fis.close()
                    if (fos != null) fos.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun getClassByte(inputStream: InputStream, className: String): ByteArray? {
            try {
                /*
                2. 执行分析与插桩
             */
                // 字节码的读取与分析引擎
                val cr = ClassReader(inputStream)
                // 字节码写出器，COMPUTE_FRAMES 自动计算所有的内容，后续操作更简单
                val cw =
                    ClassWriter(cr, ClassWriter.COMPUTE_MAXS)
                // 分析，处理结果写入cw EXPAND_FRAMES:栈图以扩展形式进行访问
                cr.accept(ClassAdapterVisitor(cw, className), ClassReader.EXPAND_FRAMES)
                /*
                3.获得新的class字节码并写出
                */
                return cw.toByteArray()
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
                println("执行字节码插桩失败！" + e.message)
            }
            return null
        }
    }
}