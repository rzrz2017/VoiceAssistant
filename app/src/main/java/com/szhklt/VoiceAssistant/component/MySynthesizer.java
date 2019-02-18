package com.szhklt.VoiceAssistant.component;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.RzMusicPkg.MediaPlayerWrapper;
import com.szhklt.VoiceAssistant.activity.ChatActivity;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.skill.IntoChatSkill;
import com.szhklt.VoiceAssistant.skill.KeyeventControlSkill;
import com.szhklt.VoiceAssistant.skill.MusicControlSkill;
import com.szhklt.VoiceAssistant.timeTask.SleepTimeout;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class MySynthesizer implements SynthesizerListener{
	private String TAG = "MySynthesizer";
	private static MySynthesizer mMySynthesizer;
	private Context context;
	private String voicerCloud = "xiaoqi";// 默认云端发音人
	private SpeechSynthesizer mTts;// 语音合成对象
	private FloatWindowManager mFWM;//悬浮窗UI管理
	private DoSomethingAfterTts md;
	
	//用来标识在AIUI再进入sleep状态时，是否要恢复播放器状态。
	//每次交互如果有过tts播放，sleep时就不需要恢复状态，因为在tts播放完成时会恢复
	private static boolean isTtsed = false;//是否有过tts合成//还可以用来配置是否需要恢复播放器状态
	public static boolean getIsTtsed(){
		return isTtsed;
	}
	public static void setIsTtsed(boolean status){
		isTtsed = status;
	}
	
	public static Boolean FLAG = false;
	public void setCallBack(DoSomethingAfterTts doSomeingAfterTts){
		this.md = doSomeingAfterTts;
	}

	private MySynthesizer(Context context){
		this.context = context;
		mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);//初始化tts
		mFWM = FloatWindowManager.getInstance();
		synthetisesetParam();
	}

	public static MySynthesizer getInstance(Context context){
		if(mMySynthesizer == null){
			mMySynthesizer = new MySynthesizer(context);
		}
		
		return mMySynthesizer;
	}

	@Override
	public void onSpeakBegin() {
		LogUtil.i(TAG, "TTS开始播报");
		isTtsed = true;
		//开始tts时停止播放多媒体
		context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
		if(ChatActivity.ISCHATMODE == false){
			MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------------"+LogUtil.getLineInfo());
			mFWM.startTtsAnimation();
		}
		
		//过度期结束
//		MyAIUI.TRANSITIONALPERIOD = false;LogUtil.e("TRANSITIONALPERIOD",MyAIUI.TRANSITIONALPERIOD.toString()+LogUtil.getLineInfo());
		SleepTimeout.getInstance().stop();
	}
	
	@Override
	public void onSpeakPaused() {
		LogUtil.i(TAG, "TTS暂停播放");
//		mFWM.stopAnimation();
	}
	@Override
	public void onSpeakResumed() {
		LogUtil.i(TAG, "TTS继续播放");
//		mFWM.startTtsAnimation();
	}
	@Override
	public void onBufferProgress(int percent, int beginPos, int endPos,String info) {
	}
	@Override
	public void onSpeakProgress(int percent, int beginPos, int endPos) {
	}
	@Override
	public void onCompleted(SpeechError error) {
		// TODO Auto-generated method stub
		LogUtil.i(TAG, "TTS播放完成");

		LogUtil.e("prestatus","tts播放完成"+"酷我播放器前状态："+ KwSdk.getPreKwStatus());
		if(ChatActivity.ISCHATMODE == false){
			MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
		}

		if(MainApplication.longWakeUp == true){
			MyAIUI.WRITEAUDIOEABLE = true;LogUtil.e("iat","MyAIUI.WRITEAUDIOEABLE = true;"+LogUtil.getLineInfo());
		}

		send("[manyspeech]" + "over");
		SleepTimeout.getInstance().restart();

		mFWM.stopAnimation();
		mFWM.removeAnswerWindow(context);
		mFWM.removeQuestionWindow(context);
		if(MainApplication.longWakeUp == false){
			mFWM.removeAll();
		}

		if(md != null){
			md.playMusicAfterTts();
			md.return2manAfterTts();
			md.playStatusAfterTts();
			md.pauseStatusAfterTts();
			md.doSomethingsAfterTts();
			md = null;
		}
	}

	/**
	 * 恢复唤醒前的音乐状态
	 */
	public static void recoveyMusicStatusBeforeWakeUp(){
		if(KwSdk.getPreKwStatus() == false){//PlayerStatus.PLAYING
			LogUtil.e("prestatus","恢复状态:"+"暂停酷我");
			KwSdk.getInstance().pause();
		}else{
			LogUtil.e("prestatus","恢复状态:"+"继续播放");
			KwSdk.getInstance().play();
		}
		if(MediaPlayerWrapper.getPreStatus() == true){
			LogUtil.e("premedia","恢复状态:"+"播放多媒体");
			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","play"));
		}else{
			LogUtil.e("premedia","恢复状态:"+"暂停多媒体");
			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
		}
	}
	
	/**
	 * 复位flag
	 */
	public static void recoveyFlag(){
		MusicControlSkill.KFLAG = null;
		IntoChatSkill.chatIntent = null;
		MyAIUI.SERVICE = null;
		KeyeventControlSkill.skillIntent = null;
	}
	
	@Override
	public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
	}
	/************************************CAE初始化**************************************/
	/**
	 * 初始化监听。
	 */
	private InitListener mTtsInitListener = new InitListener() {
		@Override
		public void onInit(int code) {
			if (code != ErrorCode.SUCCESS) {
				Toast.makeText(context, "初始化失败,错误码：" + code,Toast.LENGTH_LONG).show();
			} 
		}
	};
	/**
	 * 立即停止语义合成
	 */
	public void stopTts(){
		if(null!= mTts){
			if(!mTts.isSpeaking()){
				return;
			}
			mTts.stopSpeaking();
		}
	}
	/**
	 * 注销Tts
	 */
	public void destoryTts(){
		if (null != mTts) {
			mTts.stopSpeaking();
			mTts.destroy();
		}
	}
	/**
	 * 是否正在说话
	 */
	public Boolean isSpeaking(){
		return mTts.isSpeaking();
	}
	/**
	 * 合成参数设置
	 */
	private void synthetisesetParam() {
		mTts.setParameter(SpeechConstant.PARAMS, null);
		// 设置使用云端引擎
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
		// 设置发音人
		mTts.setParameter(SpeechConstant.VOICE_NAME, voicerCloud);
		mTts.setParameter(SpeechConstant.SPEED,"65");//语速
		mTts.setParameter(SpeechConstant.PITCH,"50");//语调
		mTts.setParameter(SpeechConstant.VOLUME,"80");//tts音量
		//设置播放器音频流类型
		mTts.setParameter(SpeechConstant.STREAM_TYPE,"3");//有用
		//设置播放合成音频打断音乐播放,默认为true
		mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
	}

	/**
	 * Tts语音合成
	 * @param answer
	 * @param question
	 */
	public void speechSynthesis(String answer,String question){
		LogUtil.e("now","speechSynthesis()"+LogUtil.getLineInfo());
		synthetisesetParam();
		mTts.stopSpeaking();
		mTts.startSpeaking(answer, this);
		if(ChatActivity.ISCHATMODE){
			//发送广播到多伦界面
			LogUtil.e("now","answer:"+answer+LogUtil.getLineInfo());
			sendAnswerMsgToChatActivity(answer);
			LogUtil.e("now","question:"+question+LogUtil.getLineInfo());
			sendQuestionMsgToChatActivity(question);
			LogUtil.e("now","发送广播到多伦界面"+LogUtil.getLineInfo());
			return;
		}
		mFWM.flushQandAWindow(answer, question);
	}


	/**
	 * 播放只需要回答并且不用弹窗的数据
	 * @param answer
	 */
	public void isOnlySpeechSynthesis(String answer){
		LogUtil.e("now","isOnlySpeechSynthesis()");
		mFWM.removeAll();
		mTts.stopSpeaking();
		mTts.startSpeaking(answer, this);
	}


	/**
	 * Tts后做一些事情
	 */
	public void doSomethingAfterTts(DoSomethingAfterTts doSomething,String answer,String question) {
		mTts.stopSpeaking();
		mTts.startSpeaking(answer, this);
		answer = answer.replace("肠", "长");
		LogUtil.e(TAG,"answer:"+answer+LogUtil.getLineInfo());
		if(question != null){
			if(ChatActivity.ISCHATMODE == true){
				sendQuestionMsgToChatActivity(question);
			}else{
				mFWM.removeQuestionWindow(context);
				mFWM.createQuestionWindow(context,question);
			}
		}
		if(answer != null){
			if(ChatActivity.ISCHATMODE == true){
				sendAnswerMsgToChatActivity(answer);
			}else{
				mFWM.removeAnswerWindow(context);
				mFWM.createAnswerWindow(context, R.layout.bubble_answer,answer);
			}
		}
		setCallBack(doSomething);
	}


	//用于闹钟
	public void doSomethingAfterAlarm(DoSomethingAfterTts doSomething,String answer,String question) {
		mTts.stopSpeaking();
		mTts.startSpeaking(answer, this);
		setCallBack(doSomething);
	}


	public class DoSomethingAfterTts{
		public String song;
		public String singer;
		public String album;
		public String theme;
		public DoSomethingAfterTts(String song,String singer,String album,String theme){
			this.song = song;
			this.singer = singer;
			this.album = album;
			this.theme = theme;
		}
		public DoSomethingAfterTts(){
		}

		public void playMusicAfterTts(){
		}
		public void return2manAfterTts(){
		}
		public void playStatusAfterTts(){
		}
		public void pauseStatusAfterTts(){
		}
		public void playMediaAfterTts(){
		}
		public void timeOut(){}
		public void doSomethingsAfterTts(){}
	}


	/**
	 * 发送广播
	 */
	private void send(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		MainApplication.getContext().sendBroadcast(intent);
	}

	/**
	 * 发送数据到Chat
	 * @param answer
	 */
	private void sendAnswerMsgToChatActivity(String answer){
		Intent intent = new Intent("com.szhklt.msg.CHAT_MESSAGE_answer");
		intent.putExtra("answer",answer);
		context.sendBroadcast(intent);
	}

	/**
	 *
	 * @param question
	 */
	private void sendQuestionMsgToChatActivity(String question){
		Intent intent = new Intent("com.szhklt.msg.CHAT_MESSAGE_question");
		intent.putExtra("question",question);
		context.sendBroadcast(intent);
	}

}
