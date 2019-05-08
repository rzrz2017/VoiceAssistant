package com.szhklt.VoiceAssistant.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

public class SearchRecordDBHelper extends SQLiteOpenHelper{
    public static final String TAG = "searchRecordDBHelpe";
    //建表语句
    public static final String CREATE_RECORDS = "create table records ("
            + "id integer primary key autoincrement, "
            + "record text unique)";
    public Context mContext;



    public SearchRecordDBHelper(Context context,
                                String name,
                                SQLiteDatabase.CursorFactory factory,
                                int version) {
        super(context,name,factory,version);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_RECORDS);
        Toast.makeText(mContext,"create succeeded",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
