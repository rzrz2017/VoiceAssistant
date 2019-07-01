package com.szhklt.VoiceAssistant.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.szhklt.VoiceAssistant.beam.intent;

/**
 * 用于执行技能
 */
public class Executor {
    private static String TAG = "Executor";
    private Gson gson;

    public Executor(){
        gson = new GsonBuilder().serializeNulls().create();
    }


    public void exe(String data,CallBack callBack){
        intent intent = gson.fromJson(data, intent.class);


    }


    interface CallBack{
        void success();
        void failure();
    }
}
