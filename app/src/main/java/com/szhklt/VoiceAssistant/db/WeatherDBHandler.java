package com.szhklt.VoiceAssistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.szhklt.VoiceAssistant.beam.WeatherData;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WeatherDBHandler {
	public SQLiteDatabase weatherDB;
	private Context mcontext;
	private WeatherDBHelper weatherdbHelper;
	private Calendar mcalendar;
	private long mtime;
	private SimpleDateFormat mformat;
	private Date mdate;
	private String mstrdate;
	
	public WeatherDBHandler(Context context) {
		super();
		this.mcontext = context;
		weatherdbHelper = new WeatherDBHelper(mcontext,"weatherinfo.db",null);
		mcalendar = Calendar.getInstance();
		if(weatherDB == null){
			weatherDB = weatherdbHelper.getWritableDatabase();
		}
	    mtime = System.currentTimeMillis(); 
	    mformat = new SimpleDateFormat("yyyy-MM-dd"); 
	    mdate = new Date(mtime); //date
	    mstrdate = mformat.format(mdate); //str
	    LogUtil.e("date","mstrdate:"+mstrdate+ LogUtil.getLineInfo());
	}
	

	public void insertALineOfWeatherMsg(WeatherData weatherData){
		ContentValues values = new ContentValues();
		values.put("AnswerText",weatherData.getAnswerText());
		values.put("WriteInTime", weatherData.getWriteInTime());
		values.put("airData", weatherData.getairData());
		values.put("airQuality", weatherData.getairQuality());
		values.put("city", weatherData.getcity());
		values.put("data", weatherData.getdata());
		values.put("humidity", weatherData.gethumidity());
		values.put("pm25", weatherData.getpm25());
		values.put("temp", weatherData.gettemp());
		values.put("tempRange", weatherData.gettempRange());
		values.put("weather", weatherData.getweather());
		values.put("weatherType", weatherData.getweatherType());
		values.put("wind", weatherData.getwind());
		values.put("windLevel", weatherData.getwindLevel());

		weatherDB.insert("WEATHERINFO", null, values);
	}
	
	public void insertAWeekOfWeatherMsg(List<WeatherData> weatherDataArray){
		for(int i = 0;i < 7;i++){
			ContentValues values = new ContentValues();
			if(i == 0){
				values.put("answer",weatherDataArray.get(i).getAnswerText());
				values.put("airData", weatherDataArray.get(i).getairData());
				values.put("airQuality", weatherDataArray.get(i).getairQuality());
				values.put("pm25", weatherDataArray.get(i).getpm25());
				values.put("humidity", weatherDataArray.get(i).gethumidity());
				values.put("temp", weatherDataArray.get(i).gettemp());
			}
			values.put("WriteInTime", weatherDataArray.get(i).getWriteInTime());
			values.put("city", weatherDataArray.get(i).getcity());
			values.put("data", weatherDataArray.get(i).getdata());
			values.put("lastUpdateTime",weatherDataArray.get(i).getlastUpdateTime());
			values.put("tempRange", weatherDataArray.get(i).gettempRange());
			values.put("weather", weatherDataArray.get(i).getweather());
			values.put("weatherType", weatherDataArray.get(i).getweatherType());
			values.put("wind", weatherDataArray.get(i).getwind());
			values.put("windLevel", weatherDataArray.get(i).getwindLevel());
			weatherDB.insert("WEATHERINFO", null, values);
		}
	}
	
	//D
	public void deleteAllWeatherMsg(){
		weatherDB.execSQL("DELETE FROM WEATHERINFO");
	}
	
	//R
	/**
	 * 返回为空代表没找到
	 * @param Date
	 * @return
	 */
	public WeatherData queryWeatherMsgBydata(String Date){
		Cursor cursor = weatherDB.query("WEATHERINFO",null,null, null, null, null, null);
		WeatherData weatherData = null;
		if(cursor.moveToFirst()){
			do {
				String data = cursor.getString(cursor.getColumnIndex("data"));
				if(data.equals(Date)){
					String WriteInTime = cursor.getString(cursor.getColumnIndex("WriteInTime"));
					int airData = cursor.getInt(cursor.getColumnIndex("airData"));
					String airQuality = cursor.getString(cursor.getColumnIndex("airQuality"));
					String city = cursor.getString(cursor.getColumnIndex("city"));
					String humidity = cursor.getString(cursor.getColumnIndex("humidity"));
					String lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
					String pm25 = cursor.getString(cursor.getColumnIndex("pm25"));
					int temp = cursor.getInt(cursor.getColumnIndex("temp"));
					String tempRange = cursor.getString(cursor.getColumnIndex("tempRange"));
					String weather = cursor.getString(cursor.getColumnIndex("weather"));
					int weatherType = cursor.getInt(cursor.getColumnIndex("weatherType"));
					String wind = cursor.getString(cursor.getColumnIndex("wind"));
					int windLevel = cursor.getInt(cursor.getColumnIndex("windLevel"));

					weatherData = new WeatherData();
					weatherData.setWriteInTime(WriteInTime);
					weatherData.setairData(airData);
					weatherData.setairQuality(airQuality);
					weatherData.setcity(city);
					weatherData.setdata(data);//**
					weatherData.sethumidity(humidity);
					weatherData.setlastUpdateTime(lastUpdateTime);
					weatherData.setpm25(pm25);
					weatherData.settemp(temp);
					weatherData.settempRange(tempRange);
					weatherData.setweather(weather);
					weatherData.setweatherType(weatherType);
					weatherData.setwind(wind);
					weatherData.setwindLevel(windLevel);
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		return weatherData;
	}
	
	//R
	public WeatherData queryWeatherMsgBydata(Date Date){
		WeatherData weatherData = null;
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		String datestr=sdf.format(Date);
		Cursor cursor = weatherDB.query("WEATHERINFO",null,null, null, null, null, null); 
		if(cursor.moveToFirst()){
			do {
				String data = cursor.getString(cursor.getColumnIndex("data"));
				if(data.equals(datestr)){
					String WriteInTime = cursor.getString(cursor.getColumnIndex("WriteInTime"));
					int airData = cursor.getInt(cursor.getColumnIndex("airData"));
					String airQuality = cursor.getString(cursor.getColumnIndex("airQuality"));
					String city = cursor.getString(cursor.getColumnIndex("city"));
					String humidity = cursor.getString(cursor.getColumnIndex("humidity"));
					String lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
					String pm25 = cursor.getString(cursor.getColumnIndex("pm25"));
					int temp = cursor.getInt(cursor.getColumnIndex("temp"));
					String tempRange = cursor.getString(cursor.getColumnIndex("tempRange"));
					String weather = cursor.getString(cursor.getColumnIndex("weather"));
					int weatherType = cursor.getInt(cursor.getColumnIndex("weatherType"));
					String wind = cursor.getString(cursor.getColumnIndex("wind"));
					int windLevel = cursor.getInt(cursor.getColumnIndex("windLevel"));

					weatherData = new WeatherData();
					weatherData.setWriteInTime(WriteInTime);
					weatherData.setairData(airData);
					weatherData.setairQuality(airQuality);
					weatherData.setcity(city);
					weatherData.setdata(data);//**
					weatherData.sethumidity(humidity);
					weatherData.setlastUpdateTime(lastUpdateTime);
					weatherData.setpm25(pm25);
					weatherData.settemp(temp);
					weatherData.settempRange(tempRange);
					weatherData.setweather(weather);
					weatherData.setweatherType(weatherType);
					weatherData.setwind(wind);
					weatherData.setwindLevel(windLevel);
					return weatherData;
				}

			} while (cursor.moveToNext());
		}
		cursor.close();
		return weatherData;
	}
	
	
	public WeatherData queryTheWeatherMsgToday(){
		return queryWeatherMsgBydata(mdate);
	}
	
	/**
	 * 获取第一行天气信息
	 * @return
	 */
	public WeatherData queryTheWeatherFristLine(){
		WeatherData weatherData = null;
		Cursor cursor = weatherDB.rawQuery("select * from weatherinfo limit 0,1", null);
		if(cursor.moveToFirst()){
			do {
				String data = cursor.getString(cursor.getColumnIndex("data"));
				String WriteInTime = cursor.getString(cursor.getColumnIndex("WriteInTime"));
				int airData = cursor.getInt(cursor.getColumnIndex("airData"));
				String airQuality = cursor.getString(cursor.getColumnIndex("airQuality"));
				String city = cursor.getString(cursor.getColumnIndex("city"));
				String humidity = cursor.getString(cursor.getColumnIndex("humidity"));
				String lastUpdateTime = cursor.getString(cursor.getColumnIndex("lastUpdateTime"));
				String pm25 = cursor.getString(cursor.getColumnIndex("pm25"));
				int temp = cursor.getInt(cursor.getColumnIndex("temp"));
				String tempRange = cursor.getString(cursor.getColumnIndex("tempRange"));
				String weather = cursor.getString(cursor.getColumnIndex("weather"));
				int weatherType = cursor.getInt(cursor.getColumnIndex("weatherType"));
				String wind = cursor.getString(cursor.getColumnIndex("wind"));
				int windLevel = cursor.getInt(cursor.getColumnIndex("windLevel"));

				weatherData = new WeatherData();
				weatherData.setWriteInTime(WriteInTime);
				weatherData.setairData(airData);
				weatherData.setairQuality(airQuality);
				weatherData.setcity(city);
				weatherData.setdata(data);//**
				weatherData.sethumidity(humidity);
				weatherData.setlastUpdateTime(lastUpdateTime);
				weatherData.setpm25(pm25);
				weatherData.settemp(temp);
				weatherData.settempRange(tempRange);
				weatherData.setweather(weather);
				weatherData.setweatherType(weatherType);
				weatherData.setwind(wind);
				weatherData.setwindLevel(windLevel);
				return weatherData;
			} while (cursor.moveToNext());
		}
		cursor.close();
		return null;
	}
	
	public WeatherData[] queryWeatherMsgTheWholeWeek(Date curdate){
		WeatherData[] weatherarray = new WeatherData[7];
		for(int i = 0;i < 7;i++){
			mcalendar.setTime(curdate);
			mcalendar.add(Calendar.DAY_OF_MONTH, i);
			weatherarray[i] = queryWeatherMsgBydata(mcalendar.getTime());
		}
		return weatherarray;
	}
	
	public WeatherData[] queryWeatherMsgTheWholeWeek(String curdateformat){
		WeatherData[] weatherarray = new WeatherData[7];
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		Date date;
		try {
			date = sdf.parse(curdateformat);
			for(int i = 0;i < 7;i++){
				mcalendar.setTime(mdate);
				mcalendar.add(Calendar.DAY_OF_MONTH, i);
				weatherarray[i] = queryWeatherMsgBydata(mcalendar.getTime());
			}
			return weatherarray;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		} 
	}
	
	public WeatherData[] queryWeatherMsgTheWholeWeek(){
		WeatherData[] weatherarray = new WeatherData[7];
//	    long time=System.currentTimeMillis(); 
//	    SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd"); 
//	    Date d1=new Date(time); //date
//	    String t1=format.format(d1); //str
	    
		for(int i = 0;i < 7;i++){
			mcalendar.setTime(mdate);
			mcalendar.add(Calendar.DAY_OF_MONTH, i);
			weatherarray[i] = queryWeatherMsgBydata(mcalendar.getTime());
		}
		return weatherarray;
	}
}
