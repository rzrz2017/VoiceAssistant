package com.szhklt.VoiceAssistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.szhklt.VoiceAssistant.util.LogUtil;

public class WeixinDBHelper  extends SQLiteOpenHelper {
    private static final int VERSION =1;
    public static final String CREATE_WEIXIN= "create table wxdevice ("
            + "id integer primary key autoincrement, "
            + "suTopic varchar(30), "
            + "state integer)";

    public WeixinDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory) {
        super(context, name,factory , VERSION);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_WEIXIN);
        LogUtil.e("weixinDatabse","create wx_device succeeded");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
    }




}
