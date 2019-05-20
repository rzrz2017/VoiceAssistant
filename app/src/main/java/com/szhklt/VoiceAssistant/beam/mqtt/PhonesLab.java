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
        String topic = phone.getTopic();
        ContentValues values = getContentValues(phone);
        mDatabase.update("wxdevice",values,"suTopic = ?",new String[]{topic});
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


}
