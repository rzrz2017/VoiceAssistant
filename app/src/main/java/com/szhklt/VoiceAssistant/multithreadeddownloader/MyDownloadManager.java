package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import android.os.Environment;
import android.text.TextUtils;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class MyDownloadManager {
	    public static String DEFAULT_FILE_DIR;//默认下载目录  
	    private Map<String, DownloadTask> mDownloadTasks;//文件下载任务索引，String为url,用来唯一区别并操作下载的文件
	    private static MyDownloadManager mInstance;  
	  
	    /** 
	     * 下载文件 
	     */  
	    public void download(String... urls) {  
	    	LogUtil.e("ranzhen","download()");
	        for (int i = 0, length = urls.length; i < length; i++) {  
	            String url = urls[i];  
	            LogUtil.e("ranzhen","url =="+String.valueOf(url));
	            if (mDownloadTasks.containsKey(url)) {  
	                mDownloadTasks.get(url).start();  
	            }  
	        }  
	    }  

	    public String getFileName(String url) {  
	        return url.substring(url.lastIndexOf("/") + 1);  
	    }  
	  
	    /** 
	     * 暂停 
	     */  
	    public void pause(String... urls) {  
	        for (int i = 0, length = urls.length; i < length; i++) {  
	            String url = urls[i];  
	            if (mDownloadTasks.containsKey(url)) {  
	                mDownloadTasks.get(url).pause();  
	            }  
	        }  
	    }  
	  
	    /** 
	     * 取消下载 
	     */  
	    public void cancel(String... urls) {  
	        for (int i = 0, length = urls.length; i < length; i++) {  
	            String url = urls[i];  
	            if (mDownloadTasks.containsKey(url)) {  
	                mDownloadTasks.get(url).cancel();  
	            }  
	        }  
	    }  
	  
	    /** 
	     * 添加下载任务 
	     */  
	    public void add(String url, DownloadListner l) {  
	        add(url, null, null, l);  
	    }  
	  
	    /** 
	     * 添加下载任务 
	     */  
	    public void add(String url, String filePath, DownloadListner l) {  
	        add(url, filePath, null, l);  
	    }  
	  
	    /** 
	     * 添加下载任务 
	     */  
	    public void add(String url, String filePath, String fileName, DownloadListner l) {  
	        if (TextUtils.isEmpty(filePath)) {//没有指定下载目录,使用默认目录  
	            filePath = getDefaultDirectory();  
	        }  
	        if (TextUtils.isEmpty(fileName)) {  
	            fileName = getFileName(url);  
	        }  
	        mDownloadTasks.put(url, new DownloadTask(new FilePoint(url, filePath, fileName), l));  
	    }  
	  
	    /** 
	     * 获取默认下载目录 
	     * 
	     * @return 
	     */  
	    private String getDefaultDirectory() {  
	        if (TextUtils.isEmpty(DEFAULT_FILE_DIR)) {  
	            DEFAULT_FILE_DIR = Environment.getExternalStorageDirectory().getAbsolutePath()  
	                    + File.separator + MainApplication.speechupdate + File.separator;  
	            LogUtil.e("ranzhen","DEFAULT_FILE_DIR == "+String.valueOf(DEFAULT_FILE_DIR));
	        }  
	        return DEFAULT_FILE_DIR;  
	    }  
	  
	    /** 
	     * 是否正在下载 
	     * @param urls 
	     * @return boolean 
	     */  
	    public boolean isDownloading(String... urls) {  
	        boolean result = false;  
	        for (int i = 0, length = urls.length; i < length; i++) {  
	            String url = urls[i];  
	            if (mDownloadTasks.containsKey(url)) {  
	                result = mDownloadTasks.get(url).isDownloading();  
	            }  
	        }  
	        return result;  
	    }  
	  
	    public static MyDownloadManager getInstance() {  
	        if (mInstance == null) {  
	            synchronized (MyDownloadManager.class) {  
	                if (mInstance == null) {  
	                    mInstance = new MyDownloadManager();  
	                }  
	            }  
	        }  
	        return mInstance;  
	    }  
	    
	    /** 
	     * 初始化下载管理器 
	     */  
	    private MyDownloadManager() {  
	        mDownloadTasks = new HashMap<>();  
	    }  
	    
}
