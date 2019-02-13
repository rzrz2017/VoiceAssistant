package com.szhklt.www.voiceassistant.component;

import java.io.IOException;
import java.io.InputStream;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.R;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class MyAIUI implements AIUIListener{
	public static final String TAG = "MyAIUI";
	public static boolean WRITEAUDIOEABLE = false;// 是否写入音频到语义
	private AIUIAgent mAIUIAgent;
	private int mAIUIState = AIUIConstant.STATE_IDLE;//AIUI当前状态
	private MySynthesizer mTts;

	public MyAIUI(){
		createAgent();
	}
	
	/**
	 * 文本语义
	 */
	public void understandText(String strContent){
		sendAudioDataToAIUI(true);
		byte[] content = strContent.getBytes();
		String params = "data_type=text";
		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE,0,0,params,content);
		mAIUIAgent.sendMessage(msg);
	}
	
	/**
	 * 停止语音交互
	 */
	private void stopSemanticInteraction(){
		LogUtil.e("mFWM"," removeAll()"+LogUtil.getLineInfo());
		mTts.stopTts();
		Into_STATE_READY();
	}

	@Override
	public void onEvent(AIUIEvent event) {
		switch (event.eventType) {
		case AIUIConstant.EVENT_CONNECTED_TO_SERVER:
			LogUtil.e(TAG, "已连接服务器");
			break;
		case AIUIConstant.EVENT_SERVER_DISCONNECTED:
			LogUtil.e(TAG, "与服务器断连");
			WRITEAUDIOEABLE = false;

			break;
		case AIUIConstant.EVENT_WAKEUP:
			LogUtil.e(TAG, "进入识别状态");
			WRITEAUDIOEABLE = true;
			break;
		case AIUIConstant.EVENT_RESULT: 
			Log.e(TAG,"语义理解完成返回");

		case AIUIConstant.EVENT_ERROR:
			LogUtil.e(TAG, "AIUI引擎错误: " + event.arg1 + "\n" + event.info);

			break;
		case AIUIConstant.EVENT_VAD:

			break;
		case AIUIConstant.EVENT_SLEEP:
			LogUtil.e(TAG,"进入睡眠");
			WRITEAUDIOEABLE = false;

			break;
		case AIUIConstant.EVENT_START_RECORD:
			LogUtil.e(TAG, "已开始录音");

			break;
		case AIUIConstant.EVENT_STOP_RECORD:
			LogUtil.e(TAG, "已停止录音");

			break;
		case AIUIConstant.EVENT_STATE:// 状态事件
			int mAIUIState = event.arg1;
			if (AIUIConstant.STATE_WORKING == mAIUIState) {
				LogUtil.e(TAG, "AIUI工作中，可进行交互");
			} else if (AIUIConstant.STATE_IDLE == mAIUIState) {
				LogUtil.e(TAG, "闲置状态，AIUI未开启");
			} else if (AIUIConstant.STATE_READY == mAIUIState) {
				LogUtil.e(TAG, "AIUI已就绪状态，等待唤醒");
			} 
			break;
		default:
			break;
		}
	}
	
	/**
	 * 技能执行完成后续操作
	 */
	private void skillExeComSubOpt() {
		LogUtil.e(TAG,"技能执行完成"+LogUtil.getLineInfo());
	}

	/**
	 * 预处理json数据
	 */
	private String preproccessResultData(AIUIEvent event) {
		try {
			JSONObject bizParamJson = new JSONObject(event.info);
			JSONObject data = bizParamJson.getJSONArray("data").getJSONObject(0);
			JSONObject params = data.getJSONObject("params");
			JSONObject content = data.getJSONArray("content").getJSONObject(0);//得到content,判断是否有cnt_id
			Log.e("event","event.info:"+event.info+LogUtil.getLineInfo());
			String sub = params.optString("sub");//得到sub字段，根据此字段判断是iat或者nlp
			if(content.has("cnt_id")){//判断有无cnt_id
				String cnt_id = content.getString("cnt_id");
				Log.e("event","cnt_id:"+cnt_id+LogUtil.getLineInfo());
				byte[] dataBuffer = event.data.getByteArray(cnt_id);
				if ("nlp".equals(sub)) {
					String cntStr = new String(dataBuffer, "utf-8");//根据cnt_id(key)的值取到对应的值(value),得到intent字段
				    LogUtil.e("cntStr","cntStr:"+cntStr+LogUtil.getLineInfo());
					long eosRsltTime = event.data.getLong("eos_rslt", -1);// 获取从数据发送完到获取结果的耗时，单位：ms
					LogUtil.e("eosRsltTime", "eosRsltTime:"+eosRsltTime+LogUtil.getLineInfo());
					
					if (TextUtils.isEmpty(cntStr)) {
						return null;
					}
					JSONObject cntJson = new JSONObject(cntStr);
					if("nlp".equals(sub)){
						Log.e("event","cntStr:"+cntStr+LogUtil.getLineInfo());
						Log.e("iat","cntStr:"+cntStr+LogUtil.getLineInfo());
						if(cntStr.equals("{\"intent\":{}}")){
							LogUtil.e("cntStr", "过滤了一些噪音"+LogUtil.getLineInfo());//intent字段为空
							WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = true"+LogUtil.getLineInfo());
							return null;
						}
						String aiuitext = cntJson.optString("intent");//得到intent中的数据
						Log.e("MyAIUI","aiuitext:"+aiuitext+LogUtil.getLineInfo());
						aiuitext=aiuitext.replaceAll("\\[n2]|\\[n1]|\\[h1]|\\[h0]|\\[n0]|\\[k3]|\\[k0]|\\[h2]", "");
						JSONObject obj = new JSONObject(aiuitext);//牛逼
						if(obj.has("service")){
							//category处理
							if("baike".equals(obj.getString("service"))){
								JSONArray ja = obj.getJSONObject("data").getJSONArray("result");
								int jalength = ja.length();
								for(int i = 0;i < jalength;i++){
									Object categorySub = new JSONTokener(ja.getJSONObject(i).getString("category")).nextValue();
									if(categorySub instanceof JSONArray){
										int categorySubLength = ((JSONArray) categorySub).length();
										if(categorySubLength == 0){
											String categoryStr = "";
											ja.getJSONObject(i).put("category",categoryStr);
											aiuitext = obj.toString();
											return aiuitext;	
										}
										String categoryStr = new String(((JSONArray) categorySub).getString(0));
										ja.getJSONObject(i).put("category",categoryStr);
										aiuitext = obj.toString();
										return aiuitext;
									}else if(categorySub instanceof JSONObject){
										
									}
								}
							}
						}
						if(!obj.has("semantic")){
							return aiuitext;
						}
						Object listarray = new JSONTokener(obj.getString("semantic")).nextValue();
						if(listarray instanceof JSONArray){
						}else if(listarray instanceof JSONObject){
							Object slots = new JSONTokener(((JSONObject) listarray).getString("slots")).nextValue();
							if(slots instanceof JSONArray){
							}else if(slots instanceof JSONObject){
								JSONArray jaySlot = new JSONArray();
								jaySlot.put((JSONObject)slots);
								((JSONObject)listarray).put("slots",jaySlot);
							}
							JSONArray jay = new JSONArray();
							jay.put((JSONObject)listarray);
							obj.put("semantic",jay);
							aiuitext = obj.toString();
						}		
						return aiuitext;
					}else if("iat".equals(sub)){//流式识别的情况
						WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = true"+LogUtil.getLineInfo());
						return null;
					}
				}else if ("tts".equals(sub)) {
					WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = true"+LogUtil.getLineInfo());
					return null;
				}
			}
		}catch(Exception e){
			LogUtil.e("exception", "解析语义异常", e);
		}
		return null;
	}

	/**
	 * AIUI初始化,创建AIUIAgent
	 */
	private void createAgent(){
		mAIUIAgent = AIUIAgent.createAgent(MainApplication.getContext(), getAIUIParams(),this);
		if (null == mAIUIAgent) {
			final String strErrorTip = "创建AIUIAgent失败！";
			LogUtil.i(TAG, strErrorTip);
		} else {
			LogUtil.i(TAG, "AIUIAgent已创建");
		}
	}

	/**
	 * 获取aiui的状态
	 * @return
	 */
	private String getAIUIParams() {
		String params = "";
		AssetManager assetManager = MainApplication.getContext().getResources().getAssets();
		try {
			InputStream ins = assetManager.open("cfg/aiui_phone.cfg");
			byte[] buffer = new byte[ins.available()];
			ins.read(buffer);
			ins.close();
			params = new String(buffer);
			JSONObject paramsJson = new JSONObject(params);
			paramsJson.getJSONObject("login").put("appid",MainApplication.getContext().getString(R.string.app_id));
			params = paramsJson.toString();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return params;
	}

	/**
	 * 发送录音数据到AIUI引擎
	 * @param enable 为true时,进行语义
	 */
	public void sendAudioDataToAIUI(boolean enable){
		if(enable){
			Into_STATE_WORKING();
			WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = true");
		}else{
			//stopWritingToAIUI();
			WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
		}
	}

	/**
	 * AIUI引擎进入工作状态(working)
	 */
	public void Into_STATE_WORKING(){
		if (AIUIConstant.STATE_WORKING != mAIUIState) {
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
			mAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * AIUI引擎进入准备状态(ready),保留上下文
	 */
	public void Into_STATE_READY(){
		WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
		if(AIUIConstant.STATE_READY != mAIUIState){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_RESET_WAKEUP, 0, 0, "", null);
			mAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * 重置AIUI引擎,可以清除上下文
	 */
	public void resetAIUI(){
		AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_RESET, 0, 0, "", null);
		mAIUIAgent.sendMessage(wakeupMsg);
	}

	/**
	 * 向AIUI引擎写入数据
	 */
	public void writeAudioData(byte[] audioData){
		WRITEAUDIOEABLE = true;
		String params = "data_type=audio,sample_rate=16000";
		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0,params, audioData);
		mAIUIAgent.sendMessage(msg);
	}

	/**
	 * 停止向AIUI写入数据
	 */
	public void stopWritingToAIUI(){
		if(AIUIConstant.STATE_WORKING == mAIUIState){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_STOP_WRITE, 0, 0, "", null);
			mAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * 发送CMD_START,AIUI会转换为待唤醒状态
	 */
	public void startAIUI(){
		if(mAIUIAgent != null){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_START, 0 ,0, "", null);
			mAIUIAgent.sendMessage(wakeupMsg);
		}
	}
}
