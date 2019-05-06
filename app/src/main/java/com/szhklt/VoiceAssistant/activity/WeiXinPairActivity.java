package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.szhklt.VoiceAssistant.R;

public class WeiXinPairActivity  extends Activity {


    private static final String TAG = "WeiXinPairActivity";
    private Button weiXincode;
    private Button  weiXinList;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MqttActivity的oncreate方法启动");
        setContentView(R.layout.activity_weixin);
        weiXincode = findViewById(R.id.weixincode);
        weiXinList = findViewById(R.id.weixinlist);
        weiXincode.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                Intent intent = new Intent(WeiXinPairActivity.this,MqttActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

        }

        });
        weiXinList.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                Intent intent2 = new Intent(WeiXinPairActivity.this,MqttListActivity.class);
                intent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent2);

            }

        });

    }

}
