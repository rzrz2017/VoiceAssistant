package com.szhklt.VoiceAssistant.skill;

import android.content.Intent;

import com.szhklt.VoiceAssistant.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.beam.intent;

public class MediaPlaySkill extends Skill {	
	private static final String TAG = "MediaPlaySkill";
	private KwSdk mkwSdk = KwSdk.getInstance();
	
	public MediaPlaySkill(intent intent){
		mintent = intent;
	}

	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
	}
	
	@Override
	public void execute() {
		extractVaildInformation();
		mkwSdk.exit();
		mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
//				RZMediaPlayActivity2.actionStart(context,null,null,mintent);
				
//				RZMediaPlayActivity.SERVICE = mintent.getService();
				
//				mediaControler.flushListView(mintent.getData().getResult());
				
//				LogUtil.e(TAG,"重要！！一切讯飞的媒体数据通过广播方式发送！！"+LogUtil.getLineInfo());
//				List<Result> mediaData;
//				try {
//					mediaData = mintent.getData().getResult();
//					if(RZMediaPlayActivity.checkVaildMediaData(mediaData) == true){//检查数据是否全部为空
//						context.sendBroadcast(new Intent("android.rzmediaplayact.action.FLUSH_LIST_VIEW").
//								putExtra("data",(Serializable)mediaData));
//					}
//				}finally{
//					mediaData = null;
//				}
			}
		}, answerText, question);
	}
	
	/**
	 * 发送广播
	 */
	public void send(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		MainApplication.getContext().sendBroadcast(intent);
	}
}
