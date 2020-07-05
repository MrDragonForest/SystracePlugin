package com.dragonforest.demo.systraceplugin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dragonforest.demo.systraceplugin.constants.PreValues
import com.dragonforest.demo.systraceplugin.utils.Monitor
import com.dragonforest.demo.systraceplugin.utils.PreUtil
import kotlinx.android.synthetic.main.activity_third2.*

class ThirdActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third2)
    }

    override fun onResume() {
        super.onResume()
        var userName = PreUtil.getString1(this,PreValues.USER.USER_NAME)
        var passwd = PreUtil.getString1(this,PreValues.USER.USER_PASSWD)
        tv_name.showMessage(userName)
        tv_name.showMessage(passwd)
        Monitor.record()
    }

}