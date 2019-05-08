package com.szhklt.VoiceAssistant.timeTask;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import android.content.Context;
import android.content.SharedPreferences;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.db.WeatherDBHandler;
import com.szhklt.VoiceAssistant.util.LogUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

public class Location {
	private static final String TAG = "Location";
	private WeatherDBHandler mwWeatherDBHandler;
	private String location;
	
	public Location(){
		mwWeatherDBHandler = new WeatherDBHandler(MainApplication.getContext());
	}
	
	public void getPositionInfo() {
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
				new Weather().queryWeather();
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
			//地理位置存入share
			SharedPreferences.Editor editor;
			editor = MainApplication.getContext().getSharedPreferences("location", Context.MODE_PRIVATE).edit();
			editor.putString("location",location);
			editor.apply();
			LogUtil.e(TAG,"location:"+location);
		}
	}
}
