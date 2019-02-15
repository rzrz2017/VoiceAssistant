package com.szhklt.www.VoiceAssistant.component;

import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.iflytek.alsa.AlsaRecorder;
import com.iflytek.alsa.AlsaRecorder.PcmListener;
import com.iflytek.cae.CAEEngine;
import com.iflytek.cae.CAEError;
import com.iflytek.cae.CAEListener;
import com.iflytek.cae.util.res.ResourceUtil;
import com.iflytek.cae.util.res.ResourceUtil.RESOURCE_TYPE;
import com.szhklt.www.VoiceAssistant.KwSdk;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.NetworkStateInterface;
import com.szhklt.www.VoiceAssistant.PromptToneSoundPool;
import com.szhklt.www.VoiceAssistant.R;
import com.szhklt.www.VoiceAssistant.RzMusicPkg.MediaPlayerWrapper;
import com.szhklt.www.VoiceAssistant.activity.ChatActivity;
import com.szhklt.www.VoiceAssistant.activity.SleepActivity;
import com.szhklt.www.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.www.VoiceAssistant.service.MainService;
import com.szhklt.www.VoiceAssistant.skill.AlarmSkill;
import com.szhklt.www.VoiceAssistant.util.LogUtil;
import com.szhklt.www.VoiceAssistant.util.NetworkUtil;
import com.szhklt.www.VoiceAssistant.util.ScreenManager;
import com.szhklt.www.VoiceAssistant.activity.DialogWIFI;

import java.util.Random;

import cn.kuwo.autosdk.api.PlayerStatus;

public class MyCAE implements CAEListener, NetworkStateInterface {
	private static final String TAG = "MyCAE";
	private Context context;
	private ScreenManager screenManager;
	public static boolean isWakeUped = false;//判断是否唤醒过，用来处理播放器前后状态不一致的问题
	private CAEEngine mCAEEngine;// CAE算法引擎
	public AlsaRecorder mRecorder = AlsaRecorder.createInstance(1, 64000, 1536);
	private FloatWindowManager mFW = FloatWindowManager.getInstance();
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
				if(!ChatActivity.ISCHATMODE){//不在聊天模式
					mFW.flushQandAWindow(
							MainApplication.getContext().getResources().getString(R.string.prompt_title) + "\n"+
									MainApplication.getContext().getResources().getStringArray(R.array.tips)[new Random(System.currentTimeMillis()).nextInt(5)],                            null);
					mFW.startComprehendAnimation();
				}
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
		MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------------"+LogUtil.getLineInfo());
		MySynthesizer.getInstance(MainApplication.getContext()).stopTts();//立即停止tts播放
		MainService.volume_value = 0;//一唤醒音量应该是0
		savePlayerStatusBeforeWakeup();//保存播放器唤醒前的状态
		screenManager.screenOn();
		toneSP.setVolumeValue();//设置音量
		if(!NetworkUtil.isNetworkConnected(MainApplication.getContext())){
			checkNetWork();//也会播放提示音//每次唤醒前检查wifi是否连通
			return;
		}
		toneSP.playSound(true);//播放提示音//注意播放完提示音后会有一些操作
		KwSdk.getInstance().pause();//暂停酷我


		MainApplication.getContext().sendBroadcast(new Intent(SleepActivity.FINISH));//关闭休眠界面
		send("wakeup");
		send("[MediaPlayActivity]" + "getstatus");
		context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
		send("[MediaPlayActivity]" + "pause");//暂停多媒体
	}

	/**
	 * 弹出网络链接窗(内部也会播放提示音)
	 */
	private void checkNetWork(){
		toneSP.playSound(false);//播放提示音
		if(!DialogWIFI.dailogWIFIisTOP){
			//弹出网络链接窗
			Intent intent = new Intent(MainApplication.getContext(),DialogWIFI.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainApplication.getContext().startActivity(intent);
		}
	}

	/**
	 * 保存唤醒前播放器状态
	 */
	public static void savePlayerStatusBeforeWakeup(){
		if(PlayerStatus.PLAYING != KwSdk.getInstance().getCurPlayerStatus()){
			KwSdk.setPreKwStatus(false);
		}else{
			KwSdk.setPreKwStatus(true);
		}
		LogUtil.e("prestatus","当前状态:"+KwSdk.getPreKwStatus());
		if(MediaPlayerWrapper.getStatus() == true){//kw在播放
			LogUtil.e("premedia","当前状态:"+"多媒体在播放");
			MediaPlayerWrapper.setPreStatus(true);
		}else{//酷我没有播放
			LogUtil.e("premedia","当前状态:"+"多媒体停止播放");
			MediaPlayerWrapper.setPreStatus(false);
		}
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
	public int startRecording(){
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
	@Override
	public void WifiDisconnect() {
		Toast.makeText(MainApplication.getContext(), "WIFI断开链接", Toast.LENGTH_SHORT).show();
		mAIUI.resetAIUI();
		mFW.removeAll();
		if(DialogWIFI.dailogWIFIisTOP == false){
			Intent intent = new Intent(MainApplication.getContext(),DialogWIFI.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            MainApplication.getContext().startActivity(intent);
		}
	}
	
	@Override
	public void WifiConnect() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				AlarmSkill.resetAlarmClock();
			}
		}).start();
		Toast.makeText(MainApplication.getContext(), "网路连接成功!", Toast.LENGTH_SHORT).show();
		Intent intent = new Intent("com.szhklt.service.MainService");
		intent.putExtra("count","internetok");
        MainApplication.getContext().sendBroadcast(intent);
	}
}