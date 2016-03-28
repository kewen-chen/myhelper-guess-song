package com.chenkewen.administrator.myhelper.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;

import com.chenkewen.administrator.myhelper.R;

/**
 * 通关界面
 * Created by Administrator on 2016/3/25.
 */
public class AllPassView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.all_pass_view);

        FrameLayout view = (FrameLayout) findViewById(R.id.layout_bar_coin);
        view.setVisibility(View.INVISIBLE);
    }
}
