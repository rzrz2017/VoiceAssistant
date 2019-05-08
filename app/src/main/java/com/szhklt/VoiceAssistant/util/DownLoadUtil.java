package com.szhklt.VoiceAssistant.util;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;

import com.szhklt.VoiceAssistant.MainApplication;

public class DownLoadUtil {
    private static DownLoadUtil instance;

    private DownloadManager downManager;
    private long id;

    private DownLoadUtil(){
        downManager = (DownloadManager) MainApplication.getContext().
                getSystemService(Context.DOWNLOAD_SERVICE);

    }

    public static DownLoadUtil getInstance(){
        if(instance == null){
            instance = new DownLoadUtil();
        }
        return instance;
    }

    public long download(String url){
        if(downManager != null){
            id = downManager.enqueue(buildRequest(url));
            return id;
        }
        return -1;
    }

    private DownloadManager.Request buildRequest(String url){
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDestinationInExternalFilesDir(MainApplication.getContext(),
                Environment.DIRECTORY_DOWNLOADS,"lrcdir");
        return request;
    }

    public void cancel(long id){
        if(downManager != null){
            downManager.remove(id);
        }
    }
}
