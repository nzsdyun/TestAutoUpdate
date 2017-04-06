package com.example.sky.testautoupdate;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import update.UpdateModule;
import update.UpdateModuleConfiguration;

public class MainActivity extends AppCompatActivity {
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/nzsdyun/TestAutoUpdate/master/app/extras/update.json";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
        UpdateModuleConfiguration updateModuleConfiguration = new UpdateModuleConfiguration.Builder(this)
                .setServerConfigurationUrl(UPDATE_URL)
                .setCheckUpdateIntervalTime(24 * 60 * 60 * 1000)
                .setMaxCheckUpdateIntervalTime(15 * 24 * 60 * 60 * 1000)
                .setStartCheckTime("17:00")
                .setEndCheckTime("18:00")
                .build();
        UpdateModule.getInstance().startAutoCheckUpdate(updateModuleConfiguration);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void Click(View view) {
        if (view.getId() == R.id.check_update) {
            UpdateModule.getInstance().checkUpdate(this, UPDATE_URL);
        }
    }
}
