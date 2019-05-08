package com.szhklt.VoiceAssistant.timeTask;

import java.util.Timer;
import java.util.TimerTask;

public class TimerUtil extends Timer {
	private TimerTask task;
	private long time;
	private Timer timer;

	public TimerUtil(TimerTask task, long time) {
		this.task = task;
		this.time = time;
	}

	/**
	 * 开始定时任务
	 */
	public void start() {
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(task, time);
	}
	
	/**
	 * 重复任务
	 * @param delay
	 * @param period
	 */
	public void scheduleRepeat(long delay,long period){
		if (timer == null) {
			timer = new Timer();
		}
		timer.schedule(task, delay, period);
	}

	/**
	 * 销毁定时任务
	 */
	public void cancel() {
		if (task != null) {
			task.cancel();
		}
	}

}
