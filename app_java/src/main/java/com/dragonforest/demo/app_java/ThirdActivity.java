package com.dragonforest.demo.app_java;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.dragonforest.demo.app_java.util.PreUtil;

public class ThirdActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_third);
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
        String name ="james";
        String passwd = "heheheh";
        name = PreUtil.INSTANCE.getString(this, PreValues.USER_NAME);
        passwd = PreUtil.INSTANCE.getString(this, PreValues.USER_PASSWD);
        TextView tv_name = findViewById(R.id.tv_name);
        TextView tv_passwd = findViewById(R.id.tv_passwd);
        tv_name.setText(name);
        tv_passwd.setText(passwd);
    }
}