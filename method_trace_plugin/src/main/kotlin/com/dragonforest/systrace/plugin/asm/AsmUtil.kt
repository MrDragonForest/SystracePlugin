package com.dragonforest.systrace.plugin.asm

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.*

/**
 *
 * create by DragonForest at 2020/7/3
 */
class AsmUtil {
    companion object {
        var extraPackages = arrayListOf<String>()
        var appPackageName: String? = null

        /**
         * 使用ASM 向class中的方法插入记录代码
         */
        @JvmStatic
        fun inject(srcFile: File?, dstFile: File?, className: String) {
            var fis: FileInputStream? = null
            var fos: FileOutputStream? = null
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
                writeToFile(SUCCESS_FILE, srcFile?.absolutePath)
            } catch (e: Exception) {
                e.printStackTrace()
                writeToFile(FAIL_FILE, srcFile?.absolutePath + "\n" + e.message)
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
                writeToFile(EXTRA_SUCCESS_FILE, className)
                return cw.toByteArray()
            } catch (e: java.lang.Exception) {
                writeToFile(EXTRA_FAIL_FILE, className + "\n" + e.message)
                e.printStackTrace()
            }
            return IOUtils.toByteArray(inputStream)
        }

        fun clearRecords() {
            var dir = File(TRACE_DIR)
            if (dir.exists()) {
                FileUtils.forceDelete(dir)
            }
        }

        val TRACE_DIR = ".methodtrace"
        val SUCCESS_FILE = "success_trace.txt"
        val FAIL_FILE = "fail_trace.txt"
        val EXTRA_SUCCESS_FILE = "extra_success_trace.txt"
        val EXTRA_FAIL_FILE = "extra_fail_trace.txt"

        private fun writeToFile(fileName: String, content: String?) {
            if (!File(TRACE_DIR).exists()) {
                File(TRACE_DIR).mkdir()
            }
            content?.let {
                var fw: FileWriter? = null
                var pw: PrintWriter? = null
                try {
                    var file = File(TRACE_DIR + File.separator + fileName)
                    fw = FileWriter(file, true)
                    pw = PrintWriter(fw)
                    pw.println(content)
                    pw.flush()
                } catch (e: java.lang.Exception) {
                    e.printStackTrace();
                } finally {
                    pw?.close()
                    fw?.close()
                }
            }
        }
    }
}