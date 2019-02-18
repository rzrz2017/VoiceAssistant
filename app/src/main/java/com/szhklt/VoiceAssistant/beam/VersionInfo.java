package com.szhklt.VoiceAssistant.beam;

import android.content.Context;
import android.content.Intent;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class VersionInfo {
	private Context context;
	private int version_apk;
	private String[] descriptions = null;
	private String apkurl;

    public VersionInfo(Context context){
    	this.context = context;
    }

	public void setAPKVersion(final int version){
		version_apk = version;
		LogUtil.e("ranzhen","setAPKVersion");
		MainApplication.UpdataMark = true;
		Intent intent = new Intent("android.szhklt.action.VERINFO_CHANDED");
		intent.putExtra("verInfoAct","version");
		context.sendBroadcast(intent);
	}
	
	public void setAPKdescription(String[] cdescription ){
		descriptions = new String[cdescription.length];
		for(int i = 0;i < cdescription.length;i++){
			if(cdescription[i] == null){
				break;
			}
			descriptions[i] = cdescription[i];
		}
		
		LogUtil.e("ranzhen","setAPKdescription");
		Intent intent = new Intent("android.szhklt.action.VERINFO_CHANDED");
		intent.putExtra("verInfoAct","description");
		context.sendBroadcast(intent);
	}
	
	public void setAPKUrl(final String capkurl ){
		apkurl = capkurl;
		
		LogUtil.e("ranzhen","setAPKUrl");
		Intent intent = new Intent("android.szhklt.action.VERINFO_CHANDED");
		intent.putExtra("verInfoAct","apkurl");
		context.sendBroadcast(intent);
	}
	
	
	public int getAPKVersion(){
		return version_apk;
	}
	public String getAPKdescription(int index){
		return descriptions[index] == null?" ":descriptions[index];
	}
	
	public String getAPKUrl(){
		return apkurl;
	}
}
