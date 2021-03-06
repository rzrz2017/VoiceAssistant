package com.szhklt.VoiceAssistant.component;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iflytek.aiui.AIUIAgent;
import com.iflytek.aiui.AIUIConstant;
import com.iflytek.aiui.AIUIEvent;
import com.iflytek.aiui.AIUIListener;
import com.iflytek.aiui.AIUIMessage;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.MusicRes.KwMusicSkill;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.activity.ChatActivity;
import com.szhklt.VoiceAssistant.beam.SemanticUnderstandResultData;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.broadcastReceiver.AlarmReceiver;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.impl.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.service.TrafficStatisticsService;
import com.szhklt.VoiceAssistant.skill.AlarmSkill;
import com.szhklt.VoiceAssistant.skill.CookBookSkill;
import com.szhklt.VoiceAssistant.skill.IntoChatSkill;
import com.szhklt.VoiceAssistant.skill.KeyeventControlSkill;
import com.szhklt.VoiceAssistant.skill.LightControlSkill;
import com.szhklt.VoiceAssistant.skill.MediaPlaySkill;
import com.szhklt.VoiceAssistant.skill.MusicControlSkill;
import com.szhklt.VoiceAssistant.skill.OpenQASkill;
import com.szhklt.VoiceAssistant.skill.Skill;
import com.szhklt.VoiceAssistant.skill.TranslationSkill;
import com.szhklt.VoiceAssistant.skill.VolumeControlSkill;
import com.szhklt.VoiceAssistant.skill.WeatherSkill;
import com.szhklt.VoiceAssistant.skill.WebSearchSkill;
import com.szhklt.VoiceAssistant.timeTask.NoSpeekTimeout;
import com.szhklt.VoiceAssistant.timeTask.ResultTimeout;
import com.szhklt.VoiceAssistant.timeTask.SpeekTimeout;
import com.szhklt.VoiceAssistant.util.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;


public class MyAIUI implements AIUIListener{
	public static final String TAG = "MyAIUI";
	private volatile static MyAIUI instance;
	public static boolean WRITEAUDIOEABLE = false;// 是否写入音频到语义
	private static AIUIAgent sAIUIAgent;
	private static int sAIUIState = AIUIConstant.STATE_IDLE;//AIUI当前状态
	private MySynthesizer mTts;
	private FloatWindowManager mFWM;
	private Skill skill;//技能引用

	private MyAIUI(){
		createAgent();
		mTts = MySynthesizer.getInstance(MainApplication.getContext());//初始化tts
		mFWM = FloatWindowManager.getInstance();
		mFWM.setOnClickListener(new View.OnClickListener() {
			@Override
			public   void onClick(View v) {
				LogUtil.e(TAG,"大海豚被点击!!!"+ LogUtil.getLineInfo());
				stopSemanticInteraction();
				mFWM.removeAll();
			}
		});
	}

	public static MyAIUI getInstance(){
		if(instance == null){
			synchronized (MyAIUI.class) {
				if(instance == null){
					instance = new MyAIUI();
				}
			}
		}
		return instance;
	}

