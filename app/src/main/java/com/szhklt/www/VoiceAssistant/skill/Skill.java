package com.szhklt.www.VoiceAssistant.skill;

import android.content.Intent;

import com.szhklt.www.VoiceAssistant.KwSdk;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.RzMusicPkg.MediaPlayerWrapper;
import com.szhklt.www.VoiceAssistant.beam.intent;
import com.szhklt.www.VoiceAssistant.component.MySynthesizer;
import com.szhklt.www.VoiceAssistant.util.LogUtil;
import com.szhklt.www.VoiceAssistant.beam.intent.Answer;


public class Skill {
	protected MySynthesizer mTts = MySynthesizer.getInstance(MainApplication.getContext());
	protected intent mintent;
	protected String question;
	protected Answer answer;
	protected String answerText;
	public Skill(){
		LogUtil.e("chat","skill父类的空构造方法"+LogUtil.getLineInfo());
	}
	public Skill(intent intent) {
		// TODO Auto-generated method stub
		LogUtil.e("chat","skill父类构造方法"+LogUtil.getLineInfo());
		mintent = intent;
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
		   mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
			   @Override
			public void doSomethingsAfterTts() {
				// TODO Auto-generated method stub
				recoveryPlayerState();   
			}
		   },answerText,  question);
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
