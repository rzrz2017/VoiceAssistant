package com.szhklt.www.voiceassistant.broadcastReceiver;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.component.MyAIUI;
import com.szhklt.www.voiceassistant.component.MySynthesizer;
import com.szhklt.www.voiceassistant.timeTask.ResultTimeout;
import com.szhklt.www.voiceassistant.util.LogUtil;

public  class OtherReceiver extends BroadcastReceiver { 
	private static final String TAG = "OtherReceiver";
	private MySynthesizer mTts = MySynthesizer.getInstance(MainApplication.getContext());
	@Override
	public void onReceive(Context context, Intent intent) {
		ResultTimeout.getInstance().stop();
		Bundle bundle=intent.getExtras();
		String othertext=bundle.getString("othertext");
		LogUtil.e(TAG,"othertext:"+othertext+LogUtil.getLineInfo());
		if(othertext!=null){
			try {
				JSONObject jsonObject = new JSONObject(othertext);
				String result = jsonObject.optString("result");
				final String text=jsonObject.getString("text");
				if(null != result){
					if(result.equals("1")){//代表智能家居控制成功，开始复述用户说的话，直接读取就可以了
						if(text!=null){
							LogUtil.e(TAG, "接收到客户的广播："+text+LogUtil.getLineInfo());
							mTts.doSomethingAfterTts(null,text, MyAIUI.SMARTQUESTION);
						}else{
							LogUtil.e(TAG, "客户已经做过处理，但text字段为空"+LogUtil.getLineInfo());
//							mTts.doSomethingAfterTts(null,"抱歉,我没有听懂,或许换个说法我就听明白了", MyAIUI.SMARTQUESTION);
						}   
					}else if(result.equals("0")||result.equals("-1")){
						LogUtil.e(TAG,"1-------------到了这里"+LogUtil.getLineInfo());
//						String lrc = new JSONObject(text).getString("text");
//						mTts.doSomethingAfterTts(null,"抱歉,我没有听懂,或许换个说法我就听明白了", MyAIUI.SMARTQUESTION);
					}else{
						//第三方返回的数据result字段为其他值，但是暂时不清楚这是不是智能家居的处理，先注释掉
						//						mTts.speechSynthesis("抱歉,我没有听懂,或许换个说法我就听明白了!", question);
					}
				}else{
					//					第三方返回的数据不为空但result字段为空（处理第三方异常情况），但是暂时不清楚这是不是智能家居的处理，先注释掉
					//											mTts.speechSynthesis(null, null);
//					mFWM.flushQandAWindow("抱歉,我没有听懂,或许换个说法我就听明白了",MyAIUI.SMARTQUESTION);
				}
			}catch (JSONException e) {
				e.printStackTrace();
			}     
		}else{
			//第三方返回的数据等于空（处理第三方异常情况），但是暂时不清楚这是不是智能家居的处理，先注释掉
			//			mTts.speechSynthesis(null, null);
//			mFWM.flushQandAWindow("抱歉,我没有听懂,或许换个说法我就听明白了",MyAIUI.SMARTQUESTION);
		}
		MyAIUI.SMARTQUESTION = null;
	}

}

