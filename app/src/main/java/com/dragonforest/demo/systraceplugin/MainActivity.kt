package com.dragonforest.demo.systraceplugin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dragonforest.demo.systraceplugin.utils.Monitor
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initView()
    }

    private fun initView() {
        Thread.sleep(500)
        btn1.setOnClickListener {
            startActivity(Intent(this@MainActivity,SecondActivity::class.java))
        }
        btn2.setOnClickListener {
            startActivity(Intent(this@MainActivity,ThirdActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        Monitor.record()
    }
}