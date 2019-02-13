package com.szhklt.www.voiceassistant.component;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class MyTextUnderstander {
	private static final String TAG = MyTextUnderstander.class.getSimpleName();
	private TextUnderstander mTextUnderstander;
	
	public MyTextUnderstander() {
		// TODO Auto-generated constructor stub
		mTextUnderstander = TextUnderstander.createTextUnderstander(MainApplication.getContext(),new InitListener() {
			@Override
			public void onInit(int code) {
				if (code != ErrorCode.SUCCESS) {
					LogUtil.e(TAG,"初始化文本语义SUCCESS）");
	        	}
			}
		});
	}
	
	/**
	 * 文本语义理解
	 * @param text
	 * @param textUnderstanderListener
	 * @return
	 */
	public int understandText(String text,TextUnderstanderListener textUnderstanderListener){
		LogUtil.e(TAG, "文本语义理解:understandText");
		return mTextUnderstander.understandText(text, textUnderstanderListener);
	}
	
	/**
	 * 回收TextUnderstander资源
	 */
	public void destory(){
		if(mTextUnderstander != null){
			mTextUnderstander.cancel();
			mTextUnderstander.destroy();	
		}
	}
}
