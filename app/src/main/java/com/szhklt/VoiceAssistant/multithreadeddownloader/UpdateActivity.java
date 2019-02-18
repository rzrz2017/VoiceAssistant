package com.szhklt.VoiceAssistant.multithreadeddownloader;

import java.io.File;
import java.io.IOException;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class UpdateActivity extends Activity implements OnClickListener {

	ProgressBar pb_progress1;
	Button start;
	Button stop;
	MyDownloadManager mDownloadManager;
	TextView apkpath;
	TextView tip;//进度百分比
	TextView description0;
	TextView description1;	
	TextView description2;
	TextView description3;
	RadioGroup rg;
	
	public static TextView adcjetinfo;
	public static TextView versioninfo;
	//public RelativeLayout content;
	public RelativeLayout progressfield;
    //String wechatUrl = "http://www.szhklt.cn/apkupdate/background_music/apk/CustomGalleryLikeiPhonesign.apk";//默认文件路径
    private String wechatUrl = null;
	private boolean btuLock = true;//按钮锁,默认可点击
    private boolean contentLock = false;//内容锁，默认不可见//表示正在下载
    
    private IntentFilter intentFilter;
    private NetworkChangeReceiver networkChangeReceiver;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	LogUtil.e("ranzhen", "onCreate()");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apkupdata);
