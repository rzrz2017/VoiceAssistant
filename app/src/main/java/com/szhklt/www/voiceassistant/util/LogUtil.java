package com.szhklt.www.voiceassistant.util;

import android.util.Log;

/**
 * 日志工具类
 * 对日志进行自定义处理
 * 1. 支持异常打印
 * @author wuhao
 *
 */
public class LogUtil {
    public static final int VERBOSE = 1;//表示都打印
    private static final int DEBUG = 2;
    private static final int INFO = 3;
    private static final int WARN = 4;
    private static final int ERROR = 5;
    private static final int NOTHING = 6;//表示什么也不打印
    //开发时将VERBOSE赋值给LEVEL，上线时将NOTHING赋值给LEVEL。其余日志打印级别看实际需求将上述常量对LEVEL进行赋值
    public static final int LEVEL = VERBOSE;
    public static void v(String tag, String msg) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg);
        }
    }
    public static void v(String tag, String msg,Throwable tr) {
        if (LEVEL <= VERBOSE) {
            Log.v(tag, msg,tr);
        }
    }
    public static void d(String tag, String msg) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg);
        }
    }
    public static void d(String tag, String msg,Throwable tr) {
        if (LEVEL <= DEBUG) {
            Log.d(tag, msg,tr);
        }
    }
    public static void i(String tag, String msg) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg);
        }
    }
    public static void i(String tag, String msg,Throwable tr) {
        if (LEVEL <= INFO) {
            Log.i(tag, msg,tr);
        }
    }

    public static void w(String tag, String msg) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg);
        }
    }
    public static void w(String tag, String msg,Throwable tr) {
        if (LEVEL <= WARN) {
            Log.w(tag, msg,tr);
        }
    }
    public static void e(String tag, String msg) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg);
        }
    }
    public static void e(String tag, String msg,Throwable tr) {
        if (LEVEL <= ERROR) {
            Log.e(tag, msg,tr);
        }
    }

    /**
     * demo:LogUtil.getLineInfo();
     * @return
     */
    public static String getLineInfo(){
        StackTraceElement ste = new Throwable().getStackTrace()[1];
        return "--"+ste.getFileName() + ":Line"+ste.getLineNumber();

    }

    public static void printCurrentLineInfo(){
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        for(StackTraceElement s : stackTrace){
            Log.e("line","类名：" + s.getClassName() + "  ,  java文件名：" + s.getFileName() + ",  当前方法名字：" + s.getMethodName() + ""
                    + " , 当前代码是第几行：" + s.getLineNumber() + ", " );
        }
    }

}
