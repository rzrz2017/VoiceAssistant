package com.szhklt.www.VoiceAssistant.timeTask;

import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.beam.WeatherData;
import com.szhklt.www.VoiceAssistant.component.MyTextUnderstander;
import com.szhklt.www.VoiceAssistant.db.WeatherDBHandler;
import com.szhklt.www.VoiceAssistant.util.JsonParse;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class WeatherTimeTask {
	private static final String TAG = "weatherTimeTask";
	private String location;

	private Context context = MainApplication.getContext();
	private TimerUtil timerUtil;
	private static WeatherTimeTask weatherTimeTask;
	private WeatherDBHandler mwWeatherDBHandler = new WeatherDBHandler(context);
	private WeatherTimeTask (){
	}
	public static WeatherTimeTask getInstance(){
		if(weatherTimeTask==null){
			weatherTimeTask= new WeatherTimeTask();
		}
		return weatherTimeTask;
	}
	
	private void start(){
		timerUtil = new  TimerUtil(new TimerTask() {
			@Override
			public void run() {
				//查询地理位置
				getPositionInfo();
				//默认查询天气
				new MyTextUnderstander().understandText("今天"+location==null?"深圳":location+"天气",
						new TextUnderstanderListener(){
					@Override
					public void onResult(UnderstanderResult result) {
						LogUtil.e(TAG,"天气定时查询返回！"+LogUtil.getLineInfo());
						String json = result.getResultString();
						JsonParse jp = new JsonParse(context);
						WeatherData[] weatherdataarray = jp.WeatherXUnderstander(json);	
						mwWeatherDBHandler.deleteAllWeatherMsg();
						mwWeatherDBHandler.insertAWeekOfWeatherMsg(weatherdataarray);
					}
					
					@Override
					public void onError(SpeechError arg0) {
						LogUtil.e(TAG,"更新天气失败"+LogUtil.getLineInfo() 
								+"\n"+"错误码："+arg0.getErrorCode()
								+"\n"+"错误描述："+arg0.getErrorDescription());
					}
				});
			}
		},0);
		timerUtil.scheduleRepeat(0,2*60*60*1000);//两小时
	}
	
	public void restart(){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
		start();
	}
	public void stop(){
		if(timerUtil!=null){
			timerUtil.cancel();
		}
	}
	/******************************************************/
	/**
	 * 获取地理位置信息并请求天气信息
	 */
	private void getPositionInfo() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					LogUtil.e(TAG, "地理位置信息信息");
					HttpClient httpClient = new DefaultHttpClient();
					HttpGet httpGet = new HttpGet("http://2018.ip138.com/ic.asp");
					HttpResponse httpResponse = httpClient.execute(httpGet);
					if (httpResponse.getStatusLine().getStatusCode() == 200){
						HttpEntity entity = httpResponse.getEntity();
						String response = EntityUtils.toString(entity,"gb2312");
						parseXMLWithPull(response.replaceAll("\r|\n", ""));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				//获取天气
//				getWeatherInformation();
			}
		}).start();
	}	
	
	private void parseXMLWithPull(String xmlData) {
		Pattern p=Pattern.compile(">([^<>]*)<");  
		Matcher m=p.matcher(xmlData);  
		int i = 0;
		while(m.find()){   
			i++;
			String line = m.group(1);
			parseWeatherInfo(i,line);
		}
	}  
	
	/**
	 * 天气
	 * @param i
	 * @param line
	 */
	private void parseWeatherInfo(int i, String line) {
		if(8 == i){
			String[] sArray1=line.split(" ");
			String[] sArray2=sArray1[1].split("：");
			String [] temp = null;
			temp = sArray2[1].split("省");
			location=temp[1];
			LogUtil.e("location","location:"+location+LogUtil.getLineInfo());
			//地理位置存入share
			SharedPreferences.Editor editor;
			editor = MainApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE).edit();
			editor.putString("location",location);
			editor.apply();
			LogUtil.e("location","location:"+location);
			//查询天气
			new MyTextUnderstander().understandText("今天"+location==null?"深圳":location+"天气",new TextUnderstanderListener(){
				@Override
				public void onResult(UnderstanderResult result) {
					LogUtil.e(TAG,"天气定时查询返回！"+LogUtil.getLineInfo());
					String json = result.getResultString();
					JsonParse jp = new JsonParse(context);
					WeatherData[] weatherdataarray = jp.WeatherXUnderstander(json);
					mwWeatherDBHandler.deleteAllWeatherMsg();
					mwWeatherDBHandler.insertAWeekOfWeatherMsg(weatherdataarray);
				}
				
				@Override
				public void onError(SpeechError arg0) {
					LogUtil.e(TAG,"更新天气失败"+LogUtil.getLineInfo() 
							+"\n"+"错误码："+arg0.getErrorCode()
							+"\n"+"错误描述："+arg0.getErrorDescription());
				}
			});
		}
	}
	
	/**
	 * 发送广播给背景音箱APK，获取天气数据
	 * 需要在获取了位置信息后进行
	 */
	private void getWeatherInformation() {
		LogUtil.e(TAG, "发送请求天气的广播"+"------------最近"+location+"天气怎么样");
		Intent updataWeatherInfoIntent = new Intent("android.action.HKLT_UPDATA_GETWEATHERINFO");
		SharedPreferences sp = MainApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
		location = sp.getString("location","深圳");
		updataWeatherInfoIntent.putExtra("text","最近"+location+"天气怎么样");
		MainApplication.getContext().sendBroadcast(updataWeatherInfoIntent);
	}
}
