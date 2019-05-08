package com.szhklt.VoiceAssistant.util;

/**
 * http的回调函数
 */

public interface HttpCallBack {
    void onFailed(int error, String errorMsg);

    void onSuccess(String res);
}
