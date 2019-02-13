package com.szhklt.www.voiceassistant.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.os.IBinder;
import android.os.PowerManager;

import com.szhklt.www.voiceassistant.KwSdk;
import com.szhklt.www.voiceassistant.broadcastReceiver.NetBroadCastReciver;
import com.szhklt.www.voiceassistant.component.MyAIUI;
import com.szhklt.www.voiceassistant.component.MyCAE;
import com.szhklt.www.voiceassistant.util.FileManager;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class MainService extends Service {
    private static final String TAG = "MainService";

    private NetBroadCastReciver netBroadCastReciver;
    private String mscPath = Environment.getExternalStorageDirectory().getPath()+"msc";
    private MyCAE mCae;
    private MyAIUI mAIUI;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogUtil.e(TAG,"主服务启动");
        //网络监听器
        netRegisterReceiver();

        if (FileManager.fileIsExists(mscPath)) {
            FileManager.deleteDir(mscPath);
        }

        //启动流量统计服务
        //启动UDP服务
//      startService(new Intent(this,UDPService2.class));

        if(mCae == null){
            mCae = new MyCAE();
        }
        if(mAIUI == null){
            mAIUI = new MyAIUI();
        }
        mCae.setmAIUI(mAIUI);

    }

    //注册网络监听
    private void netRegisterReceiver(){
        netBroadCastReciver = new NetBroadCastReciver();
        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netBroadCastReciver, netFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mCae == null){
            mCae = new MyCAE();
        }
        if(mAIUI == null){
            mAIUI = new MyAIUI();
            mCae.setmAIUI(mAIUI);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        mCae.destoryCAEandRecorder();
        unregisterReceiver(netBroadCastReciver);
        super.onDestroy();
    }
}
