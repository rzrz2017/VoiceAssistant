package com.szhklt.VoiceAssistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class WeatherDBHelper extends SQLiteOpenHelper{
	private static final int VERSION =1;
	public static final String CREATE_WEATHERINFO = "create table weatherinfo ("
			+ "id integer primary key autoincrement, "
			+ "WriteInTime text, "
			+ "airData integer, "
			+ "airQuality text, "
			+ "city text, "
			+ "data text, "
			+ "humidity text, "
			+ "lastUpdateTime text, "//2017-12-07 11:00
			+ "pm25 text, "
			+ "temp integer, "
			+ "tempRange text, "
			+ "weather text, "
			+ "weatherType integer, "
			+ "wind text, "
			+ "windLevel integer, "
			+ "answer text)";

//	weatherinfo.db
	public WeatherDBHelper(Context context, String name,
			CursorFactory factory) {
		super(context, name, factory, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_WEATHERINFO);
//		Toast.makeText(mContext,"create succeeded",Toast.LENGTH_SHORT).show();
		LogUtil.e("weatheradb","create weather succeeded");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		
	} 

}
