package com.szhklt.VoiceAssistant.util;

import android.app.Service;
import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.floatWindow.FloatActionButtomView;

/**
 * linein切换封装一下，各个方法写成了静态方法，方便全局调用。
 * @author rz
 *
 */
public class LineInControler {
	private static final String TAG = "LineInControler";
	//	public static LineInSatus PRELINEINSTATUS = LineInSatus.NATIVE;
	//	public static LineInSatus getPRELINEINSTATUS() {
	//		return PRELINEINSTATUS;
	//	}
	//	public static void setPRELINEINSTATUS(LineInSatus pRELINEINSTATUS) {
	//		PRELINEINSTATUS = pRELINEINSTATUS;
	//	}
	private static LineInControler instance = new LineInControler(MainApplication.getContext());
	private AudioManager mAudioManager;
	private AudioTrack track;
	private int bsz;

	private Handler mHandler;
	private static final int FLUSH_AUXIN_TRUE = 1;
	private static final int FLUSH_AUXIN_FALSE = 2;

	private LineInControler(Context context){
		mHandler = new Handler(new Callback() {

			@Override
			public boolean handleMessage(Message msg) {
				LogUtil.e("blehandler","msg.what :"+msg.what);
				// TODO Auto-generated method stub
				if(msg.what == FLUSH_AUXIN_TRUE){
					LogUtil.e("auxinStatus","auxinStatus变成false了"+LogUtil.getLineInfo());
					FloatActionButtomView.auxinStatus = false;
					FloatActionButtomView.flushAuxinFab(true);
				}else if(msg.what == FLUSH_AUXIN_FALSE){
					LogUtil.e("auxinStatus","auxinStatus变成true了"+LogUtil.getLineInfo());
					FloatActionButtomView.auxinStatus = true;
					FloatActionButtomView.flushAuxinFab(false);
				}
				return false;
			}
		});
		mAudioManager = (AudioManager)context.getSystemService(Service.AUDIO_SERVICE);
		bsz = AudioTrack.getMinBufferSize(8000, 
				AudioFormat.CHANNEL_CONFIGURATION_STEREO,
				AudioFormat.ENCODING_PCM_16BIT);
		track = new AudioTrack(AudioManager.STREAM_MUSIC,
				8000,
				AudioFormat.CHANNEL_CONFIGURATION_STEREO, 
				AudioFormat.ENCODING_PCM_16BIT,
				bsz,
				AudioTrack.MODE_STREAM);
	}
	public static LineInControler getInstance(){
		return instance;
	}
	/**
	 * 切换为linein
	 */
	public void switchAux(){
		LogUtil.e(TAG,"切换为linein:linein=2");
		//停止蓝牙界面
		mHandler.sendEmptyMessage(FLUSH_AUXIN_FALSE);
		track.play();
		mAudioManager.setParameters("linein=2");
	}
	/**
	 * 切换为蓝牙
	 */
	public void switchBle(){
		LogUtil.e(TAG,"切换为蓝牙:linein=1");
		mHandler.sendEmptyMessage(FLUSH_AUXIN_TRUE);
		track.play();
		mAudioManager.setParameters("linein=1");
	}
	/**
	 * 停止LineIn
	 */
	public void stopLineIn(){
		LogUtil.e(TAG,"停止LineIn:linein=0");
		//停止蓝牙界面
		mHandler.sendEmptyMessage(FLUSH_AUXIN_TRUE);
		mAudioManager.setParameters("linein=0");
		track.stop();
	}
	/**
	 * 获取当前LineIn状态
	 * @return "2" 蓝牙端的LINEIN打开
	 *         "1" 音频端的LINEIN打开
	 *         "0" 表示linein关闭
	 */
	public String getStatus(){
		return mAudioManager.getParameters("linein");
	}

	public enum LineInSatus{
		BLE,LINEIN,NATIVE;
	}
}
