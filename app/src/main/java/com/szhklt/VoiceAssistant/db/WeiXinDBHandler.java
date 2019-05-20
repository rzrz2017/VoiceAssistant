package com.szhklt.VoiceAssistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.szhklt.VoiceAssistant.beam.mqtt.Phone;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class WeiXinDBHandler {
    private String TAG ="WeiXinDBHandler";
    public SQLiteDatabase weixinDB;
    private Context mcontext;
    private WeixinDBHelper weixinDBHelper;

    private WeiXinDBHandler(Context context) {
        this.mcontext = context;
        weixinDBHelper = new WeixinDBHelper(mcontext,"wxdevice.db",null);
        if(weixinDB == null){
            weixinDB = weixinDBHelper.getWritableDatabase();
        }
    }

    //插入一条数据
    public void insertALineOfTopic(Phone phoneMsg) {
        ContentValues values = new ContentValues();
        values.put("suTopic", phoneMsg.getTopic());
        values.put("name",phoneMsg.getName());
        values.put("phoneid",phoneMsg.getId());
        Long  row = weixinDB.insert("wxdevice", null, values);

    }

    //删除一条topic
    public void deleteTopoicMsg(String topic){
        Log.e(TAG,"执行删除");
        weixinDB.execSQL("DELETE FROM wxdevice where suTopic ='"+topic+"'");
    }

    //查询所有topic
    public List<Phone> queryTopicMsg(){
        List<Phone> phoneList = new ArrayList<>();
        Cursor cursor =  weixinDB.rawQuery("SELECT * FROM wxdevice", null);
        while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndex("id"));
            String suTopic= cursor.getString(cursor.getColumnIndex("suTopic"));
            String name = cursor.getString(cursor.getColumnIndex("name"));
            String phoneid = cursor.getString(cursor.getColumnIndex("phoneid"));
            Phone phone = new Phone(name,phoneid,suTopic);
            phoneList.add(phone);
        }
       return phoneList;
    }
}
