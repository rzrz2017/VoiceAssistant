package com.szhklt.www.VoiceAssistant.timeTask;

import java.util.TimerTask;
/**
 * 没有说话倒计时
 * @author wuhao
 *
 */
public class NoSpeekTimeout {
	private TimerUtil timerUtil;
	private static NoSpeekTimeout noSpeekTimerTaskUtil;
	private NoSpeekTimeout(){
	}
	public static NoSpeekTimeout getInstance(){
		if(noSpeekTimerTaskUtil==null){
			noSpeekTimerTaskUtil= new NoSpeekTimeout();
		}
		return noSpeekTimerTaskUtil;
	}

	public void start(TimerTask tt){
		timerUtil = new  TimerUtil(tt, 4500);
		timerUtil.start();
	}
	
	public void restart(TimerTask tt){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
		start(tt);
	}
	
	public void stop(){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
	}
}
