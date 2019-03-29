package com.handsome.yuyin;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SecondActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ActionBar actionbar1=getSupportActionBar();
        assert actionbar1 != null;
        actionbar1.setDisplayShowHomeEnabled(true);
        actionbar1.setLogo(R.drawable.ic_action_help_outline);
        actionbar1.setDisplayUseLogoEnabled(true);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setContentView(R.layout.activity_second);
    }
}
