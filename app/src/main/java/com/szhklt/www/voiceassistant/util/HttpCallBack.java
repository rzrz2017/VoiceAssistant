package com.szhklt.www.voiceassistant.util;

/**
 * http的回调函数
 */

public interface HttpCallBack {
    void onFailed(int error, String errorMsg);

    void onSuccess(String res);
}
