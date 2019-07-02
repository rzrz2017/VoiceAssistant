package com.szhklt.VoiceAssistant.timeTask;

import android.content.Context;
import android.content.SharedPreferences;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.db.WeatherDBHandler;
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
//		MyAIUI.understandText("今天"+location+"天气");

	}
}
