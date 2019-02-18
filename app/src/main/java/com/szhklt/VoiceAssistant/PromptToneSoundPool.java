package com.szhklt.VoiceAssistant;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

import com.szhklt.VoiceAssistant.util.LogUtil;

/**
 * @author rz
 * 使用soundpool播放提示音
 */
public class PromptToneSoundPool {
    public static final String TAG = "PromptToneSoundPool";
    private int PromptToneStreamId;
    Context context;
    float volumeRatio = 0.0f;
    SoundPool sp;
    Integer soundID;
    public PromptToneSoundPool() {
        // TODO Auto-generated constructor stub
        LogUtil.e(TAG,"PromptToneSoundPool()"+ LogUtil.getLineInfo());
        this.context = MainApplication.getContext();
//			am = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        setVolumeValue();
        sp = new SoundPool(4,AudioManager.STREAM_MUSIC,0);
        soundID = sp.load(context, R.raw.notify_audio2, 1);
        sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int sampleId, int status) {
                // TODO Auto-generated method stub
                if(status == 0){
                    LogUtil.e("sp", "音效加载成功!"+LogUtil.getLineInfo());
                }else{
                    LogUtil.e("sp", "音效加载失败!"+"status:"+status+LogUtil.getLineInfo());
                }
            }
        });
    }

    //设置sp音量
    public void setVolumeValue(){
        AudioManager am = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
        float audioMaxVolum = am.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        LogUtil.e("sp",String.valueOf(audioMaxVolum));
        float volumeCurrent = am.getStreamVolume(AudioManager.STREAM_MUSIC);
        LogUtil.e("sp",String.valueOf(volumeCurrent));
        volumeRatio = (float)volumeCurrent/audioMaxVolum;
        LogUtil.e("sp",String.valueOf(volumeRatio));
    }

    /**
     * 播放
     */
    public void playSound(final Boolean isCallback){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                PromptToneStreamId = sp.play(soundID, volumeRatio, volumeRatio, 1, 0, 1.0f);
                Log.e(TAG, "PromptToneStreamId:"+PromptToneStreamId);
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                if(isCallback){
                    listener.onCompletion();
                }
            }
        }).start();
    }

    /**
     * 回收sp资源
     */
    public void destorySoundPool(){
        sp.unload(PromptToneStreamId);
        sp.release();
    }

    private OnSoundPoolCompletionListener listener;
    public void setCompleteListener(OnSoundPoolCompletionListener listener){
        this.listener = listener;
    }

    /**
     * @author rz
     * soundpool播放完成接口
     */
    public interface OnSoundPoolCompletionListener{
        void onCompletion();
    }
}
