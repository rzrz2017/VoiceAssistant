package com.szhklt.www.voiceassistant.timeTask;

import java.util.TimerTask;

import android.content.Context;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.timeTask.TimerUtil;

public class TtsTimeout {
	private Context context=MainApplication.getContext();
	private TimerUtil timerUtil;
	private static TtsTimeout ttsTimerTaskUtil;
	private TtsTimeout(){
	}
	public static TtsTimeout getInstance(){
		if(ttsTimerTaskUtil==null){
			ttsTimerTaskUtil= new TtsTimeout();
		}
		return ttsTimerTaskUtil;
	}
	
	private void start(){
		 timerUtil = new  TimerUtil(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					
				}
			}, 0);
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
