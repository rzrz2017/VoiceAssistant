package com.szhklt.www.voiceassistant.util;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;
import com.szhklt.www.voiceassistant.beam.TimeUtil;
import com.szhklt.www.voiceassistant.beam.WeatherData;

import android.content.Context;


public class JsonParse {
	public Context context;

	public JsonParse(Context context) {
		super();
		this.context = context;
	}
	
	public WeatherData[] WeatherXUnderstander(String json){
		TimeUtil mtimeutil = new TimeUtil(context);
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
		        		}	        		
		        	}
		        }
		} catch (Exception e) {
			
		}       	
		return weatherlistarray;	
	}
	
}
