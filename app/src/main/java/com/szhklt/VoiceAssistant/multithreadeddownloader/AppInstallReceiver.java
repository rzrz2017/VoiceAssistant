package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.File;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;

import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class AppInstallReceiver extends BroadcastReceiver{
	public final static String KWApkPath = "Android/data/com.szhklt.VoiceAssistant/files/Download/kwplay.apk";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//安装apk
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_ADDED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            if("cn.kuwo.kwmusiccar".equals(packageName)){//表示kw安装成功
            	//删除名字为kwplay的apk
            	boolean isExists = FilesUtil.getInstance().judeFileExists(new File(Environment.getExternalStorageDirectory().getPath()+KWApkPath));
				if(isExists){
	            	DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
	            	mDownloadManager.remove(VersionUtil.getInstance().getKwId(context));
				}
            }
        }
        
        //卸载APK
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REMOVED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
        	LogUtil.e("666","ACTION_PACKAGE_REMOVED,packageName = "+packageName);
        	if("cn.kuwo.kwmusiccar".equals(packageName)){
				Intent serviceIntent = new Intent(context, MainService.class);
				context.startService(serviceIntent);
        	}
        }
        
        //替换apk
        if (intent.getAction().equals(Intent.ACTION_PACKAGE_REPLACED)) {
            String packageName = intent.getData().getSchemeSpecificPart();
            LogUtil.e("666","ACTION_PACKAGE_REPLACED,packageName = "+packageName);
            if("cn.kuwo.kwmusiccar".equals(packageName)){//表示kw安装成功
            	LogUtil.e("ranzhen","packageName == cn.kuwo.kwmusiccar");
            	//删除
            	DownloadManager mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            	mDownloadManager.remove(VersionUtil.getInstance().getKwId(context));
            }
        }
	}
	
    public void removeFile(Context context) {
    	DownLoadUtils mdownLoadUtils = new DownLoadUtils(context);
        String filePath = mdownLoadUtils.getDownloadUri(SystemParams.getInstance().getLong(DownloadManager.EXTRA_DOWNLOAD_ID)).getPath();
        if(null != filePath) {
            File downloadFile = new File(filePath);
            if(null != downloadFile && downloadFile.exists()) {
                downloadFile.delete();
            }
        }
    }
}
