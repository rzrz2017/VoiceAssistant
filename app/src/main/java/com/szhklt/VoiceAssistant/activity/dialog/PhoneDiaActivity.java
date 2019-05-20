package com.szhklt.VoiceAssistant.activity.dialog;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.service.MqttService;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class PhoneDiaActivity extends Activity implements View.OnClickListener {
    private String TAG = "PhoneDiaActivity";
    private StringBuilder mes;

    private TextView mesText;
    private TextView countdownText;
    private Button ok;
    private Button no;

    private CountDownTimer countDownTimer;

    private MqttService.MqttBinder mqttBinder;
    private ClickCallBack clickCallBack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_dia);
        Intent intent = getIntent();
        mes = new StringBuilder(intent.getStringExtra("message"));
        if(intent.getBooleanExtra("positive",false)){

        }else{
            findViewById(R.id.ok_field).setVisibility(View.GONE);
        }

        if(intent.getBooleanExtra("negative",false)){

        }else{
            findViewById(R.id.no_field).setVisibility(View.GONE);
        }

        if(!intent.getBooleanExtra("positive",false)
                && !intent.getBooleanExtra("negative",false)){
            findViewById(R.id.bt_field).setVisibility(View.GONE);
        }

        mesText = (TextView)findViewById(R.id.mes);
        countdownText = (TextView)findViewById(R.id.countdown);
        ok = (Button) findViewById(R.id.ok);
        ok.setOnClickListener(this);
        no = (Button)findViewById(R.id.no);
        no.setOnClickListener(this);

        if(mes.toString() != null && !mes.equals("")){
            mesText.setText(mes);
        }

        Intent bindIntent = new Intent(this,MqttService.class);
        bindService(bindIntent,connection, Context.BIND_AUTO_CREATE);

        Long millisInFuture = intent.getLongExtra("millisInFuture",5000L);
        if(millisInFuture != 0){
            countdownText.setText("("+millisInFuture/1000+")");
            CountDown(millisInFuture);
        }else {
            countdownText.setVisibility(View.GONE);
        }
    }

    private void CountDown(Long millisInFuture){
        countDownTimer = new CountDownTimer(millisInFuture,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                LogUtil.e(TAG,"millisUntilFinished:"+millisUntilFinished);
                countdownText.setText("("+millisUntilFinished/1000+")");
            }

            @Override
            public void onFinish() {
                LogUtil.e(TAG,"onFinish");
                countdownText.setText("");
                finish();
            }
        };
        countDownTimer.start();
    }

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mqttBinder = (MqttService.MqttBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        LogUtil.e(MqttService.TAG,"onDestroy");
        mqttBinder = null;
        clickCallBack = null;
        unbindService(connection);
        if(countDownTimer != null){
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.ok){
            if(mqttBinder != null){
                clickCallBack = mqttBinder.getClickCallBack();
                if(clickCallBack != null){
                    clickCallBack.onOKClick();
                }
            }
            finish();
        }else if(v.getId() == R.id.no){
            finish();
            if(mqttBinder != null){
                clickCallBack = mqttBinder.getClickCallBack();
                if(clickCallBack != null){
                    clickCallBack.onNoClick();
                }
            }

        }
    }

    public interface ClickCallBack{
        void onOKClick();
        void onNoClick();
    }
}
