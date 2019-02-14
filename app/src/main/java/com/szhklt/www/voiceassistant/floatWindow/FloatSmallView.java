package com.szhklt.www.voiceassistant.floatWindow;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.szhklt.www.voiceassistant.R;


public class FloatSmallView extends LinearLayout implements OnClickListener {
	//用于打开大窗
	public FloatWindowManager FWMinFSV;

    /** 
     * 记录小悬浮窗的宽度 
     */  
    public int viewWidth;  
  
    /** 
     * 记录小悬浮窗的高度 
     */  
    public int viewHeight;  

    public LocalBroadcastManager localBroadcastManager;
    public Context context;
    //创建小窗
	public FloatSmallView( Context context,FloatWindowManager FWM) {
		super(context);
		this.context=context;
		// TODO Auto-generated constructor stub
		FWMinFSV = FWM;
        LayoutInflater.from(context).inflate(R.layout.float_window_small, this);
        View view = findViewById(R.id.small_window_layout);  
        viewWidth = view.getLayoutParams().width;  
        viewHeight = view.getLayoutParams().height;  
        
        ImageView SleepImage = (ImageView)findViewById(R.id.sleep);
        SleepImage.setOnClickListener(this);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.sleep:
			Intent intent=new Intent();
	        intent.putExtra("count", "yuyi");
	        intent.setAction("com.szhklt.FloatWindow.FloatSmallView");
	        context.sendBroadcast(intent);
			FWMinFSV.removesmallWindow(getContext());
			break;

		default:
			break;
		}
		
		
	}  
}
	
