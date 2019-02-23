package com.szhklt.VoiceAssistant.service;

import java.util.Timer;
import java.util.TimerTask;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.DownloadManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.VersionInfo;
import com.szhklt.VoiceAssistant.multithreadeddownloader.CommomDialog;
import com.szhklt.VoiceAssistant.multithreadeddownloader.DownLoadUtils;
import com.szhklt.VoiceAssistant.multithreadeddownloader.MyDialog;
import com.szhklt.VoiceAssistant.multithreadeddownloader.UpdateActivity;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.multithreadeddownloader.CommomDialog.OnCloseListener;

public class checkAPKUpdataService extends Service{
    public DownloadManager mdownloadmanager;  
    public VersionInfo verinfo = null;
    //dialog相关
    CommomDialog mdialog;
    MyDialog builder;
    View view;
    LinearLayout btnYes;
    LinearLayout btnNo;
    
    TextView desc0;
    TextView desc1;
    TextView desc2;
    
    private Timer timer;
    private TimerTask task;

    private DownLoadUtils mdownloadutils;
    @SuppressLint("HandlerLeak") 
    private Handler mhandler = new Handler(){
		@Override
    	public void handleMessage(Message msg) {
    		// TODO Auto-generated method stub
        	LogUtil.e("ranzhen","**** lalala ****");
        	if(!MainApplication.isUpdateDialog){
        		MainApplication.isUpdateDialog = true;
        		setDesc(getApplicationContext());
        	}
    	}
    };
    
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		LogUtil.e("ranzhen","onCreate service successd");
		mdownloadutils = new DownLoadUtils(getApplicationContext());
//		showLoginDialog(getApplicationContext());
		timer = new Timer();
	}
	
	@Override
	public int onStartCommand(Intent intent,int flags,int startId) {
		// TODO Auto-generated method stub
	    task = new TimerTask() {  
	        @Override  
	        public void run() {
	        	LogUtil.e("ranzhen","checkAPKUpdate run()");
	        	mdownloadutils.DownLoadVerXML();//检查更新
	        	if(MainApplication.UpdataMark){//UpdateMark为真时需要弹框
	            	Message mmsg = new Message();
	            	mhandler.sendMessage(mmsg);
	            	LogUtil.e("ranzhen","sendMessage to show dialog");
	        	}
	        }  
	    }; 
		timer.schedule(task,0,6*60*60*1000); //1hours
		return super.onStartCommand(intent, flags, startId);
	}
		
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e("ranzhen","check apk UpdataService OnDestroy");
		if(timer != null){
			LogUtil.e("ranzhen","timer != null!!!");
			timer.cancel();
			if(task != null){
				LogUtil.e("ranzhen","task != null repeare cancel");
				task.cancel();
			}
		}
	}

    
	public void startUpdateActivity(){
		LogUtil.e("ranzhen","startUpdateActivity()");
		Intent UpdateActintent = new Intent(getApplicationContext(), UpdateActivity.class);
		UpdateActintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		getApplicationContext().startActivity(UpdateActintent);
	}
	
	//设置提示内容
	private void setDesc(Context context){
		String[] contexts = {MainApplication.theLastVer.getAPKdescription(1),
				             MainApplication.theLastVer.getAPKdescription(2), 
				             MainApplication.theLastVer.getAPKdescription(3)};
		OnCloseListener onCloseListener = new OnCloseListener() {
			@Override
			public void onClick(Dialog dialog, boolean confirm) {
				// TODO Auto-generated method stub
				if(confirm){
	                MainApplication.isUpdateDialog = false;
	            	startUpdateActivity();
	            	mdialog.cancel();
				}else{
	                MainApplication.isUpdateDialog = false;
	            	mdialog.cancel();
				}
			}
		};
		mdialog = new CommomDialog(context, R.style.dialog, contexts, onCloseListener);
		mdialog.setTitle("有新版本了");
		mdialog.show();
	}
	
}
