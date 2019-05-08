package com.szhklt.VoiceAssistant.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.szhklt.VoiceAssistant.component.MyCAE;
import com.szhklt.VoiceAssistant.timeTask.LongRunningService;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.NetworkUtil;

public class NetBroadCastReceiver extends BroadcastReceiver {
    private static final String TAG = "NetBroadCastReceiver";
    private final int NET_ETHERNET = 1;
    private final int NET_WIFI = 2;
    private final int NET_NOCONNECT = 0;
    private MyCAE mCae;

    public NetBroadCastReceiver(MyCAE mCae){
        this.mCae = mCae;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e(TAG, "onReceive()"+LogUtil.getLineInfo());
        switch (NetworkUtil.isNetworkAvailable(context)) {
            case NET_ETHERNET:
                LogUtil.e(TAG,"以太网连接成功！");
                if(mCae != null){
                    mCae.WifiConnect();
                }
                LogUtil.e("reboot","resetReboot()"+LogUtil.getLineInfo());
                resetReboot(context);//重设定时清理
//                WeatherTimeTask.getInstance().restart();//更新数据库天气
                context.startService(LongRunningService.newIntent(context, LongRunningService.START_COMMAND_GET_WEATHER));
                break;
            case NET_WIFI:
                LogUtil.e(TAG,"WIFI连接成功！");
                if(mCae != null){
                    mCae.WifiConnect();
                }
                LogUtil.e("reboot","resetReboot()"+LogUtil.getLineInfo());
                resetReboot(context);//重设定时清理
//                WeatherTimeTask.getInstance().restart();//更新数据库天气
                context.startService(LongRunningService.newIntent(context,LongRunningService.START_COMMAND_GET_WEATHER));
                break;
            case NET_NOCONNECT:
                LogUtil.e(TAG,"网络断开！");
                if(mCae != null){
                    mCae.WifiDisconnect();
                }
                break;
            default:
                break;
        }

    }
    /**
     * 重新设置定时关机清理
     */
    private void resetReboot(Context context){
        //程序重新启动，重新设置定时清理时间
        Intent resetAlarmIntent=new Intent("com.szhklt.FloatWindow.FloatSmallView");
        Bundle bundle = new Bundle();
        bundle.putString("count", "REBOOT");
        resetAlarmIntent.putExtras(bundle);
        context.sendBroadcast(resetAlarmIntent);
    }
}
