package com.szhklt.www.VoiceAssistant.service;

import java.text.DecimalFormat;
import java.util.Timer;
import java.util.TimerTask;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.TrafficStats;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;
import com.szhklt.www.VoiceAssistant.R;
import com.szhklt.www.VoiceAssistant.activity.ChatActivity;
import com.szhklt.www.VoiceAssistant.component.MyAIUI;
import com.szhklt.www.VoiceAssistant.component.MySynthesizer;
import com.szhklt.www.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

public class TrafficStatisticsService extends Service {
//	private static final String TAG = "TrafficStatisticsService";
	public static double averageDialogTraffThresholdValue = 1.38d;
	/************action**********/
	public static final String TRAFFIC_STATISTICS_FINISH = "android.trafficstatistics.action.FINISH";
	public static final String TRAFFIC_STATISTICS_STARTSTATISTICS = "android.trafficstatistics.action.STARTSTATISTICS";
	public static final String TRAFFIC_STATISTICS_ENDSTATISTICS = "android.trafficstatistics.action.ENDSTATISTICS";

	private Handler handler = new Handler();
	private Timer timer;
	private long rxtxTotal = 0;
	private boolean isNetBad = false;
	private int time;
	private double rxtxSpeed = 1.0f;
	private DecimalFormat showFloatFormat = new DecimalFormat("0.00");
	private Intent receiverIntent;
	public final static String NET_SPEED_RECEIVER_ACTION = "com.ridgepm.network_speed_action";
	public IntentFilter intentFilter;
	public BroadcastReceiver broadcastReceiver;
	public String curTempSpeedStr;
	public static double curTempSpeed;
	public static double totalDouble; 
	public int timeForCount = 0;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		LogUtil.e("NetworkSpeedService","onCreate()");
		intentFilter = new IntentFilter();
		intentFilter.addAction(TRAFFIC_STATISTICS_FINISH);
		intentFilter.addAction(TRAFFIC_STATISTICS_STARTSTATISTICS);
		intentFilter.addAction(TRAFFIC_STATISTICS_ENDSTATISTICS);
		broadcastReceiver = new TrafficStatisticsBroadcast();
		registerReceiver(broadcastReceiver, intentFilter);
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LogUtil.e("NetworkSpeedService","onStartCommand()");
		if (timer == null) {
			timer = new Timer();
			timer.scheduleAtFixedRate(new RefreshTask(), 0L, (long) 200);
		}
		receiverIntent = new Intent();
		receiverIntent.setAction(NET_SPEED_RECEIVER_ACTION);
		int result = super.onStartCommand(intent, flags, startId);
		return result;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// Service被终止的同时也停止定时器继续运行
		timer.cancel();
		timer = null;
	}

	//    class TrafficStatisticsBinder extends Binder{
	//    	public void startStatistics(){
	//    		
	//    	};
	//    	public float endStatistics(){
	//    		
	//    		return rxtxTotal;
	//    	};
	//    }

	//定时任务
	class RefreshTask extends TimerTask {

		@Override
		public void run() {
			isNetBad = false;
			long tempSum = TrafficStats.getTotalRxBytes()
					+ TrafficStats.getTotalTxBytes();
			long rxtxLast = tempSum - rxtxTotal;
			double tempSpeed = rxtxLast * 1000 / 2000;
			rxtxTotal = tempSum;
			if ((tempSpeed / 1024d) < 20 && (rxtxSpeed / 1024d) < 20) {
				time += 1;
			} else {
				time = 0;
			}
			rxtxSpeed = tempSpeed;
			totalDouble += tempSpeed;
			timeForCount++;
			String curTempSpeedStr = showFloatFormat.format(tempSpeed / 1024d);
			LogUtil.e("NetworkSpeedService", curTempSpeedStr + "kb/s");
			if (time >= 4) {
				isNetBad = true;
				//				LogUtil.e("NetworkSpeedService", "网速差 " + isNetBad);
				time = 0; //重新检测
			}
			if (isNetBad) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						receiverIntent.putExtra("is_slow_net_speed", isNetBad);
						sendBroadcast(receiverIntent);//发送广播去取消这次请求.
					}
				});
			}
		}
	}

	class TrafficStatisticsBroadcast extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(TRAFFIC_STATISTICS_FINISH)){
				stopSelf();
			}else if(action.equals(TRAFFIC_STATISTICS_ENDSTATISTICS)){//结束累计
//				LogUtil.e("NetworkSpeedService","次数timeForCount:"+timeForCount+","+"总计totalDouble:"+showFloatFormat.format(totalDouble / 1024d)+LogUtil.getLineInfo());
				if((totalDouble / 1024d)/timeForCount < averageDialogTraffThresholdValue){
					LogUtil.e("NetworkSpeedService","对话时网络不稳定");
					Toast.makeText(context,"网络不稳定",Toast.LENGTH_SHORT).show();
					if(ChatActivity.ISCHATMODE == false)
						FloatWindowManager.getInstance().createAnswerWindow(context, R.layout.bubble_answer,"当前网络不稳定");
					MySynthesizer.getInstance(context).stopTts();
					MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
				}
			}else if(action.equals(TRAFFIC_STATISTICS_STARTSTATISTICS)){//开始累计
				LogUtil.e("NetworkSpeedService","----------------------------------");
				timeForCount = 0;
				totalDouble = 0;
			}
		}
	}
}
