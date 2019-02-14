package com.szhklt.www.voiceassistant;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import com.szhklt.www.voiceassistant.util.LogUtil;

/**
 * 智能家居控制
 * @author rz
 *
 */
public class SmartMsgPostMan{
	private final static String TAG = "SmartMsgPostMan";
	private final static String OtherAction = "android.intent.msg.AIUITEXT";
    private final String[] whitelist = {"train","flight","scheduleX","musicX","musicPlayer_smartHome",

            "HKLT.light_up_down","HKLT.volume_up_down","HKLT.keyevent","HKLT.intoaiui",

            "HKLT.music_ctr","cookbook","translation","weather","joke","story","news",

            "radio","calc","openQA","animalCries","baike","calendar","carNumber","chineseZodiac",

            "constellation","crossTalk","datetime","drama","dream","health","history","holiday",

            "idiom","internetRadio","lottery","motorViolation","novel","poetry","riddle","sentenceMaking",

            "stock","storyTelling","stroke","telephone","translation","websearch","wordFinding",

            "wordsMeaning","brainTeaser","AIUI.guessNumber","AIUI.cyclopedia","calc","LEIQIAO.cyclopedia",
            
            "englishEveryday","AIUI.brainTeaser","openQA","LEIQIAO.relationShip",""};
    
    private List<String> needDealSerList;
    private static SmartMsgPostMan instance = new SmartMsgPostMan();
    public static SmartMsgPostMan getInstance(){
    	return instance;
    }
  //获取context对象
  	private Context context=MainApplication.getContext();
  	private ISpeechAsrListener textListener;
    public ISpeechAsrListener getTextListener() {
		return textListener;
	}

	public void setTextListener(ISpeechAsrListener textListener) {
		LogUtil.e("wujia", "setTextListener（）"+LogUtil.getLineInfo());
		this.textListener = textListener;
	}

	private SmartMsgPostMan(){
        needDealSerList = new ArrayList<String>();
        for(String tmp:whitelist){
            needDealSerList.add(tmp);
        }
    }

    public boolean isExist(String service){
        int i = needDealSerList.indexOf(service);
        if(i == -1){
            Log.e("rz",service+"不存在于whitelist中,需要发送给典声");
            return false;
        }else{
            return true;
        }
    }
    
    public void sendMsg2Client(String xftext){
        try {
        	if(textListener != null){
				if(xftext != null){
					JSONObject obj;
					String question;
					obj = new JSONObject(xftext);
					question = obj.getString("text");
					textListener.onResult(question);
				}
        	}
		} catch (RemoteException | JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        Intent intent = new Intent(OtherAction);
        Bundle bundle = new Bundle();
        bundle.putString("hktext", xftext);
        intent.putExtra("hktext", xftext);
        context.sendBroadcast(intent);
    }
}
