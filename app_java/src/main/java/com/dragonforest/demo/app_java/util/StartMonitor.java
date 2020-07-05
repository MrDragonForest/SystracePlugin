package com.dragonforest.demo.app_java.util;

import android.os.Build;
import android.os.Trace;
import android.util.Log;

import androidx.annotation.RequiresApi;

/**
 * create by DragonForest at 2020/7/4
 */
public class StartMonitor {
    private static String TAG = "StartMonitor";
    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static void start(){
        Log.i("Start","开始");
    }
}
