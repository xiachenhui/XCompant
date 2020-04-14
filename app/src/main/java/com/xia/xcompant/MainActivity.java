package com.xia.xcompant;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.xia.router_annotation.Router;

/**
 * author : xia chen hui
 * email : 184415359@qq.com
 * date : 2019/12/6/006 15:13
 * desc : 组件化测试
 **/
@Router(path = "/main/test")
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
}