	/**
	 * 文本语义
	 */
	public static void understandText(String strContent){
		Log.e(TAG,"text understand");
		sendAudioDataToAIUI(true);
		byte[] content = strContent.getBytes();
		String params = "data_type=text";
		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE,0,0,params,content);
		sAIUIAgent.sendMessage(msg);
	}

	/**
	 * 停止语音交互
	 */
	private void stopSemanticInteraction(){
		LogUtil.e("mFWM"," removeAll()"+ LogUtil.getLineInfo());
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
				mFWM.removeAll();
				break;
			case AIUIConstant.EVENT_WAKEUP:
				LogUtil.e(TAG, "进入识别状态");
				WRITEAUDIOEABLE = true;
				break;
			case AIUIConstant.EVENT_RESULT:
				Log.e(TAG,"语义理解完成返回");
				MySynthesizer.FLAG = false;
				MySynthesizer.recoveyFlag();
				Log.e(TAG+".......", String.valueOf(event));
				String xftext = preproccessResultData(event);//预处理数据
				
				if(xftext == null){
					break;
				}
				LogUtil.e("xftext","xftext:"+xftext+LogUtil.getLineInfo());

				handle(xftext);
			//TTS回调监听
			case AIUIConstant.EVENT_TTS:
				switch (event.arg1){
					case AIUIConstant.TTS_SPEAK_BEGIN:
						LogUtil.e(TAG,"开始播放");
						mTts.onSpeakBegin();
						break;
					case AIUIConstant.TTS_SPEAK_PROGRESS:
						LogUtil.e(TAG,"缓冲进度为...");
//						mTts.onBufferProgress();
						break;
					case AIUIConstant.TTS_SPEAK_PAUSED:
						LogUtil.e(TAG,"暂停播放");
						mTts.onSpeakPaused();
						break;
					case AIUIConstant.TTS_SPEAK_RESUMED:
						LogUtil.e(TAG,"恢复播放");
						mTts.onSpeakResumed();
						break;
					case AIUIConstant.TTS_SPEAK_COMPLETED:
						LogUtil.e(TAG,"播放完成");
						mTts.onCompleted();
						break;
						default:
							break;
				}
			case AIUIConstant.EVENT_ERROR:
				LogUtil.e(TAG, "AIUI引擎错误: " + event.arg1 + "\n" + event.info);
//				if(!ChatActivity.ISCHATMODE){
//					LogUtil.e("now","----------------"+LogUtil.getLineInfo());
//					WRITEAUDIOEABLE = false;//禁止长唤醒
//				}
				if(event.arg1 == 10120){//网络问题
					Toast.makeText(MainApplication.getContext(),"网络不稳定",Toast.LENGTH_SHORT).show();
				}
				break;
			case AIUIConstant.EVENT_VAD:
				if (AIUIConstant.VAD_BOS == event.arg1) {
					//前节点(event.arg1取值为0)
					LogUtil.e("iat", "前节点");
					LogUtil.e(TAG, "开始说话");

					if(MyCAE.isWakeUped == false){
						//之前没有唤醒过
						//保存播放器唤醒前的状态
						MyCAE.savePlayerStatusBeforeWakeup();
					}else{
						MyCAE.isWakeUped = false;
					}
					MainApplication.getContext().sendBroadcast(new Intent(TrafficStatisticsService.TRAFFIC_STATISTICS_STARTSTATISTICS));

					if(!ChatActivity.ISCHATMODE){
						mFWM.startComprehendAnimation();//拿话筒动作
					}
				}else if(AIUIConstant.VAD_EOS == event.arg1) {
					//后节点(event.arg1取值为2)
					if(!ChatActivity.ISCHATMODE){
						if(MainApplication.longWakeUp == false){
							LogUtil.e("iat","WRITEAUDIOEABLE = false"+LogUtil.getLineInfo());
							LogUtil.e("now","----------------"+LogUtil.getLineInfo());
							WRITEAUDIOEABLE = false;//禁止长唤醒
						}
					}
					LogUtil.e("iat", "后节点");
					MainApplication.getContext().sendBroadcast(new Intent(TrafficStatisticsService.TRAFFIC_STATISTICS_ENDSTATISTICS));

					SpeekTimeout.getInstance().stop();
					mFWM.startThinkAnimation();//抬眼睛思考
					MainService.volume_value = 0;
					LogUtil.e(TAG, "停止语义");
				}else{
					//当event.arg1取值为1，event.arg2为音量值
					NoSpeekTimeout.getInstance().stop();//当检测到有音量值，停止定时任务
					MainService.volume_value = event.arg2;
					LogUtil.e("volume",String.valueOf(event.arg2));
				}
				break;
			case AIUIConstant.EVENT_SLEEP:
				LogUtil.e(TAG,"进入睡眠");
				WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());

				if(MySynthesizer.getIsTtsed() == false){//判断这次交互否是有过tts
					MySynthesizer.recoveyMusicStatusBeforeWakeUp();//恢复唤醒前状态
				}else{
					MySynthesizer.setIsTtsed(false);
				}

				mFWM.removeAll();

				if(ChatActivity.ISCHATMODE == true){
					Intent sintent = new Intent("com.szhklt.msg.closeActivity.ChatActivity");
					MainApplication.getContext().sendBroadcast(sintent);
				}

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
	 * 预处理AIUI返回的结果
	 */
	private String preproccessResultData(AIUIEvent event) {
		Log.e(TAG,"HandlerJson");
		Log.e(TAG,"event"+event);
		try {
			JSONObject Json = new JSONObject(event.info);
			JSONObject data = Json.getJSONArray("data").getJSONObject(0);
			JSONObject params = data.getJSONObject("params");
			JSONObject content = data.getJSONArray("content").getJSONObject(0);//得到content,判断是否有cnt_id

			Log.e("event","event.info:"+event.info+LogUtil.getLineInfo());
			String sub = params.optString("sub");//得到sub字段，根据此字段判断是iat或者nlp
			if(content.has("cnt_id")){
				//判断有无cnt_id
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
						Log.e(TAG,"开始处理nlp");
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
					}else if("iat".equals(sub)){
						//流式识别的情况
						Log.e(TAG,"开始处理iat");
						WRITEAUDIOEABLE = true;LogUtil.e("iat", "WRITEAUDIOEABLE = true"+LogUtil.getLineInfo());
						return null;
					}
				}else if ("tts".equals(sub)) {
					String sid = event.data.getString("sid");
					byte[] audio = event.data.getByteArray(cnt_id);

					int dts = content.getInt("dts");
					int frameId = content.getInt("frame_id");
					int percent = event.data.getInt("percent");//合成进度
					boolean isCancel = "1".equals(content.getString("cancel"));

					StringBuffer stringBuffer = new StringBuffer();
					stringBuffer.append("dts:");
					stringBuffer.append(dts);
					stringBuffer.append("frameId:");
					stringBuffer.append(frameId);
					stringBuffer.append("percent:");
					stringBuffer.append(percent);
					stringBuffer.append("isCancel:");
					stringBuffer.append(isCancel);
					LogUtil.e(TAG,stringBuffer.toString()+LogUtil.getLineInfo());

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
	 * 主动语音合成
	 * @param ttsStr 需要合成的文字
	 * @param arg1 类型(START CANCEL PAUSE RESUME)
	 */
	public static void activeTTS(String ttsStr,int arg1) throws UnsupportedEncodingException {
		LogUtil.e(TAG,"主动语音合成"+LogUtil.getLineInfo());
		byte[] ttsData;
		StringBuffer params;

		if(ttsStr != null) {
			ttsData = ttsStr.getBytes("utf-8");
			params = new StringBuffer();
			params.append("vcn=x_chongchong");
			params.append(",speed=50");
			params.append(",pitch=50");
			params.append(",volume=60");
		}else{
			ttsData = "".getBytes("utf-8");
			params = new StringBuffer();
		}

		//开始合成
		AIUIMessage startTts = new AIUIMessage(AIUIConstant.CMD_TTS,arg1,0,params.toString(),ttsData);
		sAIUIAgent.sendMessage(startTts);
	}

	/********************************************************************/
	/**
	 * 处理理解完成后RC等于0的情况(操作成功)
	 */
	private void disposeAfterComprehendedRC0(final intent mintent) {
		LogUtil.e("dispose","disposeAfterComprehendedRC0()"+LogUtil.getLineInfo());

		if(mintent == null){
			return;
		}
        String service = mintent.getService();
		LogUtil.e("musicstate", "service:"+service+LogUtil.getLineInfo());
		if("weather".equals(service)){
			skill = new WeatherSkill(mintent);
			skill.execute();
		}else if("cookbook".equals(service)){
			skill = new CookBookSkill(mintent);
			skill.execute();
		}else if("translation".equals(service)){
			skill = new TranslationSkill(mintent);
			skill.execute();
		}else if ("musicX".equals(service)) {// 酷我
			skill = new KwMusicSkill(mintent);
			skill.execute();
//			skill = new CocheerSkill(mintent);
//			skill.execute();
//			skill = new MiGuSkill(mintent);
//			skill.execute();
		}else if("scheduleX".equals(service)){//闹钟
			skill = new AlarmSkill(mintent);
			skill.execute();
		}else if("HKLT.music_ctr".equals(service)){//上下曲,暂停控制
			skill = new MusicControlSkill(mintent);
			skill.execute();
		}else if("HKLT.light_up_down".equals(service)){//亮度控制
			skill = new LightControlSkill(mintent);
			skill.execute();
		}else if("HKLT.volume_up_down".equals(service)){//音量控制
			skill = new VolumeControlSkill(mintent);
			skill.execute();
		}else if("HKLT.keyevent".equals(service)){
			skill = new KeyeventControlSkill(mintent);
			skill.execute();
		} else if (service.equals("news")
				|| service.equals("radio")
				|| service.equals("story")
				|| service.equals("drama")
				|| service.equals("crossTalk")
				|| service.equals("storyTelling")
				|| service.equals("health")
				|| service.equals("history")) {
			skill = new MediaPlaySkill(mintent);
			skill.execute();
		}else if("baike".equals(service)){
			skill = new WebSearchSkill(mintent);
			skill.execute();
		}else if("openQA".equals(service)){
			skill = new OpenQASkill(mintent);
			skill.execute();
		}else if("HKLT.intoaiui".equals(service)){
			skill = new IntoChatSkill(mintent);
			skill.execute();
		}else{
			skill = new Skill(mintent);
			skill.execute();
		}
	}

	/**
	 * 处理理解完成后RC等于1的情况(输入异常)
	 */
	private void disposeAfterComprehendedRC1(final intent responseMsg){
		LogUtil.e("dispose","disposeAfterComprehendedRC1()"+LogUtil.getLineInfo());
	}
	/**
	 * 处理理解完成后RC等于2的情况(系统内部异常)
	 */
	private void disposeAfterComprehendedRC2(final intent responseMsg){
		LogUtil.e("dispose","disposeAfterComprehendedRC2()"+LogUtil.getLineInfo());
	}

	/**
	 * 处理理解完成后RC等于3的情况(业务操作失败)
	 */
	private void disposeAfterComprehendedRC3(final intent mintent){
		LogUtil.e("dispose","disposeAfterComprehendedRC3()"+LogUtil.getLineInfo());
		final SemanticUnderstandResultData data = new SemanticUnderstandResultData();
		data.awr.setAnsw(mintent.getText());
		data.setintent(mintent.getSemantic().get(0).getIntent());
		sendAudioDataToAIUI(false);
		Log.e("stop","sendAudioDataToAIUI(false)被执行!"+LogUtil.getLineInfo());

		//上下首切换时可能会有RC == 3的情况
		if("musicX".equals(mintent.getService())){
			skill = new KwMusicSkill(mintent);
			skill.execute();
			return;
		}

		if (mintent.getService().indexOf("story") != -1) {
			if (data.awr.getAnsw() == null) {
				mTts.doSomethingAfterTts(null, "这个我还在努力签约中哦", null);
			} else {
				if (data.awr.getAnsw().indexOf("学习") != -1) {
					mTts.doSomethingAfterTts(null, "这个我还在努力签约中哦", null);
				}
			}
		} else if (mintent.getService().indexOf("weather") != -1) {// 天气
			mTts.doSomethingAfterTts(null,data.awr.getAnsw(),data.awr.getAnsw());
		} else if (mintent.getService().indexOf("scheduleX") != -1) {// 取消和改变闹钟
			if (data.getintent().equals("CANCEL")) {
				stopRemind();
				mTts.doSomethingAfterTts(null,data.awr.getAnsw(),data.awr.getAnsw());
			} else if (data.getintent().equals("CHANGE")) {
				stopRemind();
				mTts.doSomethingAfterTts(null,"该功能还在维护中",data.awr.getAnsw());
			}
		}else if (mintent.getService().equals("news")
				|| mintent.getService().equals("radio")
				|| mintent.getService().equals("story")
				|| mintent.getService().equals("drama")
				|| mintent.getService().equals("crossTalk")
				|| mintent.getService().equals("storyTelling")
				|| mintent.getService().equals("health")
				|| mintent.getService().equals("history")) {
			mTts.speechSynthesis(mintent.getAnswer().getText(),null);
		}else{
			ResultTimeout.getInstance().restart();
		}
	}

	/**
	 * 处理理解完成后RC等于4的情况
	 */
	private void disposeAfterComprehendedRC4(final intent mintent){
		LogUtil.e("dispose","disposeAfterComprehendedRC4()"+LogUtil.getLineInfo());
		if(MainApplication.longWakeUp == true){
			return;
		}

		if(ChatActivity.ISCHATMODE == false){
			MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
			String question = mintent.getText();
			mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
				@Override
				public void doSomethingsAfterTts() {
					// TODO Auto-generated method stub
					mFWM.removeAll();
				}
			},"抱歉,我没有听懂,或许换个说法我就听明白了",question);
		}
		return;
		//发送给后台服务器
		//if (question.length() > 5) {
		//  try {
		//      if (location == null) {
		//      location = "未知";
		//  }
		//      mUtil.httpsend(location,question,"");
		//  } catch (UnsupportedEncodingException e) {
		//      e.printStackTrace();
		//  }
		//}
	}

	/**
	 * 关闭提醒
	 */
	private void stopRemind() {
		Intent intent = new Intent(MainApplication.getContext(), AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(MainApplication.getContext(), 0,intent, 0);
		AlarmManager am = (AlarmManager) MainApplication.getContext().getSystemService(MainApplication.getContext().ALARM_SERVICE);
		am.cancel(pi);
		Toast.makeText(MainApplication.getContext(), "关闭了提醒", Toast.LENGTH_SHORT).show();
	}

	/**
	 * AIUI初始化,创建AIUIAgent
     * 调用AIUIAgent.createAgent创建对象之后，服务即为就绪状态。
	 */
	private void createAgent(){
		sAIUIAgent = AIUIAgent.createAgent(MainApplication.getContext(), getAIUIParams(),this);
		if (null == sAIUIAgent) {
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
	 * @param enable 为true时,进行语义
	 */
	public static void sendAudioDataToAIUI(boolean enable){
		if(enable){
			Into_STATE_WORKING();
			WRITEAUDIOEABLE = true;
			LogUtil.e("iat", "WRITEAUDIOEABLE = true");
		}else{
			//stopWritingToAIUI();
			WRITEAUDIOEABLE = false;
			LogUtil.e("now","----------------"+LogUtil.getLineInfo());
		}
	}

	/**
	 * AIUI引擎进入工作状态(working)
	 */
	public static void Into_STATE_WORKING(){
		if (AIUIConstant.STATE_WORKING != sAIUIState) {
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_WAKEUP, 0, 0, "", null);
			sAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * AIUI引擎进入准备状态(ready),保留上下文
	 */
	public void Into_STATE_READY(){
		WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
		if(AIUIConstant.STATE_READY != sAIUIState){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_RESET_WAKEUP, 0, 0, "", null);
			sAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * 重置AIUI引擎,可以清除上下文
	 */
	public void resetAIUI(){
		AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_RESET, 0, 0, "", null);
		sAIUIAgent.sendMessage(wakeupMsg);
	}

	/**
	 * 向AIUI引擎写入数据
	 */
	public void writeAudioData(byte[] audioData){
		WRITEAUDIOEABLE = true;
		String params = "data_type=audio,sample_rate=16000";
		AIUIMessage msg = new AIUIMessage(AIUIConstant.CMD_WRITE, 0, 0,params, audioData);
		sAIUIAgent.sendMessage(msg);
	}

	/**
	 * 停止向AIUI写入数据
	 */
	public void stopWritingToAIUI(){
		if(AIUIConstant.STATE_WORKING == sAIUIState){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_STOP_WRITE, 0, 0, "", null);
			sAIUIAgent.sendMessage(wakeupMsg);
		}
	}

	/**
	 * 发送CMD_START,AIUI会转换为待唤醒状态
	 */
	public void startAIUI(){
		if(sAIUIAgent != null){
			AIUIMessage wakeupMsg = new AIUIMessage(AIUIConstant.CMD_START, 0 ,0, "", null);
			sAIUIAgent.sendMessage(wakeupMsg);
		}
	}


    //解析结果
	public void handle(String string){
		//智能家居
		try {
			JSONObject obj = new JSONObject(string);
//			MyAIUI.SMARTQUESTION = obj.getString("text");
			String service = obj.optString("service","");
			LogUtil.e(TAG,"service:"+service+LogUtil.getLineInfo());
		} catch (JSONException e) {
			e.printStackTrace();
		}

		intent mintent = new intent();
		Gson mgson = new GsonBuilder().serializeNulls().create();
		mintent = mgson.fromJson(string,intent.class);
		if(mintent == null){
			FloatWindowManager.getInstance().removeAll();
			return;
		}else{
			switch (mintent.getRc()) {
				case 0:
					disposeAfterComprehendedRC0(mintent);
					break;
				case 1:
					disposeAfterComprehendedRC1(mintent);
					break;
				case 2:
					disposeAfterComprehendedRC2(mintent);
					break;
				case 3:
					disposeAfterComprehendedRC3(mintent);
					break;
				case 4:
					disposeAfterComprehendedRC4(mintent);
					break;
				default:
					break;
			}
			//技能执行结束
			skillExeComSubOpt();//在tts播放完成之前
			mintent = null;
		}
		skill = null;
	}

}
