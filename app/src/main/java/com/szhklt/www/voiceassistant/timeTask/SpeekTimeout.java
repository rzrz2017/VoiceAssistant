package com.szhklt.www.voiceassistant.timeTask;

import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;

import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.component.MyAIUI;
import com.szhklt.www.voiceassistant.component.MySynthesizer;
import com.szhklt.www.voiceassistant.floatWindow.FloatWindowManager;
import com.szhklt.www.voiceassistant.util.LogUtil;

/**
 * 说话过长倒计时
 * @author wuhao
 *
 */
public class SpeekTimeout {
	public static final String TAG = "SpeekTimeout";
	public boolean isSpeekTimeout = false;
	private Context context=MainApplication.getContext();
	private TimerUtil timerUtil;
	private static SpeekTimeout speekTimerTaskUtil;
	private MySynthesizer mTts=MySynthesizer.getInstance(MainApplication.getContext());
	private MyAIUI mAIUI;
	public static SpeekTimeout getInstance(){
		if(speekTimerTaskUtil==null){
			speekTimerTaskUtil= new SpeekTimeout();
		}
		
		return speekTimerTaskUtil;
	}

	private void start(){
		LogUtil.e(TAG,"start()"+LogUtil.getLineInfo());
		timerUtil = new TimerUtil(new TimerTask() {
			@Override
			public void run() {
				LogUtil.e(TAG,"说话时间太长");
				
				//resetAIUI
				MainApplication.getContext().sendBroadcast(new Intent(MyAIUI.RESET_AIUI));
				
				mTts.doSomethingAfterTts( mTts.new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
//						mTts.destoryTts();//注销tts
						FloatWindowManager.getInstance().removeAll();
					}
				}, "您的话也太肠了吧", null);
				stop();
			}
		}, 6000);
		timerUtil.start();
	}
	
	public void restart(){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
		start();
	}
	
	public void stop(){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
	}
}
