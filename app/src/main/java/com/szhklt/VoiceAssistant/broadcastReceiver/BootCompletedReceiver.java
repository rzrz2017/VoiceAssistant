package com.szhklt.VoiceAssistant.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.util.LogUtil;


public class BootCompletedReceiver extends BroadcastReceiver{

	@Override
	public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Toast.makeText(context,"正在启动语音助手",Toast.LENGTH_SHORT).show();
            LogUtil.e("starttime","接受到开机广播时间的时间为="+System.currentTimeMillis());
            LogUtil.e("BootCompletedReceiver","重启启动Mainservice");
            Intent startIntent = new Intent(context, MainService.class);
            startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startService(startIntent);
            
  	        //重置闹钟
//  	        LogUtil.e("alarm","设备重新启动，重新设置闹钟和定时清理时间");
//			Intent resetAlarmIntent=new Intent("com.szhklt.FloatWindow.FloatSmallView");
//			Bundle bundle = new Bundle();
//			bundle.putString("count","RESET_ALARMCLOCK");
//			bundle.putString("count", "REBOOT");
//			resetAlarmIntent.putExtras(bundle);
//	        context.sendBroadcast(resetAlarmIntent);
        }
	}
}
