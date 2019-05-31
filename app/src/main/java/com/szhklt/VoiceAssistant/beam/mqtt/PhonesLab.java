package com.szhklt.VoiceAssistant.beam.mqtt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.szhklt.VoiceAssistant.db.WeixinDBHelper;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class PhonesLab {
    private static String TAG = "PhonesLab";
    private static PhonesLab sphoneLab;
    private Context mContext;
    private SQLiteDatabase mDatabase;


    public static PhonesLab get(Context context){
        if(sphoneLab == null){
            sphoneLab = new PhonesLab(context);
        }
        return sphoneLab;
    }

    private PhonesLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new WeixinDBHelper(mContext).getWritableDatabase();
    }

    /**
     * 返回一个数据库的快照
     * @return
     */
    public List<Phone> getPhones(){
        List<Phone> phones = new ArrayList<>();
        PhoneCursorWrapper cursor = queryPhone(null,null);

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                phones.add(cursor.getPhone());
                cursor.moveToNext();
            }
        }finally {
            cursor.close();
        }

        return phones;
    }

    public Phone getPhone(String topic){
        PhoneCursorWrapper cursor = queryPhone("suTopic = ?",new String[]{topic});
        try{
            LogUtil.e(TAG,"cursor.getCount():"+cursor.getCount());
            if(cursor.getCount() == 0){

                return null;
            }
            cursor.moveToFirst();
            return cursor.getPhone();
        }finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Phone phone){
        ContentValues values = new ContentValues();
        values.put("suTopic",phone.getTopic());
        values.put("name",phone.getName());
        values.put("phoneid",phone.getId());
        values.put("status",phone.getStatus());

        return values;
    }


    public void addPhone(Phone p){
        ContentValues values = getContentValues(p);
        mDatabase.insert("wxdevice",null,values);
    }

    public void deletePhone(Phone p){
        String topic = p.getTopic();
        mDatabase.delete("wxdevice","suTopic = ?",new String[]{topic});
    }

    public void deletePhone(String topic){
        mDatabase.delete("wxdevice","suTopic = ?",new String[]{topic});
    }

    public void updatePhone(Phone phone){
        ContentValues values = getContentValues(phone);
        mDatabase.update("wxdevice",values,"suTopic = ?",new String[]{phone.getTopic()});
    }

    private PhoneCursorWrapper queryPhone(String whereClause, String[] whereArgs){
        Cursor cursor = mDatabase.query(
                "wxdevice",
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new PhoneCursorWrapper(cursor);
    }

    /**
     * 获取当前连接这的手机信息
     * @return
     */
    public Phone getCurPhone(){
        PhoneCursorWrapper cursor = queryPhone("status = ?",new String[]{String.valueOf(1)});
//        PhoneCursorWrapper cursor = new PhoneCursorWrapper(mDatabase.rawQuery("select * from wxdevice where status = ?",new String[]{String.valueOf(1)}));
        try{
            LogUtil.e(TAG,"cursor.getCount():"+cursor.getCount());
            if(cursor.getCount() == 0){

                return null;
            }
            cursor.moveToFirst();
            LogUtil.e(TAG,"当前在线的设备:"+cursor.getPhone());
            return cursor.getPhone();
        }finally {
            cursor.close();
        }
    }


    /**
     * 保存当前的连接着的数据
     *
     */
//    public static void saveCurTop(String topic){
//        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext()).edit();
//        editor.putString("connect_topic",topic);
//        editor.commit();
//    }

    /**
     * 获取topic
     */
//    public static String getCurTop(){
//        SharedPreferences share = PreferenceManager.getDefaultSharedPreferences(MainApplication.getContext());
//        return share.getString("connect_topic",null);
//    }

}
