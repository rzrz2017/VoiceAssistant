package com.szhklt.www.VoiceAssistant.skill;

import android.content.Intent;
import com.szhklt.www.VoiceAssistant.KwSdk;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.activity.ChatActivity;
import com.szhklt.www.VoiceAssistant.beam.intent;
import com.szhklt.www.VoiceAssistant.component.MyAIUI;
import com.szhklt.www.VoiceAssistant.timeTask.NoSpeekTimeout;
import com.szhklt.www.VoiceAssistant.timeTask.SpeekTimeout;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

public class IntoChatSkill extends Skill{
	private static final String TAG="ChatSkill";
	public static String chatIntent;
	public IntoChatSkill(intent intent){
		mintent=intent;
	}
	@Override
	protected void extractVaildInformation() {
		super.extractVaildInformation();
		chatIntent = mintent.getSemantic().get(0).getIntent();
		LogUtil.e(TAG, "chatIntent:"+chatIntent+ LogUtil.getLineInfo());
	}

	@Override
	public void execute() {
		extractVaildInformation();
		if("into_aiui".equals(chatIntent)){
			LogUtil.e(TAG,"启动聊天界面");

			//停止超时
			NoSpeekTimeout.getInstance().stop();
			SpeekTimeout.getInstance().stop();
			
            //暂停多媒体播放器
			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayer.action.PLAYER_ACTION").putExtra("playeraction","pause"));
			KwSdk.getInstance().pause();//暂停酷我音乐
			
			ChatActivity.ISCHATMODE = true;
			//进入聊天模式后，需要允许写入数据到AIUI引擎
			MyAIUI.WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = false"+LogUtil.getLineInfo());
			
			mTts.isOnlySpeechSynthesis("进入聊天模式");
			
			Intent intent = new Intent(MainApplication.getContext(),ChatActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainApplication.getContext().startActivity(intent);
		}else if("exit_aiui".equals(chatIntent)){
			MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
			mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
				@Override
				public void doSomethingsAfterTts() {
					// TODO Auto-generated method stub
					Intent sintent = new Intent("com.szhklt.msg.closeActivity.ChatActivity");
					MainApplication.getContext().sendBroadcast(sintent);
				}
			},"好的",question);
			ChatActivity.ISCHATMODE = false; 
		}
	}
}
