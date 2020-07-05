package com.dragonforest.demo.systraceplugin

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dragonforest.demo.systraceplugin.constants.PreValues
import com.dragonforest.demo.systraceplugin.utils.Monitor
import com.dragonforest.demo.systraceplugin.utils.PreUtil

class SecondActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
    }

    override fun onResume() {
        PreUtil.putString1(this,PreValues.USER.USER_NAME,"hanlonglin")
        PreUtil.putString1(this,PreValues.USER.USER_PASSWD,"123456")
        super.onResume()
        Monitor.record()
    }
}