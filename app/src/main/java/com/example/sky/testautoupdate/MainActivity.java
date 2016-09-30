package com.example.sky.testautoupdate;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import update.UpdateModule;

public class MainActivity extends AppCompatActivity {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/nzsdyun/TestAutoUpdate/master/app/extras/update.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void Click(View view) {
        if (view.getId() == R.id.check_update) {
            UpdateModule.getInstance().checkUpdate(this, UPDATE_URL);
        }

    }
}
