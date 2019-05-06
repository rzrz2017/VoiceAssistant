package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.service.MqttService;
import com.szhklt.VoiceAssistant.util.AESUtil;
import com.szhklt.VoiceAssistant.util.QRCodeUtil;


public class MqttActivity extends Activity {
    private static final String TAG = "MqttActivity";
    private String sn;
    private static final String KEY = "hklthklthklthklt";
    private ImageView code;
    private TextView error;
    private TextView retry;
    private MqttService mqttService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MqttActivity的oncreate方法启动");
        setContentView(R.layout.activity_mqtt);
        code = findViewById(R.id.code);
        error = findViewById(R.id.error);
        retry = findViewById(R.id.retry);
        sn = getDeviceSN();

        MqttService.startservice(MqttActivity.this,sn);

        //给返回键绑定点击事件
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        //给重试键绑定点击事件
        findViewById(R.id.retry)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        pair();
                    }
                });


    }


    //获取设备SN号
    public static String getDeviceSN() {

        String serialNumber = android.os.Build.SERIAL;
        Log.e(TAG, "serialNumber:" + serialNumber);
        return serialNumber;
    }

    //响应auth提供者
    private void requestAuthorize() {
        Log.e(TAG, "响应");
        MqttService.startservice(MqttActivity.this);


    }

    @Override
    public void onResume() {
        super.onResume();
        pair();
    }

    @Override
    public void onPause() {
        super.onPause();
        //activity.cancelAuthorize();
    }

    //在客户端生成二维码
    private void pair() {

        if (sn == null || "".equals(sn)) {
            return;
        }
        try {
            String AESsn = AESUtil.getEnc(sn, KEY);
            Log.e(TAG, "加密sn:" + AESsn);
          /*  String DESsn = AESUtil.getDec("ss",KEY);
            Log.e("解密sn",DESsn);*/


            String codeinfo = AESsn + ":" + sn;
            Log.e(TAG,"codeinfo"+codeinfo);
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(codeinfo, 500, 500);
            code.setImageBitmap(bitmap);
            retry.setVisibility(View.INVISIBLE);

        } catch (Exception e) {
            e.printStackTrace();
            showError("二维码生成失败");
        }
        requestAuthorize();
    }

   /* public void second(){
        Log.e(TAG,"second执行了");
    }*/


    private void showError(String message) {
        Log.e(TAG, "showerror");
        error.setText(message);
        code.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.codeerror));
        retry.setVisibility(View.VISIBLE);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }



}