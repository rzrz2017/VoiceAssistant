package com.szhklt.www.voiceassistant.broadcastReceiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.szhklt.www.voiceassistant.util.LogUtil;
import com.szhklt.www.voiceassistant.util.NetworkUtil;

public class NetBroadCastReciver extends BroadcastReceiver {
    private static final String TAG = "NetBroadCastReciver";
    private final int NET_ETHERNET = 1;
    private final int NET_WIFI = 2;
    private final int NET_NOCONNECT = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtil.e(TAG, "onReceive()"+LogUtil.getLineInfo());
        switch (NetworkUtil.isNetworkAvailable(context)) {
            case NET_ETHERNET:
                LogUtil.e(TAG,"以太网连接成功！");
//                mCae.WifiConnect();
                LogUtil.e("reboot","resetReboot()"+LogUtil.getLineInfo());
//                resetReboot();//重设定时清理
//                WeatherTimeTask.getInstance().restart();//更新数据库天气
                break;
            case NET_WIFI:
                LogUtil.e(TAG,"WIFI连接成功！");
//                mCae.WifiConnect();
                LogUtil.e("reboot","resetReboot()"+LogUtil.getLineInfo());
//                resetReboot();//重设定时清理
//                WeatherTimeTask.getInstance().restart();//更新数据库天气
                break;
            case NET_NOCONNECT:
                LogUtil.e(TAG,"网络断开！");
//                mCae.WifiDisconnect();
                break;
            default:
                break;
        }

    }
}
