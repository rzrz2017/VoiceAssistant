package com.szhklt.www.VoiceAssistant.skill;


import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.smartline.docking.JDUtils;
import com.smartline.docking.JDsmartMsgCallback;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.component.MySynthesizer;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

public class ZJTSmartHomeSkill {
	private MySynthesizer mTts = MySynthesizer.getInstance(MainApplication.getContext());
	private String xftext;
	private String  attrValue;
	private String service;
	private String question;
	public ZJTSmartHomeSkill(String xftext) {
		this.xftext=xftext;
	}

	public void smartHomeControl(){
		LogUtil.e("zjt","smartHomeControl()");
		try {
			JSONTokener tokener = new JSONTokener(xftext); 
			JSONObject jsonObject = new JSONObject(tokener);
			service=jsonObject.getString("service");
			LogUtil.e("zjt","这一行出错"+"jsonObject:"+jsonObject.toString()+LogUtil.getLineInfo());
			JSONObject jsonsemantic = jsonObject.getJSONObject("semantic");
			LogUtil.e("zjt","jsonsemantic:"+jsonsemantic+LogUtil.getLineInfo());
			JSONObject jsonslots = jsonsemantic.getJSONObject("slots");
			LogUtil.e("zjt","jsonslots:"+jsonslots+LogUtil.getLineInfo());
			  attrValue =jsonslots.getString("attrValue");
			  if(service.equals("light_smartHome")){
					if(attrValue.equals("开")){
						sendhttp("客厅灯", "打开");
						mTts.doSomethingAfterTts(null, "已为您打开灯",question);
					}else if(attrValue.equals("关")){
						sendhttp("客厅灯", "关闭");
						mTts.doSomethingAfterTts(null, "已为您关闭灯",question);
					}
				}else if(service.equals("curtain_smartHome")){
					if(attrValue.equals("开")){
						sendhttp("客厅窗帘", "打开");
						mTts.doSomethingAfterTts(null, "正在为您打开窗帘",question);
					}else if(attrValue.equals("关")){
						sendhttp("客厅窗帘", "关闭");
						mTts.doSomethingAfterTts(null, "正在为您关闭窗帘",question);
					}
				}
		} catch (JSONException e) {
			e.printStackTrace();
			LogUtil.e("zjt","出错了，见鬼");
		} 
		
	}

	private String sendhttp(final String name, final String how) {
		String temp = "DS111111110000003";
		JDUtils.deviceControl(MainApplication.getContext(), temp, name, how,new JDsmartMsgCallback() {
			@Override
			public void msgCallback(boolean arg0, String arg1) {
				LogUtil.e("zjt" ,"智能家居控制网络请求:"+name + "---" + how+LogUtil.getLineInfo());
				LogUtil.e("zjt" , "智能家居返回的结果:" + arg0 +"---"+ arg1+LogUtil.getLineInfo());
			}
		});
		return name + how;
	}
}
