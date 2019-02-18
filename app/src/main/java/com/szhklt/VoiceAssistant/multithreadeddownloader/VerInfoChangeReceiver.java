package com.szhklt.VoiceAssistant.multithreadeddownloader;

import com.szhklt.VoiceAssistant.util.LogUtil;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class VerInfoChangeReceiver extends BroadcastReceiver{

	public String VERINFO_CHANDED = "android.szhklt.action.VERINFO_CHANDED";
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		LogUtil.e("ranzhen","VerInfoChangeReceiver  onReceive()");
		if(intent.getAction() == VERINFO_CHANDED){
			String verAct = intent.getStringExtra("verInfoAct");
			if("version".equals(verAct)){
				LogUtil.e("ranzhen","VerInfoChangeReceiver version");
				//重点是否需要更新标志
				//MainApplication.UpdataMark = true;
			}else if("description".equals(verAct)){
				LogUtil.e("ranzhen","VerInfoChangeReceiver description");
//				MainApplication.UpdataMark = true;
			}else if("apkurl".equals(verAct)){
				LogUtil.e("ranzhen","VerInfoChangeReceiver apkurl");
			}else {
				
			}

		}else if(intent.getAction() == "android.download.speech.CAN_DOWNLOAD_KW"){//需要下载kw
			LogUtil.e("kw","intent.getAction() == “android.download.speech.CAN_DOWNLOAD_KW”");
			//下载酷我
//			DownLoadUtils mdownload = new DownLoadUtils(context);
//			mdownload.DownloadKWAPK(MainApplication.kwUrl);
		}
	}
}
