package com.szhklt.VoiceAssistant.skill;

import android.content.Intent;

import com.szhklt.VoiceAssistant.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.RzMusicPkg.MediaPlayerWrapper;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.component.MySynthesizer;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.util.LogUtil;


public class Skill {
	protected MySynthesizer mTts;
	protected FloatWindowManager mFWM;//悬浮窗UI管理
	protected intent mintent;
	protected String question;
	protected intent.Answer answer;
	protected String answerText;
	public Skill(intent intent) {
		// TODO Auto-generated method stub
		LogUtil.e("chat","skill父类构造方法"+LogUtil.getLineInfo());
		mintent = intent;
		mFWM = FloatWindowManager.getInstance();
		mTts = MySynthesizer.getInstance(MainApplication.getContext());
	}

	protected void extractVaildInformation(){
		question = mintent.getText();
		LogUtil.e("now",question+LogUtil.getLineInfo());
		answer = mintent.getAnswer();
		if(answer != null){
			answerText = answer.getText();
		}else{
			answerText = mintent.getText();
		}
	};

	public void execute() {
		extractVaildInformation();
		mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
				// TODO Auto-generated method stub
				recoveryPlayerState();
			}
		},answerText, question);
	};

	/**
	 * 恢复播放器状态
	 */
	protected void recoveryPlayerState(){
		KwSdk.getInstance().recoveryState();
		if(MediaPlayerWrapper.getPreStatus() == true){
			LogUtil.e("premedia","恢复状态:"+"播放多媒体");
			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","play"));
		}else{
			LogUtil.e("premedia","恢复状态:"+"暂停多媒体");
			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
		}
	}
}
