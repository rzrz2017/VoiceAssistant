package com.szhklt.www.VoiceAssistant.timeTask;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

public class LongRunningService extends Service{
	private static final String TAG = "LongRunningService";
	public static final String KEY_START_COMMAND = "start_command";
	public static final int START_COMMAND_GET_WEATHER = 0;	
	public static final int STOP_COMMAND_GET_WEATHER = 2;
	
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		AlarmManager manager = (AlarmManager)getSystemService(ALARM_SERVICE);
		final int increment;
		long triggerAtTime = 0;
		Intent i = new Intent(this,LongRunningService.class);
		PendingIntent pi;
		int cmd = intent.getIntExtra(KEY_START_COMMAND, -1);
		LogUtil.e(TAG,"cmd:"+cmd);
		switch (cmd) {
			case START_COMMAND_GET_WEATHER:
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						// TODO Auto-generated method stub
						LogUtil.e(TAG,"---时间到了查询天气---"+LogUtil.getLineInfo());
						//查询地理位置
						new Location().getPositionInfo();//内部查询了天气
						new Weather().queryWeather();
					}
				}).start();
				increment = 2*60*60*1000;//两个小时查一下天气 2*60*60*1000
				triggerAtTime = SystemClock.elapsedRealtime() + increment;
				i.putExtra("KEY_START_COMMAND", START_COMMAND_GET_WEATHER);
				pi = PendingIntent.getService(this, START_COMMAND_GET_WEATHER, i, 0);
				manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
				break;
			case STOP_COMMAND_GET_WEATHER:
				i.putExtra("KEY_START_COMMAND", START_COMMAND_GET_WEATHER);
				pi = PendingIntent.getService(this, START_COMMAND_GET_WEATHER, i, 0);
				manager.cancel(pi);
				break;
		}
		return 0;
	}
	
    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(TAG,"LongRunningService onCreate");
    }
    
	/**
	 * 发送广播
	 */
	public void sendMainServiceBoardcast(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		MainApplication.getContext().sendBroadcast(intent);
	}
    
    /*************************************************************/
    public static Intent newIntent(Context context,int cmd){
    	Intent intent = new Intent(context,LongRunningService.class);
    	LogUtil.e(TAG,"newIntent:" + cmd + LogUtil.getLineInfo());
    	intent.putExtra(KEY_START_COMMAND,cmd);
    	return intent;
    }
}
