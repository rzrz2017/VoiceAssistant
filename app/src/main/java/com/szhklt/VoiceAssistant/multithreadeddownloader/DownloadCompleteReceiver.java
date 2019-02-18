package com.szhklt.VoiceAssistant.multithreadeddownloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.szhklt.VoiceAssistant.util.LogUtil;

public class DownloadCompleteReceiver extends BroadcastReceiver {
	public final static String XmlPath = "Android/data/com.szhklt.VoiceAssistant/files/Download";
	DownLoadUtils mdowmloadutils;
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		mdowmloadutils = new DownLoadUtils(context.getApplicationContext());
        if (intent.getAction().equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            long downId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1); 
    		if(mdowmloadutils.getDownloadUri(downId).getPath().contains("VersionInfo.xml")){
    			LogUtil.e("ranzhen","xml file download compeleted!!!");
    			mdowmloadutils.Domj4_VersionXML_Prase();
				mdowmloadutils.mDownloadManager.remove(downId);
				//FilesUtil.getInstance().deleteDirWihtFile(new File("/sdcard/Android/data/com.szhklt.VoiceAssistant/files/Download"));
    		}else if(mdowmloadutils.getDownloadUri(downId).getPath().contains("Speech.apk")){
    			LogUtil.e("ranzhen","Speech.apk has been downloadcomplete");
    			//LauncherAppliaction.getContext().getContentResolver().unregisterContentObserver(mco);
    			//silentInstallApk();
    			//installApk(getApplicationContext());
    		}
        }
	} 
	
    public int getAPPVersionCode(Context ctx) {  
        int currentVersionCode = 0;  
        PackageManager manager = ctx.getPackageManager();  
        try {  
            PackageInfo info = manager.getPackageInfo(ctx.getPackageName(), 0);  
            String appVersionName = info.versionName; 
            currentVersionCode = info.versionCode; 
            System.out.println(currentVersionCode + " " + appVersionName);  
        } catch (NameNotFoundException e) {  
            e.printStackTrace();  
        }  
        return currentVersionCode;  
    } 
}
