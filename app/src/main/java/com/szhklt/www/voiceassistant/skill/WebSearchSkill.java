package com.szhklt.www.voiceassistant.skill;


import android.content.Context;
import android.content.Intent;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.beam.intent;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class WebSearchSkill extends Skill{
	private Context context= MainApplication.getContext();
	private String webUrl=null;
	private String answer=null;
	public WebSearchSkill(intent intent){
		mintent = intent;
	}
	@Override
	protected void extractVaildInformation() {
		super.extractVaildInformation();
		try {
			webUrl=mintent.getData().getResult().get(0).getUrl();
			LogUtil.e("websearch", "webUrl:"+webUrl+LogUtil.getLineInfo());
			answer=mintent.getAnswer().getText();
			LogUtil.e("websearch", "answer:"+answer+LogUtil.getLineInfo());
		} catch (Exception e) {
			webUrl="www.baidu.com";
			answer="抱歉，我没有听懂，或许换个说法我就听明白了";
		}
	}
	@Override
	public void execute() {
		super.execute();
		LogUtil.e("websearch", "tts后的事情:"+LogUtil.getLineInfo());
//		Intent intent = new Intent(context,WebSeachActivity.class);
//		intent.putExtra("web", webUrl);
//		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//		context.startActivity(intent);
		mTts.doSomethingAfterTts(null,answer,question);
	}
	private void sendMainServiceBoardcast(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		context.sendBroadcast(intent);
	}
}
