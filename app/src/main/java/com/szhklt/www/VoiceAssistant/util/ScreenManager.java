package com.szhklt.www.VoiceAssistant.util;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Context;
import android.os.PowerManager;

import com.szhklt.www.VoiceAssistant.MainApplication;

/**
 * @author rz
 * 用于控制锁屏和唤醒屏幕
 */
public class ScreenManager {
    private KeyguardManager mKeyguardManager ;
    private KeyguardManager.KeyguardLock mKeyguardLock;
    private PowerManager mPowerManager ;
    private PowerManager.WakeLock mWakeLock;

    public ScreenManager(){
        mKeyguardManager = (KeyguardManager) MainApplication.getContext().getSystemService(Context.KEYGUARD_SERVICE);
        mPowerManager = (PowerManager) MainApplication.getContext().getSystemService(Context.POWER_SERVICE);
    }

    public void screenOff(){

    }

    /**
     * 解锁屏幕
     */
    @SuppressLint("InvalidWakeLockTag")
    public void screenOn(){
        if (!isScreenOn()) {
            mWakeLock = mPowerManager.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP| PowerManager.SCREEN_BRIGHT_WAKE_LOCK,"bright");
            mWakeLock.acquire(10000); // 点亮屏幕
            mWakeLock.release(); // 释放

            // 屏幕解锁
            mKeyguardLock = mKeyguardManager.newKeyguardLock("unLock");
            mKeyguardLock.reenableKeyguard();
            mKeyguardLock.disableKeyguard(); // 解锁
        }
    }

    public boolean isScreenOn() {
        return mPowerManager.isScreenOn();
    }

    public void destory(){
        if(mWakeLock != null){
            if(mWakeLock.isHeld()){
                mWakeLock.release();
            }
            mWakeLock = null;
        }
    }
}
