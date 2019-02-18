package com.szhklt.VoiceAssistant.broadcastReceiver;

import java.text.SimpleDateFormat;
import java.util.Date;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;

import com.szhklt.VoiceAssistant.AlarmClockOperation;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.activity.AlarmClockActivity;
import com.szhklt.VoiceAssistant.db.AlarmClockDBHelper;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.DialogReboot;

/**
 *此类处理闹钟和定时清理等广播，然后将任务分发 
 *
 */
public class AlarmReceiver extends BroadcastReceiver {  
	private String TAG ="AlarmReceiver";
	private SQLiteDatabase adb= AlarmClockDBHelper.getInstance().getReadableDatabase();
	@Override
	public void onReceive(Context context, Intent intent) {  
		if ("android.alarm.demo.action".equals(intent.getAction())) {

		}else if ("android.alarm.guan.action".equals(intent.getAction())) {//收到定时清理的广播，打开倒计时页面
			LogUtil.e("alarm","系统即将清理垃圾");
			AlarmClockOperation.getInstance().setReboottime();
			Intent reboot = new Intent(context, DialogReboot.class);
			reboot.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
			context.startActivity(reboot);
		}else if("android.alarm.alarmclock.MainService".equals(intent.getAction())){//闹钟
			if(MainApplication.firmwareVersion.contains("pmu_IRQ")){//如果是xiaoba_ac108_v1.8_pmu_IRQ_wakeup 这个版本或者更高，就用setAlarmClock设置闹钟
				int id = intent.getIntExtra("requestCode", 0);
				LogUtil.e("alarm","id:"+id+LogUtil.getLineInfo());
				long triggerAtMillis = intent.getLongExtra("triggerAtMillis", 0);
				LogUtil.e("alarm","triggerAtMillis:"+triggerAtMillis+LogUtil.getLineInfo());
				String datetime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(triggerAtMillis));   
				String content = intent.getStringExtra("tag");
				boolean isrepeat=intent.getBooleanExtra("isrepeat", false);
				LogUtil.e("alarm","闹钟时间到了!datetime:"+datetime+";content:"+content+";isrepeat:"+isrepeat+";id:"+id+LogUtil.getLineInfo());
				if(isrepeat==false){
					adb.execSQL("delete from alarmlist where _id="+id);
				}
				Intent alarmUIintent = new Intent(context, AlarmClockActivity.class);
				alarmUIintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				alarmUIintent.putExtra("content", content);
				alarmUIintent.putExtra("time", datetime);
				context.startActivity(alarmUIintent);
			}else{
				int id =intent.getIntExtra("id", 0);
				String datetime = intent.getStringExtra("datetime");
				String content = intent.getStringExtra("content");
				String repeat = intent.getStringExtra("repeat");
				LogUtil.e("alarm","闹钟时间到了!datetime:"+datetime+";content:"+content+";repeat:"+repeat+";id:"+id+LogUtil.getLineInfo());
				if("单次".equals(repeat)){//如果是单次闹钟就从数据库中删除
					adb.execSQL("delete from alarmlist where _id="+id);
				}
				//打开闹钟界面
				Intent alarmUIintent = new Intent(context, AlarmClockActivity.class);
				alarmUIintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				alarmUIintent.putExtra("content", content);
				alarmUIintent.putExtra("time", datetime);
				context.startActivity(alarmUIintent);
			}
			context.sendBroadcast(new Intent("com.szhklt.activity.AlarmListActivity.UPDATEALARMLIST"));
		}
	}
}