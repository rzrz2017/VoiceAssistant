package com.szhklt.VoiceAssistant.view;

import android.content.Context;
import android.graphics.PixelFormat;
import android.text.method.ScrollingMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;

public class FloatPromptView extends View{
	
	 private Context pmContext;  
	 public WindowManager mWindowManager;  
	 private WindowManager.LayoutParams pwmParams;  
	 public View pmContentView; //代表的是当前的提示悬浮窗  
	 private boolean bShow = false;  
	 
	 String content;
//	private View pFloatLayout;
	

    
    /** 
     * 记录手指按下时在小悬浮窗的View上的横坐标的值 
     */  
//    private float xInView;  
  
    /** 
     * 记录手指按下时在小悬浮窗的View上的纵坐标的值 
     */  
//    private float yInView;   

	public FloatPromptView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
        if (pwmParams == null) {  
            pwmParams = new WindowManager.LayoutParams();  
        }  
        pmContext = context;
	}
	
//    LayoutInflater inflater = LayoutInflater.from(context);  
//    //获取浮动窗口视图所在布局
//    //类似setContentView()
//    cFloatLayout = (LinearLayout) inflater.inflate(R.layout.qipao, null); 
//    
//    //改变内容
//    TextView records = (TextView) cFloatLayout.findViewById(R.id.records);
//    records.setText(cmd);
	
    public void setLayout(int layout_id) {  
        pmContentView = LayoutInflater.from(pmContext).inflate(layout_id, null);        
    } 
    
    
//    private void updateViewPosition() {  
//        pwmParams.x = (int) (pmScreenX - pmRelativeX);  
//        pwmParams.y = (int) (pmScreenY - pmRelativeY);  
//        mWindowManager.updateViewLayout(pmContentView, pwmParams);  
//    }  
    
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
            pcontent.setMovementMethod(ScrollingMovementMethod.getInstance()); 
            pcontent.setText(content);//设置内容
            
            bShow = true;  
        }  
    }  
  
    public void close() {  
        if (pmContentView != null) {  
        	mWindowManager.removeView(pmContentView);  
			//pmContentView = null;
			//FloatWindowManager.promptWindow = null;
            bShow = false;  
        }  
    }  
      
    public boolean isShow() {  
        return bShow;  
    }  

	public void closePromptview() {
		// TODO Auto-generated method stub
		bShow = false;//为false时表示悬浮窗不存在
		mWindowManager.removeView(pmContentView);
		mWindowManager = null;
	}  

}
