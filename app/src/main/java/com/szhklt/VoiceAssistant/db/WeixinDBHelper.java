package com.szhklt.VoiceAssistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.szhklt.VoiceAssistant.util.LogUtil;

/**
 * 负责创建数据库
 */
public class WeixinDBHelper  extends SQLiteOpenHelper {
    private static final int VERSION =1;
    private static final String DATABASE_NAME = "wxdevice.db";
    public static final String CREATE_WEIXIN= "create table wxdevice ("
            + "id integer primary key autoincrement, "
            + "suTopic varchar(30),"
            + "name text,"
            + "phoneid text)";

    public WeixinDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name,factory , VERSION);
    }

    public WeixinDBHelper(Context context){
        super(context,DATABASE_NAME,null,VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_WEIXIN);
        LogUtil.e("WeixinDBHelper","create wx_device succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }
}
