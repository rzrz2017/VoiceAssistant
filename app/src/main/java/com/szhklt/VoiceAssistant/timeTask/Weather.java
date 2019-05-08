package com.szhklt.VoiceAssistant.timeTask;

import android.content.Context;
import android.content.SharedPreferences;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.beam.WeatherData;
import com.szhklt.VoiceAssistant.component.MyTextUnderstander;
import com.szhklt.VoiceAssistant.db.WeatherDBHandler;
import com.szhklt.VoiceAssistant.util.JsonParse;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class Weather {
	private static final String TAG = "Weather";
	
	public void queryWeather(){
		//地理位置存入share
		SharedPreferences pref;
		pref = MainApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE);
		String location = pref.getString("location", "深圳"); 
		LogUtil.e(TAG,"location:"+location);
		
		//默认查询天气
		final WeatherDBHandler mwWeatherDBHandler = new WeatherDBHandler(MainApplication.getContext());
		new MyTextUnderstander().understandText("今天"+location+"天气",new TextUnderstanderListener(){
			@Override
			public void onResult(UnderstanderResult result) {
				LogUtil.e(TAG,"天气定时查询返回！"+LogUtil.getLineInfo());
				String json = result.getResultString();
				JsonParse jp = new JsonParse(MainApplication.getContext());
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
