package com.szhklt.VoiceAssistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.szhklt.VoiceAssistant.beam.Topic;

import java.util.ArrayList;
import java.util.List;

public class WeiXinDBHandler {

    private String TAG ="WeiXinDBHandler";
    public SQLiteDatabase weixinDB;

    private Context mcontext;

    private WeixinDBHelper weixinDBHelper;

    public WeiXinDBHandler() {
    }

    public WeiXinDBHandler(Context context) {
        this.mcontext = context;
        weixinDBHelper = new WeixinDBHelper(mcontext,"wxdevice.db",null);
        if(weixinDB == null){
            weixinDB = weixinDBHelper.getWritableDatabase();
        }
    }

    //插入一条数据
    public void insertALineOfTopic(Topic topic) {
        ContentValues values = new ContentValues();
        values.put("suTopic", topic.getSuTopic());
        values.put("state", topic.getState());
        Long  row = weixinDB.insert("wxdevice", null, values);

    }
    //删除一条topic
    public void deleteTopoicMsg(String topic ){
        Log.e(TAG,"执行删除");
        weixinDB.execSQL("DELETE FROM wxdevice where suTopic ='"+topic+"'");
    }

    //查询所有topic
    public List<Topic> queryTopicMsg(){
        List<Topic> topicList = new ArrayList<Topic>();
       Cursor cursor =  weixinDB.rawQuery("SELECT * FROM wxdevice", null);
       while (cursor.moveToNext()){
            Integer id = cursor.getInt(cursor.getColumnIndex("id"));
            String suTopic= cursor.getString(cursor.getColumnIndex("suTopic"));
            Integer state = cursor.getInt(cursor.getColumnIndex("state"));
            Topic topic = new Topic();
            topic.setId(id);
            topic.setSuTopic(suTopic);
            topic.setState(state);
            topicList.add(topic);
       }
       return  topicList;
    }


    //查询指定topic的状态
    public Integer queryTopicMsgState(String topic){
         Cursor cursor= weixinDB.rawQuery("SELECT * FROM wxdevice where suTopic = '"+topic +"'",null);
        while (cursor.moveToNext()){
            Integer state = cursor.getInt(cursor.getColumnIndex("state"));
            return  state;
        }
        return  null;
    }

    //修改指定topic的状态
    public void updateTopicMsg(String topic,Integer state){

            ContentValues value = new ContentValues();
            value.put("state",state);
            weixinDB.update("wxdevice", value, "suTopic='"+topic+"'", new String[] {});
        }

    //修改指定topic之外的其他topic的状态
    public  void updateOtherTopicState(String topic, Integer state){
        ContentValues value = new ContentValues();
        value.put("state",state);
        weixinDB.update("wxdevice", value, "suTopic !='"+topic+"'", new String[] {});
    }

}
