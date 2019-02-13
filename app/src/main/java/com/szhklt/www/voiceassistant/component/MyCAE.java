package com.szhklt.www.voiceassistant.component;

import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import com.iflytek.alsa.AlsaRecorder;
import com.iflytek.alsa.AlsaRecorder.PcmListener;
import com.iflytek.cae.CAEEngine;
import com.iflytek.cae.CAEError;
import com.iflytek.cae.CAEListener;
import com.iflytek.cae.util.res.ResourceUtil;
import com.iflytek.cae.util.res.ResourceUtil.RESOURCE_TYPE;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.PromptToneSoundPool;
import com.szhklt.www.voiceassistant.util.LogUtil;
import com.szhklt.www.voiceassistant.util.ScreenManager;

public class MyCAE implements CAEListener{
	private static final String TAG = "MyCAE";
	private Context context;
	private ScreenManager screenManager;
	public static boolean isWakeUped = false;//判断是否唤醒过，用来处理播放器前后状态不一致的问题
	private CAEEngine mCAEEngine;// CAE算法引擎
	public AlsaRecorder mRecorder = AlsaRecorder.createInstance(1, 64000, 1536);
	private String mResPath;
	public PromptToneSoundPool toneSP;

	public MyAIUI mAIUI;
	
	public MyCAE(){
		LogUtil.e(TAG,"MyCAE()"+LogUtil.getLineInfo());
		context = MainApplication.getContext();
		screenManager = new ScreenManager();

		initCAEAlgorithm();
		//初始化AIUI
		
		//初始化sp
		toneSP = new PromptToneSoundPool();
		toneSP.setCompleteListener(new PromptToneSoundPool.OnSoundPoolCompletionListener() {//提示音播放完成监听
			@Override
			public void onCompletion() {
				// TODO Auto-generated method stub
				mAIUI.sendAudioDataToAIUI(true);//使AIUI进入工作状态
			}
		});
	}

	public void setmAIUI(MyAIUI mAIUI) {
		this.mAIUI = mAIUI;
	}
	
	@Override
	public void onWakeup(String arg0) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onWakeup---麦克风阵列被唤醒"+LogUtil.getLineInfo());
		isWakeUped = true;
		screenManager.screenOn();
		toneSP.setVolumeValue();//设置音量
		toneSP.playSound(true);//播放提示音//注意播放完提示音后会有一些操作


		send("wakeup");
		send("[MediaPlayActivity]" + "getstatus");
		context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
		send("[MediaPlayActivity]" + "pause");//暂停多媒体
	}

	@Override
	public void onAudio(byte[] audioData, int dataLen, int param1,int param2) {// 将录到的音频写入
		// TODO Auto-generated method stub
		if(MyAIUI.WRITEAUDIOEABLE == true) {
			try {
				mAIUI.writeAudioData(audioData);
			} catch (Exception e) {
				LogUtil.e(TAG, "向AIUI写入数据有异常" + LogUtil.getLineInfo());
			}
		}
	}
	
	@Override
	public void onError(CAEError arg0) {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onError"+LogUtil.getLineInfo());
		LogUtil.e("mFWM"," removeAll()"+LogUtil.getLineInfo());
	}

	/**
	 * 初始化CAE引擎,主要是设置唤醒词
	 */
	private void initCAEAlgorithm(){
		mResPath = ResourceUtil.generateResourcePath(context,RESOURCE_TYPE.assets,MainApplication.JETNAME);
		LogUtil.e(TAG,"mResPath:"+mResPath+LogUtil.getLineInfo());
		mCAEEngine = CAEEngine.createInstance("cae", mResPath);
		mCAEEngine.setCAEListener(this);// 设置CAE监听者
		mCAEEngine.setRealBeam(2);
		if (null == mCAEEngine) {
			LogUtil.d(TAG, "引擎对象为空，请先创建");
			return;
		} else {
			startRecording();// 开始录音
		}
	}

	/**
	 * 重置CAE
	 */
	public void resetCAE(){
		if(mCAEEngine!=null){
			mCAEEngine.reset();
		}
	}
	
	/**
	 * 开始录音
	 */
	int startRecording(){
		return mRecorder.startRecording(mPcmListener);
	}
	
	/**
	 * 停止录音
	 */
	public void stopRecording(){
		if(mRecorder != null){
			mRecorder.stopRecording();
		}
	}
	
	/**
	 * 注销CAE
	 */
	public void destoryCAEandRecorder(){
	    if(screenManager != null){
	        screenManager.destory();//释放唤醒锁
        }

		if (mRecorder != null) {
			mRecorder.stopRecording();
			mRecorder = null;
			if (mCAEEngine != null) {
				mCAEEngine.setCAEListener(null);
				mCAEEngine.destroy();
				mCAEEngine = null;
			}
		}
		toneSP.destorySoundPool();//由于和CAE是绑定关系。
	}

	private JSONObject mSyn = new JSONObject();
	/**
	 * AlSA录音机 音频监听器
	 */
	PcmListener mPcmListener = new PcmListener() {
		@Override
		public void onPcmData(byte[] data, int dataLen) {
        synchronized (mSyn) {
            if (null != mCAEEngine) {
                mCAEEngine.writeAudio(data, dataLen);
            }
        }
		}
	};
	
	/**
	 * 发送广播（可能是要发给客户）
	 */
	private void send(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		MainApplication.getContext().sendBroadcast(intent);
	}
	
	/*****************************网络状态回调**********************************/
//	@Override
//	public void WifiDisconnect() {
//		Toast.makeText(mainService, "WIFI断开链接", Toast.LENGTH_SHORT).show();
//		mAIUI.resetAIUI();
//		mFW.removeAll();
//		if(DialogWIFI.dailogWIFIisTOP == false){
//			Intent intent = new Intent(mainService,DialogWIFI.class);
//			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//			mainService.startActivity(intent);
//		}
//	}
	
//	@Override
//	public void WifiConnect() {
//		new Thread(new Runnable() {
//			@Override
//			public void run() {
//				AlarmSkill.resetAlarmClock();
//			}
//		}).start();
//		Toast.makeText(mainService, "网路连接成功!", Toast.LENGTH_SHORT).show();
//		Intent intent = new Intent("com.szhklt.service.MainService");
//		intent.putExtra("count","internetok");
//		mainService.sendBroadcast(intent);
//	}
}