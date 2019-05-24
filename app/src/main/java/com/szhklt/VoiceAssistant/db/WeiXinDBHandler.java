package com.szhklt.VoiceAssistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.szhklt.VoiceAssistant.beam.mqtt.Phone;

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

}
