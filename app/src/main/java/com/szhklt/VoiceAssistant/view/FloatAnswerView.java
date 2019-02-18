package com.szhklt.VoiceAssistant.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;

public class FloatAnswerView extends View{

    private Context pmContext;
    public WindowManager mWindowManager;
    private WindowManager.LayoutParams pwmParams;
    public View pmContentView; //代表的是当前的提示悬浮窗
    private boolean bShow = false;
    public FloatAnswerView(Context context) {
        super(context);
        // TODO Auto-generated constructor stub
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (pwmParams == null) {
            pwmParams = new WindowManager.LayoutParams();
        }
        pmContext = context;
    }


    public void setLayout(int layout_id) {
        pmContentView = LayoutInflater.from(pmContext).inflate(layout_id, null);
        pmContentView.setOnTouchListener(new OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        break;
                    default:
                        break;
                }
                return true;
            }
        });

    }

    public void show(String content) {
        if (pmContentView != null) {
            pwmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            pwmParams.format = PixelFormat.RGBA_8888;
            pwmParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            pwmParams.alpha = 1.0f;
            pwmParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;
            pwmParams.x = 0;
            pwmParams.y = 230;
            pwmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            pwmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            // 显示自定义悬浮窗口  
            mWindowManager.addView(pmContentView, pwmParams);
            TextView pcontent = (TextView)pmContentView.findViewById(R.id.cprompt);
            pcontent.setText(content);//设置内容

            bShow = true;
        }
    }

    public void close() {
        if (pmContentView != null) {
            mWindowManager.removeView(pmContentView);
            bShow = false;
        }
    }

    public boolean isShow() {
        return bShow;
    }
}
