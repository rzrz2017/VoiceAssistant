package com.szhklt.www.VoiceAssistant.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.szhklt.www.VoiceAssistant.util.LogUtil;

/**
 * 闹钟数据处理类  此类是一个单例
 *
 */
public class AlarmDBHandler {
	private ContentValues values = new ContentValues();
	private AlarmDBHandler(){
	}
	private static AlarmDBHandler instance;
	public static synchronized AlarmDBHandler getInstance(){
		if(instance == null){
			synchronized (AlarmDBHandler.class) {
				if(instance == null){
					instance = new AlarmDBHandler();
				}
			}
		}
		return instance;
	}
	
	//添加数据
	public synchronized void add(SQLiteDatabase db,int id,String datetime,String alarmtype,String content,String repeat, int state){
		if(datetime == null && alarmtype == null){
			LogUtil.e("alarm","闹钟数据为空!"+LogUtil.getLineInfo());
			return;
		}
		//开始组装数据
		values.put("_id",id);
		values.put("datetime",datetime);
		values.put("alarmtype",alarmtype);
		values.put("state", state);
		if(content == null){
			values.put("content","无");
		}else{
			values.put("content",content);
		}
		if(repeat == null){
			values.put("repeat","单次");
		}else{
			values.put("repeat",repeat);
		}
		db.insert("alarmlist",null, values);
		values.clear();
	}
	
	//删除数据
	public synchronized void delete(SQLiteDatabase db,String datetime,String alarmtype,String content,String repeat){
		String deletesql;
		if(datetime == null ){
			LogUtil.e("alarm","闹钟数据删除失败!因为"+"datetime:"+datetime+"内容为空!"+LogUtil.getLineInfo());
			return;
		}
		if(alarmtype == null){
			LogUtil.e("alarm","闹钟数据删除失败!因为"+"alarmtype:"+alarmtype+"内容为空!"+LogUtil.getLineInfo());
			return;
		}
		if(null == content){
			LogUtil.e("alarm","因为"+"content:"+content+"内容为空!"+LogUtil.getLineInfo());
			content = "无";
		}
		if(null == repeat){
			LogUtil.e("alarm","因为"+"repeat:"+repeat+"内容为空!"+LogUtil.getLineInfo());
			repeat = "单次";
		}
		deletesql = "delete from alarmlist where datetime = "+"\""+datetime+"\""+" and alarmtype = "+"\""+alarmtype+"\""+" and content = "+"\""+content+"\""+" and repeat = "+"\""+repeat+"\""+";";
		//删除数据
		LogUtil.e("alarm","deletesql:"+deletesql+LogUtil.getLineInfo());
		db.execSQL(deletesql);
	}
	
	//查看数据
	public synchronized void query(SQLiteDatabase db){
		Cursor cursor = db.query("alarmlist",null,null,null,null,null, null);
		if(cursor.moveToFirst()){
			do {
				//便利Cursor对象,取出数据并答应
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				String datetime = cursor.getString(cursor.getColumnIndex("datetime")); 
				String alarmtype = cursor.getString(cursor.getColumnIndex("alarmtype"));
				String content = cursor.getString(cursor.getColumnIndex("content"));
				
				LogUtil.e("alarm","alarm id is"+id+LogUtil.getLineInfo());
				LogUtil.e("alarm","alarm datetime is"+datetime+LogUtil.getLineInfo());
				LogUtil.e("alarm","alarm alarmtype is"+alarmtype+LogUtil.getLineInfo());
				LogUtil.e("alarm","alarm content is "+content+LogUtil.getLineInfo());
			} while (cursor.moveToNext());
			cursor.close();
		}
	}
}
