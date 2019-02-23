package com.szhklt.VoiceAssistant.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.szhklt.VoiceAssistant.RzMusicPkg.RzMusicLab;
import com.szhklt.VoiceAssistant.activity.RZMediaPlayActivity2;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.Timer;
import java.util.TimerTask;

public class MusicReceiver extends BroadcastReceiver {
    private static String TAG = "MusicReceiver";
    private String dsmusic="";
    public String count;
    private int temp;//current song index

    public int musicTotal;//歌曲总计
    public int musicCounter;//实际歌曲计数器
    public int loadFailCounter = 0;//加载失败计数
    public Timer timer = new Timer();
    public static boolean tag = true;
    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO Auto-generated method stub
        Bundle bundle = intent.getExtras();
        count = bundle.getString("count");
        if(count.equals("DSintoMUSIC")){//进入多媒体播放界面
        }else if(count.startsWith("[DSKUGOU]")){
            LogUtil.e(TAG,"tag:"+tag);
            if(tag == false){
                return;
            }
            tag = false;
            timer.schedule(new TimerTask() {

                @Override
                public void run() {
                    // TODO Auto-generated method stub
                    tag = true;
                }
            }, 3500);

            RzMusicLab.get().getRzMusics().clear();
            dsmusic=count.replaceAll("\\[DSKUGOU]", "");
            String[] musicstr=dsmusic.split("@@");
            LogUtil.e(TAG,"musicstr[0]:"+musicstr[0]);
            temp=Integer.parseInt(musicstr[1].toString());
            if(musicstr[0].length()>10){
                Intent sintent = new Intent(context, RZMediaPlayActivity2.class);
                sintent.putExtra("kg_data",musicstr[0]);
                sintent.putExtra("kg_temp",temp);
                sintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(sintent);
            }
        }
    }
}
