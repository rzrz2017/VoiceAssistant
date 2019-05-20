package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.activity.dialog.PhoneListDiaAct;
import com.szhklt.VoiceAssistant.util.AESUtil;
import com.szhklt.VoiceAssistant.util.CommonUtils;
import com.szhklt.VoiceAssistant.util.QRCodeUtil;


/**
 * 小程序配对二维码界面
 */
public class MqttActivity extends Activity {
    private static final String TAG = "MqttActivity";
    private String sn;
    private static final String KEY = "hklthklthklthklt";
    private ImageView code;
    private TextView error;
    private Button phonesBt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e(TAG, "MqttActivity的oncreate方法启动");
        setContentView(R.layout.activity_mqtt);
        code = findViewById(R.id.code);
        error = findViewById(R.id.error);
        phonesBt = (Button) findViewById(R.id.phones);
        sn = CommonUtils.getSerialNumber();

        phonesBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MqttActivity.this, PhoneListDiaAct.class));
            }
        });

        //给返回键绑定点击事件
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
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

    @Override
    public void onDestroy() {
        super.onDestroy();

    }


    //在客户端生成二维码
    private void pair() {
        if (sn == null || "".equals(sn)) {
            return;
        }
        try {
            String AESsn = AESUtil.getEnc(sn, KEY);
            Log.e(TAG, "加密sn:" + AESsn);
            String codeinfo = AESsn + ":" + sn;
            Log.e(TAG,"codeinfo"+codeinfo);
            Bitmap bitmap = QRCodeUtil.createQRCodeBitmap(codeinfo, 500, 500);
            code.setImageBitmap(bitmap);
        } catch (Exception e) {
            e.printStackTrace();
            showError("二维码生成失败");
        }
    }

    private void showError(String message) {
        Log.e(TAG, "showerror");
        error.setText(message);
        code.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.codeerror));
    }
}