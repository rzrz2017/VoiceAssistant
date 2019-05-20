package com.szhklt.VoiceAssistant.util;

public class CommonUtils {
    /**
     * 获取序列号
     * @return
     */
    public static String getSerialNumber(){
        String serialNumber = android.os.Build.SERIAL;
        return serialNumber;
    }

}
