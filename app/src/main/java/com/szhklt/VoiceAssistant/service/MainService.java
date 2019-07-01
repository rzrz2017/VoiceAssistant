package com.szhklt.VoiceAssistant.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.widget.Toast;

import com.szhklt.VoiceAssistant.AlarmClockOperation;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.broadcastReceiver.NetBroadCastReceiver;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.component.MyCAE;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.timeTask.SleepTimeout;
import com.szhklt.VoiceAssistant.timeTask.SpeekTimeout;
import com.szhklt.VoiceAssistant.util.FileManager;
import com.szhklt.VoiceAssistant.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.base.bean.Music;

public class MainService extends Service{
    private static final String TAG = "MainService";
    private static final String ACTION_TOUCHEVENT = "com.szhklt.ACTION_TOUCHEVENT";
    public static int volume_value;// 当前说话音量

    private NetBroadCastReceiver netBroadCastReceiver;
    private String mscPath = Environment.getExternalStorageDirectory().getPath()+"msc";
    private MyCAE mCae;
    private MyAIUI mAIUI;
    private MyReceiver receiver;
    private IntentFilter filter;
    private KwSdk mKwSdk;
    private AudioManager mAudioManager;
    private AlarmClockOperation alarmClockOperation = AlarmClockOperation.getInstance();
    private FloatWindowManager mFWM = FloatWindowManager.getInstance();


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    //取消绑定
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    //创建
    @Override
    public void onCreate() {
        super.onCreate();
        //test

        //网络监听器
        LogUtil.e(TAG,"主服务启动");
        if (FileManager.fileIsExists(mscPath)) {
            FileManager.deleteDir(mscPath);
        }
        //启动流量统计服务
        //启动UDP服务
        //启动MQTT服务
        startService(new Intent(this,MqttService.class));
//      startService(new Intent(this,UDPService2.class));
        receiver = new MyReceiver();//注册广播接收者
        filter = new IntentFilter();
        filter.addAction("com.szhklt.FloatWindow.FloatSmallView");
        filter.addAction(ACTION_TOUCHEVENT);
//        filter.addAction(ACTION_TTS);
//        filter.addAction(MyAIUI.RESET_AIUI);
        filter.addAction("com.szhklt.getmusicinfo");//获取当前音乐信息
        registerReceiver(receiver, filter);
        saveRebootTime();//保存重启时间数据，用于定时清理

        if(mCae == null){
            mCae = new MyCAE();
        }
        netRegisterReceiver(mCae);
        if(mAIUI == null){
            mAIUI = MyAIUI.getInstance();
        }
        mCae.setmAIUI(mAIUI);

        //酷我
        mKwSdk = KwSdk.getInstance();
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //判断能否唤醒,如果为false,则关闭录音
        if(!getSwicthState()){
            Toast.makeText(getBaseContext(),"语音助手: 已停止唤醒",Toast.LENGTH_SHORT).show();
            mCae.stopRecording();
        }

        mKwSdk.registerPlayerStatusListener(new OnPlayerStatusListener() {
            @Override
            public void onPlayerStatus(PlayerStatus arg0, Music music) {
                if(arg0 == PlayerStatus.PLAYING || arg0 == PlayerStatus.INIT){//播放歌曲
                    LogUtil.e("kwsdk","kw: PLAYING & INIT");
                    mKwSdk.curKwMusicStatus = 1;
                    Intent intent = new Intent();
                    intent.putExtra("count", "[MediaPlayActivity]" + "pause");
                    intent.setAction("com.szhklt.service.MainService");
                    sendBroadcast(intent);

                    //发送到典声手机
//                    sendMsgToDsPhoneStatusFromKW(1);
//                    sendMsgToDsPhoneFromKW(music);
                }else if(arg0 == PlayerStatus.PAUSE){
                    LogUtil.e("kwsdk","kw: PAUSE");
                    mKwSdk.curKwMusicStatus = 0;
                    JSONObject o = new JSONObject();
                    try {
                        o.put("state", 0);
                        o.put("position", mKwSdk.getCurrentPos());
                        o.put("duration", mKwSdk.getCurrentMusicDuration());
                        o.put("volume",mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
                        LogUtil.e("dsplay","send json:"+o.toString()+LogUtil.getLineInfo());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendBroadcast(new Intent("com.hklt.play_song").putExtra("type",2).putExtra("data",o.toString()));
                    o = null;
                }
            }
        });
        SleepTimeout.getInstance().restart();
    }

    //注册网络监听
    private void netRegisterReceiver(MyCAE mCae){
        netBroadCastReceiver = new NetBroadCastReceiver(mCae);
        IntentFilter netFilter = new IntentFilter();
        netFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(netBroadCastReceiver, netFilter);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(mCae == null){
            mCae = new MyCAE();
        }
        if(mAIUI == null){
            mAIUI = MyAIUI.getInstance();
            mCae.setmAIUI(mAIUI);
        }

        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public void onDestroy() {
        mCae.destoryCAEandRecorder();
        unregisterReceiver(netBroadCastReceiver);
        stopService(new Intent(this,MqttService.class));
        super.onDestroy();
    }


    private boolean getSwicthState(){
        SharedPreferences pref = getSharedPreferences("keystatus", MODE_PRIVATE);
        String state = pref.getString("swstate","on");//默认打开
        LogUtil.e("MainService", state+LogUtil.getLineInfo());
        if("on".equals(state)){
            return true;
        }else if("off".equals(state)){
            return false;
        }
        return false;
    }

    /**
     * 保存重启时间
     */
    private void saveRebootTime(){
        SharedPreferences.Editor editor = this.getSharedPreferences("rebootdate",Context.MODE_PRIVATE).edit();
        SharedPreferences pref = getSharedPreferences("rebootdate", MODE_PRIVATE);
        String string = pref.getString("reboottime", "04时00分");//设置默认值
        LogUtil.e("MainService", string+LogUtil.getLineInfo());
        editor.putString("reboottime",string);
        editor.commit();
    }


    /**
     * 定时清理
     */
    public class MyReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(ACTION_TOUCHEVENT)){
                LogUtil.e("MainService","接收到触摸屏幕的广播!"+LogUtil.getLineInfo());
                SleepTimeout.getInstance().restart();
                return;
            }

            Bundle bundle = intent.getExtras();
            String count = bundle.getString("count");
            if (count.indexOf("sendAudioDataToAIUI") != -1) {
            }else if (count.indexOf("cancle") != -1) {
            }else if (count.indexOf("islauncher") != -1) {
                LogUtil.e("mFWM"," removeAll()"+LogUtil.getLineInfo());
                send("islauncher");
            }else if (count.indexOf("exitKW") != -1) {
            }else if (count.indexOf("ISNOTLAUNCHER") != -1) {
            }else if (count.indexOf("sleepactivity") != -1) {
            }else if (count.indexOf("KWmusic") != -1) {
            }else if (count.indexOf("kwpause") != -1) {
            }else if (count.indexOf("SmallView") != -1) {
            }else if (count.indexOf("Smallgone") != -1) {
                mFWM.removesmallWindow(context);
            }else if (count.equals("resetAIUI")) {
            }else if (count.equals("REBOOT")) {//定时清理
                LogUtil.e("MainService","接收到定时清理的广播!"+LogUtil.getLineInfo());
                alarmClockOperation.setReboottime();
            }else if (count.equals("cancelREBOOT")){
                LogUtil.e("MainService","接收到取消定时清理的广播!"+LogUtil.getLineInfo());
                alarmClockOperation.cancelRebootAlarmClock();
            }else if(count.indexOf("STOP_PCMRECORD") != -1){//停止唤醒
                LogUtil.e("MainService","接收到停止唤醒的广播!"+LogUtil.getLineInfo());
                mCae.stopRecording();
                mFWM.removeAll();
                mAIUI.Into_STATE_READY();
                SpeekTimeout.getInstance().stop();//停止说话超时
            }else if(count.indexOf("WRITE_AUDIO") != -1){//开始唤醒
                writeAudio();
                LogUtil.e("MainService","接收到开始唤醒的广播!"+LogUtil.getLineInfo());
            }else if(count.indexOf("RESET_ALARMCLOCK") != -1){//用于在重启之后重新设置闹钟
            }else if (count.indexOf("[tts]") != -1) {
            }
        }

        /**
         * 允许唤醒
         */
        private void writeAudio() {
            // TODO Auto-generated method stub
            send("returngraye");
            if (null == mCae) {
                return;
            }
            if (0 == mCae.startRecording()) {//使用外部录音，并开始录音
            }else {
                LogUtil.i(TAG, "start recording fail...");
            }
        }

        /**
         * 发送广播
         */
        private void send(String text) {
            Intent intent = new Intent();
            intent.putExtra("count", text);
            intent.setAction("com.szhklt.service.MainService");
            sendBroadcast(intent);
        }
    }
}
