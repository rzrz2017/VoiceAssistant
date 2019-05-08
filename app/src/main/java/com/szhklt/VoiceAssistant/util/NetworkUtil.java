package com.szhklt.VoiceAssistant.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import java.io.IOException;

public class NetworkUtil {
    private static int NET_ETHERNET = 1;
    private static int NET_WIFI = 2;
    private static int NET_NOCONNECT = 0;
    /**
     * 检查Wifi的链接状态
     * @param context
     * @return
     */
    public static boolean checkWifiState(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        Boolean isWifiConn = networkInfo.isConnected();
        return isWifiConn;
    }

    /**
     * 检查移动网络的链接状态
     * @param context
     * @return
     */
    public static boolean checkMobileState(Context context){
        ConnectivityManager manager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        Boolean isMobileConn = networkInfo.isConnected();
        return isMobileConn;
    }

    /**
     * 判断WIFI网络是否可用
     * @param
     * @return
     * @author ysc
     */
    public static boolean pingIpAddress(String ipAddress) {
        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 3 " + ipAddress);//ping1次，每次3秒，超时失败
            int status = process.waitFor();
            if (status == 0) {
                return true;
            } else {
                return false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 判断是否有网络连接
     * @param context
     * @return
     */
    public static boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static int isNetworkAvailable(Context context){
        ConnectivityManager connectManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ethNetInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo wifiNetInfo = connectManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if(ethNetInfo != null && ethNetInfo.isConnected()){
            return NET_ETHERNET;
        }else if(wifiNetInfo != null && wifiNetInfo.isConnected()){
            return NET_WIFI;
        }else {
            return NET_NOCONNECT;
        }
    }


}
