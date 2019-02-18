package com.szhklt.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
        LogUtil.e("MainActivity","onCreate()");
        startService(new Intent(this, MainService.class));
        if(FloatWindowManager.getInstance().fabButton != null){
            if(FloatWindowManager.getInstance().fabButton.thisMenu.isExpanded() == false){
                FloatWindowManager.getInstance().createFloatButton(this);
            }
        }else{
            FloatWindowManager.getInstance().createFloatButton(this);
        }
        finish();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
