package com.szhklt.VoiceAssistant;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import com.szhklt.VoiceAssistant.util.HkAlarmClock;
import com.szhklt.VoiceAssistant.util.LogUtil;

/**
 * 用于处理闹钟
 * @author wuhao
 *
 */
public class AlarmClockOperation{
	public static final int REBOOT_ID = 3442;
	private static AlarmClockOperation alarmClockOperation;
	private static final String TAG="AlarmClockService";
	private HkAlarmClock mAlarmClock= new HkAlarmClock();
	private SharedPreferences preferences;
	private String reboottime;
	private Context context=MainApplication.getContext();

	/**
	 * 私有化构造器
	 */
	private AlarmClockOperation(){
	}

	public static synchronized  AlarmClockOperation getInstance(){
		if(alarmClockOperation == null){
			alarmClockOperation = new AlarmClockOperation();
		}
		return alarmClockOperation;
	}


	/**
	 * 设置凌晨重启
	 */
	public void setReboottime() {
		LogUtil.e("reboot", "开始设置定时清理时间"+ LogUtil.getLineInfo());
		preferences= context.getSharedPreferences("rebootdate",Activity.MODE_PRIVATE);
		preferences.edit();
		reboottime=preferences.getString("reboottime",null);
		mAlarmClock.sTomorrowDateArray = mAlarmClock._GetTomorrowDate();// 获取明天日期
		String[] YMDHMS = new String[7];
		YMDHMS[0] = mAlarmClock.sTomorrowDateArray[0];
		LogUtil.e("reboot","YMDHMS[0]"+YMDHMS[0]);
		YMDHMS[1] = mAlarmClock.sTomorrowDateArray[1];
		LogUtil.e("reboot","YMDHMS[1]"+YMDHMS[1]);
		YMDHMS[2] = mAlarmClock.sTomorrowDateArray[2];
		LogUtil.e("reboot","YMDHMS[2]"+YMDHMS[2]);
		if(reboottime!=null){
			String [] SC=reboottime.split("时");
			YMDHMS[3] = SC[0];
			YMDHMS[4] = SC[1].replace("分", "");
			YMDHMS[5] = "00";
			YMDHMS[6] = "清理";
		}else{
			YMDHMS[3] = "04";
			YMDHMS[4] = "00";
			YMDHMS[5] = "00";
			YMDHMS[6] = "清理";
		}
		if (YMDHMS != null) {
			setAlarmClock(YMDHMS[0], YMDHMS[1], YMDHMS[2], YMDHMS[3],YMDHMS[4], YMDHMS[5], YMDHMS[6],"android.alarm.guan.action");
		}
	}

	/**
	 * 设置闹钟
	 */
	public void setAlarmClock(String year, String month, String day,
			String hourOfDay, String minute, String second, String action,
			String string) {

		long time = System.currentTimeMillis();
		LogUtil.d("reboot", year + "/" + month + "/" + day + "/" + hourOfDay + ":"+ minute + ":" + second + ",---->" + action + ",num=" + time+LogUtil.getLineInfo());
		Intent intent = new Intent(string);
		Bundle bundle = new Bundle(); // 携带时间数据
		bundle.putString("year", year);
		bundle.putString("month", month);
		bundle.putString("day", day);
		bundle.putString("hour", hourOfDay);
		bundle.putString("minute", minute);
		bundle.putString("second", second);
		bundle.putString("action", action);
		intent.putExtras(bundle);
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		long longDate = 0;
		try {
			Date date = sdf.parse(year + "-" + month + "-" + day + " "+ hourOfDay + ":" + minute + ":" + second);
			longDate = date.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		long TimeDifference = longDate - System.currentTimeMillis();// 获得时间差
		LogUtil.e(TAG, "时间差:" + TimeDifference);
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context,REBOOT_ID , intent, PendingIntent.FLAG_CANCEL_CURRENT);
		if (Build.VERSION.SDK_INT < 19) {
			am.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ TimeDifference, sender);
		} else {
			am.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+ TimeDifference, sender);
		}
	}
	
	/**
	 * 取消重启闹钟"android.alarm.guan.action"
	 */
	public void cancelRebootAlarmClock(){
		Intent intent = new Intent("android.alarm.guan.action");
		AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast(context,REBOOT_ID , intent, PendingIntent.FLAG_CANCEL_CURRENT);
		am.cancel(sender);
	}
}
