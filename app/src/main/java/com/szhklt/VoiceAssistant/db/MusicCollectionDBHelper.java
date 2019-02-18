package com.szhklt.VoiceAssistant.db;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.util.LogUtil;

/**
 * 音乐收藏
 * @author wuhao
 *
 */
public class MusicCollectionDBHelper extends SQLiteOpenHelper{
	private static final int VERSION =1;
	private static MusicCollectionDBHelper musicCollectionDBHelper=null;
	private static final String SQL="create table music_collection ("
			+ "id integer not null primary key autoincrement, "
			+ "name varchar(500), "
			+ "singer varchar(500), "
			+ "album varchar(500),"
			+ "createtime datetime,"
			+ "modifytime datetime,"
			+ "count integer)";
	private MusicCollectionDBHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
	}


	public static  MusicCollectionDBHelper getInstance(){
		if(musicCollectionDBHelper==null){
			musicCollectionDBHelper= new MusicCollectionDBHelper(MainApplication.getContext(), "music.db", null, VERSION);//升级数据库时改变第四个参数的值
		}
		return musicCollectionDBHelper;

	}
	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

	}
	/**
	 * 根据id删除数据
	 * @param id
	 */
	public void deleteById(int id){
		try {
			getReadableDatabase().delete("music_collection", "id="+id, null);
		} catch (Exception e) {
			LogUtil.e("MusicCollectionDBHelper", "根据id删除数据错误"+ LogUtil.getLineInfo());
		}
	}
	/**
	 * 根据歌手删除数据
	 * @param singer
	 */
	public void deleteBySinger(String singer){
		try {
			getReadableDatabase().delete("music_collection", "singer="+singer, null);
		} catch (Exception e) {
			LogUtil.e("MusicCollectionDBHelper", "根据singer删除数据错误"+ LogUtil.getLineInfo());
		}
	}
	/**
	 * 删除所有歌曲
	 */
	public void deleteAll(){
		getReadableDatabase().execSQL("delete from music_collection");
	}
	/**
	 * 插入数据,如果已经有数据，则count加1
	 * @param singer
	 * @param name
	 * @param album
	 */
	public void insert(String singer,String name,String album){
		Cursor cursor=getReadableDatabase().query("music_collection",  new  String[]{"id,count"}, "singer="+"\""+singer+"\""+" and name="+"\""+name+"\"", null, null, null, null);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		if(cursor.moveToFirst()){
			do{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				int count=cursor.getInt(cursor.getColumnIndex("count"));
				ContentValues values=new ContentValues();
				values.put("count", ++count);
				values.put("modifytime", timestamp.toLocaleString());
				getReadableDatabase().update("music_collection", values, "id="+"\""+id+"\"", null);
				values.clear();
			}while(cursor.moveToNext());
		}else{
			ContentValues values=new ContentValues();
			values.put("singer", singer);
			values.put("name", name);
			values.put("album", album);
			values.put("count", 1);
			values.put("createtime", timestamp.toLocaleString());
			values.put("modifytime", timestamp.toLocaleString());
			getReadableDatabase().insert("music_collection", null, values);
			values.clear();
		}
	}
	/**
	 * 查询所有数据
	 * @return
	 */
	public Map<Integer,Map<String,Object>> queryAll(){
		Cursor cursor=getReadableDatabase().query("music_collection",  null, null, null, null, null, null);
		Map<Integer, Map<String,Object>> map=new HashMap<>();
		if(cursor.moveToFirst()){
			do{
				Map<String,Object> map1=new HashMap<>();
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				String singer = cursor.getString(cursor.getColumnIndex("singer"));
				String name = cursor.getString(cursor.getColumnIndex("name"));
				String album = cursor.getString(cursor.getColumnIndex("album"));
				String createtime = cursor.getString(cursor.getColumnIndex("createtime"));
				String modifytime = cursor.getString(cursor.getColumnIndex("modifytime"));
				int count=cursor.getInt(cursor.getColumnIndex("count"));
				map1.put("singer", singer);
				map1.put("name", name);
				map1.put("album", album);
				map1.put("createtime", createtime);
				map1.put("modifytime", modifytime);
				map1.put("count", count);
				map.put(id, map1);
			}while(cursor.moveToNext());
		}
		return map;
	}
	/**
	 * 根据歌曲信息查询id
	 * @param singer
	 * @param name
	 * @return
	 */
	public int queryIdBySong(String singer,String name,String album){
		Cursor cursor=getReadableDatabase().query("music_collection",  new  String[]{"id"}, "singer="+"\""+singer+"\""+" and name="+"\""+name+"\""+" and album="+"\""+album+"\"", null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				int id = cursor.getInt(cursor.getColumnIndex("id"));
				return id;
			}while(cursor.moveToNext());
		}
		return -1;//收藏列表没有此歌曲
	}
	public Map<Integer,List<Object>> queryByCreatetime(Date createtime){
		return null;
	}
}
