package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import android.os.Environment;

import okhttp3.Cache;
import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpUtil {
    private OkHttpClient mOkHttpClient;  
    private static HttpUtil mInstance;  
    private final static long CONNECT_TIMEOUT = 60;//超时时间，秒  
    private final static long READ_TIMEOUT = 60;//读取时间，秒  
    private final static long WRITE_TIMEOUT = 60;//写入时间，秒  
    
    private static final long cacheSize = 1024 * 1024 * 10;// 缓存文件最大限制大小10M
    private static String cacheDirectory = Environment.getExternalStorageDirectory() + "/okttpcaches"; // 设置缓存文件路径  
    private static Cache cache = new Cache(new File(cacheDirectory), cacheSize);
    
    /** 
     * @param url        下载链接 
     * @param startIndex 下载起始位置 
     * @param endIndex   结束为止 
     * @param callback   回调 
     * @throws IOException 
     */  
    public void downloadFileByRange(String url, long startIndex, long endIndex, Callback callback) throws IOException {  
        // 创建�?个Request  
        // 设置分段下载的头信息�? Range:做分段数据请�?,断点续传指示下载的区间�?�格�?: Range bytes=0-1024或�?�bytes:0-1024  
        Request request = new Request.Builder()
                .cacheControl(new CacheControl.Builder().maxAge(1024 * 1024 * 3, TimeUnit.SECONDS).build())
        		.header("RANGE", "bytes=" + startIndex + "-" + endIndex)  
                .url(url) 
                .build();  
        doAsync(request, callback);  
    }  
    
    public void getContentLength(String url, Callback callback) throws IOException {  
        // 创建�?个Request  
        Request request = new Request.Builder()  
                .cacheControl(new CacheControl.Builder().maxAge(1024 * 1024 * 3, TimeUnit.SECONDS).build())
                .url(url)  
                .build();  
        doAsync(request, callback);  
    }  
    
    /** 
     * 同步GET请求 
     */  
    public void doGetSync(String url) throws IOException {  
        //创建�?个Request  
        Request request = new Request.Builder()  
                .url(url)  
                .build();  
        doSync(request);  
    }  
    
    /** 
     * 异步请求 
     */  
    private void doAsync(okhttp3.Request request, Callback callback) throws IOException {  
        //创建请求会话  
        Call call = mOkHttpClient.newCall(request);  
        //同步执行会话请求  
        call.enqueue((okhttp3.Callback) callback);  
    }  
  
    /** 
     * 同步请求 
     */  
    private Response doSync(Request request) throws IOException {  
        //创建请求会话  
        Call call = mOkHttpClient.newCall(request);  
        //同步执行会话请求  
        return call.execute();  
    }  
    
    /** 
     * @return HttpUtil实例对象 
     */  
    public static HttpUtil getInstance() {  
        if (null == mInstance) {  
            synchronized (HttpUtil.class) {  
                if (null == mInstance) {  
                    mInstance = new HttpUtil();  
                }  
            }  
        }  
        return mInstance;  
    }  
  
    /** 
     * 构�?�方�?,配置OkHttpClient 
     */  
    private HttpUtil() {  
        //创建okHttpClient对象  
        OkHttpClient.Builder builder = new OkHttpClient.Builder()  
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)  
                .writeTimeout(READ_TIMEOUT, TimeUnit.SECONDS)  
                .readTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)
                .cache(cache);//设置缓存 
        mOkHttpClient = builder.build();  
    }  
}
