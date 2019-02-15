package com.szhklt.www.VoiceAssistant.timeTask;

import java.util.TimerTask;

/**
 * 无结果返回超时
 * @author wuhao
 *
 */
public class ResultTimeout {
	private TimerUtil timerUtil;
	private static ResultTimeout resultTimeout;
	private ResultTimeout(){
		
	}
	public static ResultTimeout getInstance(){
		if(resultTimeout==null){
			resultTimeout= new ResultTimeout();
		}
		return resultTimeout;
	}
	private void start(){

		timerUtil = new  TimerUtil(new TimerTask() {
			@Override
			public void run() {
			}
		}, 3000);
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
