package com.szhklt.VoiceAssistant.activity;

import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.floatWindow.FloatActionButtomView;
import com.szhklt.VoiceAssistant.util.LineInControler;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

public class BlueToothActivity2 extends Activity{
	public static final String CLOSE_ACTIVITY = "com.szhklt.activity.BlueToothActivity2.CLOSE_ACTIVITY";
	
	public static boolean openAux = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth2);
		
		IntentFilter inf = new IntentFilter();
		inf.addAction(CLOSE_ACTIVITY);
		
		registerReceiver(myb, inf);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		KwSdk.getInstance().pause();
		//关闭多媒体播放器
		MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
	
//		bletooth.setColorNormalResId(R.color.default_color_1);
		if(FloatActionButtomView.bletooth != null){
			FloatActionButtomView.bletooth.setTitle("关闭蓝牙");
		}
		LineInControler.getInstance().switchBle();
		FloatActionButtomView.saveSwicthState(false, "blestate");
		
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		if(FloatActionButtomView.bletooth != null){
		FloatActionButtomView.bletooth.setTitle("打开蓝牙");
		}
		if(openAux == false){
			LineInControler.getInstance().stopLineIn();
		}
		openAux = false;
		FloatActionButtomView.saveSwicthState(true, "blestate");
		
		unregisterReceiver(myb);
		super.onDestroy();
	}
	
	private BroadcastReceiver myb = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(CLOSE_ACTIVITY.equals(action)){
				finish();
			}
		}
	};
}
