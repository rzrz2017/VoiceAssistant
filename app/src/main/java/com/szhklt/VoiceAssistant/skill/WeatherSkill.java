package com.szhklt.VoiceAssistant.skill;

import android.content.Context;
import android.content.SharedPreferences;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.beam.TimeUtil;
import com.szhklt.VoiceAssistant.beam.WeatherData;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.db.WeatherDBHandler;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;


public class WeatherSkill extends Skill {
	private static final String TAG = WeatherSkill.class.getSimpleName(); 
	private WeatherDBHandler mweatherDBHandler;
	private List<WeatherData> weatherdataarray;
	private Context context;
	public WeatherSkill(intent intent){
		super(intent);
		context = MainApplication.getContext();
		mintent = intent;
		mweatherDBHandler = new WeatherDBHandler(MainApplication.getContext());
	}
	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();

		weatherdataarray = WeatherXUnderstander(mintent);
		SharedPreferences pref = context.getSharedPreferences("location",context.MODE_PRIVATE);
		if(weatherdataarray.size() == 0){
			return;
		}
		if(pref.getString("location","~").contains(weatherdataarray.get(0).getcity())){
			mweatherDBHandler.deleteAllWeatherMsg();
			mweatherDBHandler.insertAWeekOfWeatherMsg(weatherdataarray);
		}
	}
	
	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		super.execute();
	}
	
	public List<WeatherData> WeatherXUnderstander(intent intent){
		TimeUtil mtimeutil = new TimeUtil(MainApplication.getContext());
		mtimeutil.Date_Func_Array();
		List<WeatherData> weatherlistarray = new ArrayList<>(7);
		for(int i = 0;i < 7;i++){
			WeatherData weatherdata =new WeatherData();
			if(i == 0){
				weatherdata.setairData(intent.getData().getResult().get(i).getAirData());
				weatherdata.setairQuality(intent.getData().getResult().get(i).getAirQuality());
				weatherdata.sethumidity(intent.getData().getResult().get(i).getHumidity());
				weatherdata.setpm25(intent.getData().getResult().get(i).getPm25());
				weatherdata.settemp(intent.getData().getResult().get(i).getTemp());
			}
			LogUtil.e("AnswerText",intent.getAnswer().getText());
			weatherdata.setAnswerText(intent.getAnswer().getText());
			weatherdata.setcity(intent.getData().getResult().get(i).getCity());
			weatherdata.setdata(intent.getData().getResult().get(i).getDate());
			weatherdata.setlastUpdateTime(intent.getData().getResult().get(i).getLastUpdateTime());
			weatherdata.setpm25(intent.getData().getResult().get(i).getTempHigh());
			weatherdata.settempRange(intent.getData().getResult().get(i).getTempRange());
			weatherdata.setweather(intent.getData().getResult().get(i).getWeather());
			weatherdata.setweatherType(intent.getData().getResult().get(i).getWeatherType());
			weatherdata.setwind(intent.getData().getResult().get(i).getWind());
			weatherdata.setwindLevel(intent.getData().getResult().get(i).getWindLevel());
			weatherdata.setWriteInTime(mtimeutil.getTodaysDate());
			weatherlistarray.add(weatherdata);
			LogUtil.e(TAG,weatherdata.toString()+LogUtil.getLineInfo());
		}


		return weatherlistarray;
	}
}
