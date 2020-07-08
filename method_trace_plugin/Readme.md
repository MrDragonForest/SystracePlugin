## 插件使用
root build.gradle:
```
    repositories {
        ...
        maven { url = "E:\AndroidStudioProjects\mavenlib\repo" }
    }
    dependencies {
        classpath "com.dragonforest.systrace:methodtrace-plugin:1.0.0"
    }
```

app build.gradle:
```
  apply plugin: "com.dragonforest.systraceplugin"
```

- 编译完成会在根目录下生成 .methodtrace 目录，该目录下有两个文件：
    - success_trace.log ： 插桩成功的class列表
    - fail_trace.log ： 插桩失败的class列表
    
- 默认插桩只针对项目中使用的类，如果需要对其他类进行插桩（如第三方sdk）,请在app build.gradle中添加如下：
```
methodTraceConfig {
    //程序主目录（现在不需要配置）
    appPackageName = "com\\dragonforest\\demo\\app_java"

    //额外要插桩的包（第三方，如果只需要记录项目中的不要配置这个）
    extraTracePackages = [
            "com/google/gson/",
            "okhttp3/",
            "retrofit2/",
            "androidx/appcompat/app/",
            "androidx/appcompat/view/",
            "androidx/appcompat/widget/",
            "androidx/fragment/app/",
            "okio/"
    ]
}
```