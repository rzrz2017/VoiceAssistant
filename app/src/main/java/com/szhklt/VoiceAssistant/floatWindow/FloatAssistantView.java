package com.szhklt.VoiceAssistant.floatWindow;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.view.FloatPromptView;


public class FloatAssistantView extends LinearLayout{
	
	public static AnimationDrawable animationDrawable;
	
    public ImageView animationIV = null;
    
	 /** 
     * 记录大悬浮窗的宽度 
     */  
    public int viewWidth;  
    
    /** 
     * 记录大悬浮窗的高度 
     */  
    public int viewHeight;  
    
    public View volumeview;
    
    public FloatWindowManager mFWM;
    public FloatPromptView mFAV;
    Context context;
	public FloatAssistantView(Context context, FloatWindowManager FWM ) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub	
		mFWM=FWM;
		
		LayoutInflater.from(context).inflate(R.layout.float_window_big, this);
        View view = findViewById(R.id.big_window_layout);  
        viewWidth = view.getLayoutParams().width;  
        viewHeight = view.getLayoutParams().height;       
        volumeview = (View)findViewById(R.id.volumeview);
        
        //播放动画
		animationIV = (ImageView) findViewById(R.id.animationIV);
        //点击图片关闭大悬浮窗创建小悬浮窗
//        animationIV.setOnClickListener(this);
	}
}


