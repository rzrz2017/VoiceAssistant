package com.szhklt.VoiceAssistant.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SearchRecordDBHandler {
    private static final String TAG = "SearchRecordDBHandler";
    public SQLiteDatabase recordDB;
    ContentValues values;

    public SearchRecordDBHandler(SQLiteDatabase db){
        recordDB = db;
        values = new ContentValues();
    }

    //插入
    public void insert(String record){
        //开始组装第一条数据
        values.put("record",record);
        if (record != null){
            recordDB.insert("records",null,values);
        }
        values.clear();
    }

    //删除数据
    public void delect(String record){
        recordDB.delete("records","record = ?",new String[]{record});
    }

    //查询数据
    public ArrayList<String> query(){
        LogUtil.e(TAG,"query()");
        ArrayList<String> list = new ArrayList<>();
        Cursor cursor = recordDB.query("records",
                null,
                null,
                null,
                null,
                null,
                null);
        if(cursor.moveToFirst()){
            do {
                //便利Cursor对象,取出数据并打印
                String record = cursor.getString(cursor.getColumnIndex("record"));
                LogUtil.e(TAG,"record:"+record);
                list.add(record);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return list;
    }
}
