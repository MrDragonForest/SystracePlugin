package com.dragon.systrace.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.dragon.systrace.plugin.asm.AsmUtil
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.apache.http.util.TextUtils
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry


/**
 *
 * create by DragonForest at 2020/7/3
 */
class MethodTraceTransform : Transform() {
    /**
     * Returns the unique name of the transform.
     *
     *
     * This is associated with the type of work that the transform does. It does not have to be
     * unique per variant.
     */
    override fun getName(): String {
        return this.javaClass.simpleName
    }

    /**
     * Returns the type(s) of data that is consumed by the Transform. This may be more than
     * one type.
     *
     * **This must be of type [QualifiedContent.DefaultContentType]**
     */
    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    /**
     * Returns whether the Transform can perform incremental work.
     *
     *
     * If it does, then the TransformInput may contain a list of changed/removed/added files, unless
     * something else triggers a non incremental run.
     */
    override fun isIncremental(): Boolean {
        return true
    }

    /**
     * Returns the scope(s) of the Transform. This indicates which scopes the transform consumes.
     */
    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        //消费型输入，可以从中获取jar和class的文件路径，需要输出给下一个任务
        var inputs = transformInvocation?.inputs
        //引用型输入，无需输出
        var refrenceInputs = transformInvocation?.referencedInputs
        //管理输出路径，如果inputs为空，此值也为空
        val outputProvider = transformInvocation?.outputProvider
        //是否是增量编译
        val incremental = transformInvocation?.isIncremental

        AsmUtil.clearRecords()
        /*
            进行读取class和jar, 并做处理
         */
        for (input in inputs!!) {
            // 处理class
            val directoryInputs =
                input.directoryInputs
            for (directoryInput in directoryInputs) {
                // 目标file
                val dstFile: File = outputProvider!!.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                // 执行转化整个目录
                transformDir(directoryInput.file, dstFile)
            }
            // 处理jar
            val jarInputs = input.jarInputs
            for (jarInput in jarInputs) {
                val jarPath = jarInput.file.absolutePath
                val dstFile: File = outputProvider!!.getContentLocation(
                    jarInput.file.absolutePath,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                transformJar(jarInput.file, dstFile)
            }
        }
    }

    private fun transformDir(inputDir: File, dstDir: File) {
        try {
            if (dstDir.exists()) {
                FileUtils.forceDelete(dstDir)
            }
            FileUtils.forceMkdir(dstDir)
        } catch (e: IOException) {
            e.printStackTrace()
        }
        val inputDirPath = inputDir.absolutePath
        val dstDirPath = dstDir.absolutePath
        val files = inputDir.listFiles()
        for (file in files) {
            var dstFilePath = file.absolutePath
            dstFilePath = dstFilePath.replace(inputDirPath, dstDirPath)
            val dstFile = File(dstFilePath)
            if (file.isDirectory) {
                // 递归
                transformDir(file, dstFile)
            } else if (file.isFile) {
                // 转化单个class文件
                transformSingleFile(file, dstFile)
            }
        }
    }

    /**
     * 转化jar
     * 对jar暂不做处理，所以直接拷贝
     * @param inputJarFile
     * @param dstFile
     */
    private fun transformJar(inputJarFile: File, dstFile: File): Unit {
        //未配置额外插桩包直接拷贝jar包，减少编译时间
        if (AsmUtil.extraPackages.size == 0) {
            FileUtils.copyFile(inputJarFile, dstFile)
            return
        }
        try {
            val jarFile = JarFile(inputJarFile)
            val tempJarFile =
                File(inputJarFile.parent + File.separator + "classes_tmp.jar")
            if (tempJarFile.exists()) {
                FileUtils.forceDelete(tempJarFile)
            }
            val jarOutputStream = JarOutputStream(FileOutputStream(tempJarFile))
            val jarEntries: Enumeration<JarEntry> = jarFile.entries()
            // 遍历jar包中的.class文件
            while (jarEntries.hasMoreElements()) {
                val jarEntry: JarEntry = jarEntries.nextElement()
                val jarEntryName: String = jarEntry.getName()
                val zipEntry = ZipEntry(jarEntryName)
                val jarEntryInputStream: InputStream = jarFile.getInputStream(jarEntry)
                println("jarEntryName->$jarEntryName")
                if (checkJarEntry(jarEntryName)) {
                    println("checkJarEntry hit->$jarEntryName")
                    var clsName = jarEntry2ClassName(jarEntryName)
                    println("className ->$clsName")
                    // 进行插桩
                    // 修改原有class 重新写入
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(
                        AsmUtil.getClassByte(
                            jarEntryInputStream,
                            clsName
                        )
                    )
                } else {
                    // 不进行操作 原封写入
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(jarEntryInputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            FileUtils.copyFile(tempJarFile, dstFile)
            FileUtils.forceDelete(tempJarFile)
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    /**
     * 转化class文件
     * 注意：
     * 这里只对InjectTest.class进行插桩，但是对于其他class要原封不动的拷贝过去，不然结果中就会缺少class
     * @param inputFile
     * @param dstFile
     */
    private fun transformSingleFile(
        inputFile: File,
        dstFile: File
    ) {
        println("singleFileName->${inputFile.name},path->${inputFile.absolutePath}")
        if (checkClassFile(inputFile)) {
            println("checkClassFile hit->${inputFile.name}")
//            var clsName = singleClassFile2ClassName(inputFile.absolutePath)
            var clsName = inputFile.name
            println("className ->$clsName")
            AsmUtil.inject(inputFile, dstFile, clsName)
        } else {
            FileUtils.copyFile(inputFile, dstFile)
        }
    }

    private fun checkClassFile(inputFile: File): Boolean {
        var fname = inputFile.name
        if (!fname.endsWith(".class")
            || fname == "R.class"
            || fname.startsWith("R\$")
            || fname == "BuildConfig.class"
            || fname.contains("android/")
        ) {
            return false
        }
        return true
    }

    private fun checkJarEntry(entryName: String?): Boolean {
        if (TextUtils.isEmpty(entryName)) return false
        var extraPackages = AsmUtil.extraPackages
        if (extraPackages.size == 0) return false
        extraPackages.forEach {
            if (entryName?.startsWith(it) == true && entryName.endsWith(".class")) {
                return true
            }
        }
        return false
    }

    private fun jarEntry2ClassName(entryName: String): String {
        var clsName = entryName.replace("/", ".")
        return clsName
    }

    private fun singleClassFile2ClassName(filePath: String): String {
        var TEMPELE_STR = AsmUtil.appPackageName ?: ""
        var index = filePath.indexOf(TEMPELE_STR)
        var classPath = filePath
        if (index != -1) {
            classPath = filePath.substring(index)
        }
        var clsName = classPath.replace("\\", ".")
        return clsName
    }
}