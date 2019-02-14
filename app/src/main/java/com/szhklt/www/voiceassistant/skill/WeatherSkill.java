package com.szhklt.www.voiceassistant.skill;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import android.content.Context;
import android.content.SharedPreferences;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.beam.TimeUtil;
import com.szhklt.www.voiceassistant.beam.WeatherData;
import com.szhklt.www.voiceassistant.beam.intent;
import com.szhklt.www.voiceassistant.db.WeatherDBHandler;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class WeatherSkill extends Skill {
	private static final String TAG = WeatherSkill.class.getSimpleName(); 
	private String weatherJson;
	private WeatherDBHandler mweatherDBHandler;
	private Context context;
	public WeatherSkill(intent intent, String json){
		LogUtil.e(TAG,"json:"+json);
		context = MainApplication.getContext();
		mintent = intent;
		weatherJson = json;
		mweatherDBHandler = new WeatherDBHandler(MainApplication.getContext());
	}
	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		WeatherData[] weatherdataarray;
		weatherdataarray = WeatherXUnderstander(weatherJson);
		SharedPreferences pref = context.getSharedPreferences("location",context.MODE_PRIVATE);
		if(pref.getString("location","~").contains(weatherdataarray[0].getcity())){
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
	
	public WeatherData[] WeatherXUnderstander(String json){
		TimeUtil mtimeutil = new TimeUtil(MainApplication.getContext());
		mtimeutil.Date_Func_Array();
		WeatherData[] weatherlistarray = new WeatherData[7];
		 try {
			 JSONTokener tokener = new JSONTokener(json);
		     JSONObject jsonObject = new JSONObject(tokener);
		     JSONObject jsonData = jsonObject.optJSONObject("data");
		        if(null !=jsonData){
		        	JSONArray jsonArray = jsonData.getJSONArray("result");
		        	if(jsonObject.getString("service").indexOf("weather")!=-1){
		        		for (int i = 0; i <jsonArray.length(); i++) {
		        			JSONObject dataObject = (JSONObject) jsonArray.get(i);	        			
		        			WeatherData weatherdata =new WeatherData();
		        			if(i == 0){
		        				LogUtil.e("AnswerText",jsonObject.optJSONObject("answer").optString("text"));
		        				weatherdata.setAnswerText(jsonObject.optJSONObject("answer").optString("text"));
			        			weatherdata.setairData(dataObject.optInt("airData"));
			        			weatherdata.setairQuality(dataObject.optString("airQuality"));
			        			weatherdata.sethumidity(dataObject.optString("humidity"));
			        			weatherdata.setpm25(dataObject.optString("pm25"));
			        			weatherdata.settemp(dataObject.optInt("temp"));
		        			}
		        			weatherdata.setcity(dataObject.optString("city"));
		        			weatherdata.setdata(dataObject.optString("date"));
		        			weatherdata.setlastUpdateTime(dataObject.optString("lastUpdateTime"));
		        			weatherdata.setpm25(dataObject.optString("pm25"));
		        			weatherdata.settempRange(dataObject.optString("tempRange"));
		        			weatherdata.setweather(dataObject.optString("weather"));
		        			weatherdata.setweatherType(dataObject.optInt("weatherType"));
		        			weatherdata.setwind(dataObject.optString("wind"));
		        			weatherdata.setwindLevel(dataObject.getInt("windLevel"));
		        			weatherdata.setWriteInTime(mtimeutil.getTodaysDate());
		        			weatherlistarray[i] = weatherdata;
		        			LogUtil.e(TAG,weatherdata.toString()+LogUtil.getLineInfo());
		        		}	        		
		        	}
		        }
		} catch (Exception e) {
		}       	
		return weatherlistarray;	
	}
}
