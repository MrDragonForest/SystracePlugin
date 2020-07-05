package com.dragonforest.demo.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Trace;

import com.dragonforest.demo.app_java.util.PreUtil;

public class SecondActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        initView();
    }

    private void initView() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        PreUtil.INSTANCE.putString(this,PreValues.USER_NAME,"hanlonglin");
        PreUtil.INSTANCE.putString(this,PreValues.USER_PASSWD,"123456");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}