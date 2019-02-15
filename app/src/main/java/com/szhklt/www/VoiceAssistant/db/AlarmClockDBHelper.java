package com.szhklt.www.VoiceAssistant.db;

import com.szhklt.www.VoiceAssistant.MainApplication;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class AlarmClockDBHelper extends SQLiteOpenHelper {
	private static final int VERSION=2;//闹钟数据库版本号,升级时一次+1,不可低于上一版本
//	public static final String CREATE_ALARMCLOCK_1 = "create table alarmlist ("
////			+ "id integer primary key autoincrement, "
//			+ "_id integer, "
//			+ "datetime text, "
//			+ "content text, "
//			+ "alarmtype text, "
//			+ "repeat text)";
	public static final String CREATE_ALARMCLOCK_2 = "create table alarmlist ("
			+ "_id integer primary key autoincrement, "
			+ "datetime text, "
			+ "content text, "
			+ "alarmtype text, "
			+ "repeat text,"
			+ "state integer)";
	private static AlarmClockDBHelper alarmClockDataBaseHelper=null;
	private AlarmClockDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);

	}
public static  AlarmClockDBHelper getInstance(){
	if(alarmClockDataBaseHelper==null){
		alarmClockDataBaseHelper= new AlarmClockDBHelper(MainApplication.getContext(), "alarmclock.db", null, VERSION);//升级数据库时改变第四个参数的值
	}
	return alarmClockDataBaseHelper;
	
}
	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(CREATE_ALARMCLOCK_2);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		final String CREATE_TEMP_USER = "alter table alarmlist rename to _temp_alarmlist;";//修改原来的alarmlist表名改为_temp_alarmlist临时表名
		final String INSERT_DATA = "insert into alarmlist select *,'' from _temp_alarmlist";//把临时备份表中的数据copy到新创建的数据库表中
		final String DROP_USER = "drop table _temp_alarmlist";//删除临时备份表
		
		switch(newVersion){
        case VERSION:
           db.execSQL(CREATE_TEMP_USER); //第一步将旧表改为临时表

           db.execSQL(CREATE_ALARMCLOCK_2); //第二步创建新表(新添加的字段或去掉 的字段)

           db.execSQL(INSERT_DATA); //第三步将旧表中的原始数据保存到新表中以防遗失

           db.execSQL(DROP_USER); //第四步删除临时备份表
           break;
    }
		
	}
}
