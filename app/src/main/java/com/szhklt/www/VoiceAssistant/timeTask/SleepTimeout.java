package com.szhklt.www.VoiceAssistant.timeTask;

import java.util.TimerTask;
import android.content.Context;
import android.content.Intent;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.activity.SleepActivity;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

/**
 * 定时休眠任务工具类
 * @author wuhao
 *
 */
public class SleepTimeout {
	public final static String TAG = "SleepTimeout";
	private static SleepTimeout sleepTimerTaskUtil;
	private Context context=MainApplication.getContext();
	private TimerUtil timerUtil;
	private SleepTimeout(){
	}
	public static SleepTimeout getInstance(){
		if(sleepTimerTaskUtil==null){
			sleepTimerTaskUtil= new SleepTimeout();
		}
		return sleepTimerTaskUtil;
	}
	/**
	 * 开始休眠定时任务
	 */
	private void start(){
		timerUtil = new TimerUtil(new TimerTask() {
			@Override
			public void run() {
				LogUtil.e(TAG,"休眠时间到了");
				Intent intent = new Intent(context, SleepActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
				sendMainServiceBoardcast("gotosleep");
				sendMainServiceBoardcast("[manyspeech]" + "over");
			}
		}, 5*60*1000);//5*60*1000
		timerUtil.start();
	}

	/**
	 * 重新开始休眠定时任务
	 */
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

	/**
	 * 发送广播
	 */
	public void sendMainServiceBoardcast(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		context.sendBroadcast(intent);
	}
}