//        MainService.isLauncherAcyivity=false;
        if(MainApplication.theLastVer==null){
        	finish();
        }
        //检查更新
        //DownLoadUtils.getInstance().DownLoadVerXML();//下载XML文件，即是检查更新
    	wechatUrl = MainApplication.theLastVer.getAPKUrl();//获取url
    	
    	LogUtil.e("ranzhen","wechatUrl = "+wechatUrl);
    	initNetworkChangeReceiver();
        initViews();
        initDatas();
        initDownloads();
    }
    
    @Override
    protected void onResume() {
    	// TODO Auto-generated method stub
    	super.onResume();
    	LogUtil.e("ranzhen","onResume()");
    	
        versioninfo.setText("当前版本:"+DownLoadUtils.getAPPVersionCode(this)+"最新版本:"+MainApplication.theLastVer.getAPKVersion());
    	MainApplication.isUpdateDialog = true;
		if(!MainApplication.UpdataMark){
			btuLock=false;
		}else{
			btuLock=true;
		}
    }
    
    private void initNetworkChangeReceiver(){
    	intentFilter = new IntentFilter();
    	intentFilter.addAction("android.net.conn.CONNECTIVITY_CHANGE");
    	networkChangeReceiver = new NetworkChangeReceiver();
    	registerReceiver(networkChangeReceiver, intentFilter);
    }
    
    private void initViews(){
    	pb_progress1 = (ProgressBar)findViewById(R.id.progressBar1);
    	start = (Button)findViewById(R.id.start);
    	start.setOnClickListener(this);
    	stop = (Button)findViewById(R.id.cancel);
    	stop.setOnClickListener(this);
    	apkpath = (TextView)findViewById(R.id.apkpath);		
    	tip = (TextView)findViewById(R.id.tip);
    	adcjetinfo = (TextView)findViewById(R.id.adcjettype);
    	versioninfo = (TextView)findViewById(R.id.versioninfo);
		rg = (RadioGroup)findViewById(R.id.rg);
		rg.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				switch (checkedId) {
				case R.id.radioButton1:
					MainApplication.rgbuttonstatus = 1;
	                View viewById1 = rg.findViewById(checkedId);
	                if (!viewById1.isPressed()){
	                    return;
	                }
					//开启自动更新检查
					LogUtil.e("ranzhen","**** radioButton1 has been changed ****");
					break;
				case R.id.radioButton2:
					MainApplication.rgbuttonstatus = 2;
	                View viewById2 = rg.findViewById(checkedId);
	                if (!viewById2.isPressed()){
	                    return;
	                }
				    //关闭自动更新检查
					startCheckForUpdatasApkService(false);
					break;
				default:
					break;
				}
			}
		});
    	//content = (RelativeLayout)findViewById(R.id.content);
    	description0 = (TextView)findViewById(R.id.description0);
    	description1 = (TextView)findViewById(R.id.description1);
    	description2 = (TextView)findViewById(R.id.description2);
    	description3 = (TextView)findViewById(R.id.description3);
    	
    	progressfield = (RelativeLayout)findViewById(R.id.progressfield);
    }
    
    private void initDatas(){
    	LogUtil.e("ranzhen","initDatas()");
//    	LogUtil.e("ranzhen","getAPKdescription() = "+MainApplication.theLastVer.getAPKdescription(0));
//    	String ss = LauncherAppliaction.theLastVer.getAPKdescription(0);
    	//versioninfo.setText(MainApplication.theLastVer.getAPKdescription(0));
    	adcjetinfo.setText("当前ADC类型是:"+MainApplication.ADCTPYE+";唤醒词是:"+(MainApplication.JETNAME.contains("dingdong")?"叮咚叮咚":"小巴小巴"));
    	description0.setText("新版本信息");
    	description1.setText(MainApplication.theLastVer.getAPKdescription(1));
    	description2.setText(MainApplication.theLastVer.getAPKdescription(2));
    	description3.setText(MainApplication.theLastVer.getAPKdescription(3));
    }

    private void initDownloads() {
        mDownloadManager = MyDownloadManager.getInstance();
        mDownloadManager.add(wechatUrl,new DownloadListner() {
            @Override
            public void onFinished() {
            	start.setText("下载");
            	//stop.setVisibility(View.GONE);
                Toast.makeText(UpdateActivity.this, "下载完成!", Toast.LENGTH_SHORT).show();
                finish();
                install();//speeck下载完成
            }
            
            @Override
            public void onProgress(float progress) {
//            	LogUtil.e("ranzhen","onProgress() callback,progress =="+String.valueOf(progress));
            	//一旦进入下载后，显示content
            	if(!contentLock){
                	contentLock = true;//内容锁，解锁
                	progressfield.setVisibility(View.VISIBLE);
            	}
            	
                pb_progress1.setProgress((int) (progress * 100));
                tip.setText("下载进度："+String.format("%.2f", progress * 100) + "%");
            }

            @Override
            public void onPause() {
                Toast.makeText(UpdateActivity.this, "暂停了!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel() {
            	tip.setText("下载进度：0%");
                pb_progress1.setProgress(0);
                Toast.makeText(UpdateActivity.this, "下载已取消!", Toast.LENGTH_SHORT).show();
                apkpath.setText("安装包路径");
            	start.setText("下载");
            	stop.setVisibility(View.GONE);
            	
            	contentLock = false;//表示content不应该显示
            	progressfield.setVisibility(View.GONE);
            }
        });
    }
    

    @Override
    protected void onStop() {
    	// TODO Auto-generated method stub
    	super.onStop();
    	MainApplication.isUpdateDialog = false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.start){//下载
			if(MainApplication.UpdataMark == false){
				Toast.makeText(this, "主人,当前版本为最新版本,无需更新呢!", Toast.LENGTH_SHORT).show();
				return;
			}
			
			LogUtil.e("updata",start.getText().toString());
			
			if("更新".equals(start.getText().toString()) || "下载".equals(start.getText().toString())){
				LogUtil.e("updata","清空安装目录");
				//清空安装目录	
				String fileName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainApplication.speechupdate + File.separator;
				File file = new File(fileName);
				if(!file.exists()){
					LogUtil.e("updata","删除失败");
				}else{
					if(file.isDirectory()){
						File[] files = file.listFiles();  
						for (int i = 0; i < files.length; i++) {  
							if (files[i].isFile()) {
								LogUtil.e("updata","删除文件"+files[i].getName());
								files[i].delete();
							}
						}
					}
				}
			}
			
			if(btuLock){
//				btuLock = false;//上锁
	            if (!mDownloadManager.isDownloading(wechatUrl)) {
	                LogUtil.e("ranzhen","you clicked pause");
	                mDownloadManager.download(wechatUrl);//继续下载
	                apkpath.setText(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainApplication.speechupdate + File.separator);
	                start.setText("暂停");
	            	stop.setVisibility(View.VISIBLE);
	            	contentLock = true;
	            	progressfield.setVisibility(View.VISIBLE);
	            } else {
	                LogUtil.e("ranzhen","you clicked start");
	            	mDownloadManager.pause(wechatUrl);//暂停下载
	            	start.setText("继续");
	            }	            
			}

		}else if(v.getId() == R.id.cancel){
//			if(btuLock){
//				btuLock = false;
	            LogUtil.e("ranzhen","you clicked stop");
				mDownloadManager.cancel(wechatUrl);
//			}
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
	    if(keyCode == KeyEvent.KEYCODE_BACK){  
	        moveTaskToBack(true);  
	        return true;  
	    }  
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(networkChangeReceiver);
	}
	
	class NetworkChangeReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			ConnectivityManager connectivityManager = (ConnectivityManager)
					getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
			if(networkInfo != null && networkInfo.isAvailable()){
            }else {
                setContentView(R.layout.activity_apkupdata);
                initViews();
                initDatas();
                initDownloads();
			}
	    }
	}
	
    /**
     * @param saveDir
     * @return
     * @throws IOException
     * 判断下载目录是否存在
     */
	@SuppressWarnings("unused")
	private String isExistDir(String saveDir) throws IOException {
        // 下载位置
        File downloadFile = new File(Environment.getExternalStorageDirectory(), saveDir);
        if (!downloadFile.mkdirs()) {
            downloadFile.createNewFile();
        }
        String savePath = downloadFile.getAbsolutePath();
        return savePath;
    }

    /**
     * @param url
     * @return
     * 从下载连接中解析出文件名
     */
    private String getNameFromUrl(String url) {
        return url.substring(url.lastIndexOf("/") + 1);
    }
    
    private void install(){
        String apkFilePath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + MainApplication.speechupdate + File.separator;
        LogUtil.e("ranzhen","apkFilePath == " + apkFilePath);
        //apk文件的本地路径  
        File apkfile = new File(apkFilePath+getNameFromUrl(MainApplication.theLastVer.getAPKUrl()));  
        //会根据用户的数据类型打开android系统相应的Activity。  
        Intent intent = new Intent(Intent.ACTION_VIEW);  
        //设置intent的数据类型是应用程序application  
        intent.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");  
        //为这个新apk开启一个新的activity栈  
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        //开始安装  
        startActivity(intent);  
        
        //关闭旧版本的应用程序的进程  
        //android.os.Process.killProcess(android.os.Process.myPid()); 
    }
    
    //开启自动检查更新apk服务
	private void startCheckForUpdatasApkService(final boolean enable) {
		if(enable){
			Intent updataIntent = new Intent("com.szhklt.service.CHECK_UPDATA");//启动更新服务
	        startService(updataIntent);
		}else{
			Intent stopupdataIntent = new Intent("com.szhklt.service.CHECK_UPDATA");//停止更新服务
			stopService(stopupdataIntent);
		}
	}
	
}
