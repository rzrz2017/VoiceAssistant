package com.szhklt.VoiceAssistant;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.rich.czlylibary.sdk.MiguCzlySDK;
import com.rich.player.sdk.InitPlayerServiceCallback;
import com.rich.player.sdk.PlayMusicClient;
import com.szhklt.VoiceAssistant.beam.VersionInfo;
import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.lang.reflect.Method;
import java.util.List;

public class MainApplication extends Application{
	private final static String TAG = "MainApplication";
	public static final boolean longWakeUp = true;//表示长唤醒
	//ac108apkurldingdong
	//cx20810apkurldingdong
	//ac108apkurlxiaoba
	//cx20810apkurlxiaoba
	/*********************************以下四选一********************************************/	
	//小吧 CX20810
	//		public static String JETTPYE = "xiaoba";
	//		public static String ADCTPYE = "cx20810";
	//		public static String JETNAME = "xiaobaxiaoba_4mic.jet";

	//小吧 AC108
	//		public static String JETTPYE = "xiaoba";
	//		public static String ADCTPYE = "ac108";
	//		public static String JETNAME = "xiaobaxiaoba_4mic.jet";

	//叮咚 CX20810
	//	public static String JETTPYE = "dingdong";
	//	public static String ADCTPYE = "cx20810";
	//	public static String JETNAME = "dingdong.jet";	

	//叮咚 AC108
	public static String JETTPYE = "dingdong";
	public static String ADCTPYE = "ac108";
	public static String JETNAME = "dingdong.jet";
	/**************************************************************************************/
	private CrashHandler crashHandler;
	public static boolean ValueAnimationFlag = true;//ture表示播放完成
	public static String speechupdate = "speechupdate";
	public static boolean UpdataMark = false;//更新标志
	public static boolean isUpdateDialog = false;
	public static boolean isCheckUpdata = false;
	public static boolean isDownloadAPK = false;
	public static boolean isDownloadKW = false;//正在下载kw
	public static boolean isinstallKW = false;

	public static int tmp;//和当前下载的酷我APK绑定，下载完成后存入share
	public static long tmpId;
	public static int netKWVer = 0; //网上的酷我号//每下载一次xml更新一次
	public static String kwUrl;

	public static int rgbuttonstatus = 2;//默认情况下选中rgbutton2
	public static VersionInfo theLastVer = null;
	private static Context context;

	public static String firmwareVersion=null;//固件版本号


	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.e(TAG,"onCreate()"+ LogUtil.getLineInfo());
		context = getApplicationContext();
		{
			crashHandler = new CrashHandler();
			CrashHandler catchHandler = crashHandler.getInstance();
			catchHandler.init(getApplicationContext());
		}
		//初始化版本信息beams
		theLastVer = new VersionInfo(getApplicationContext());
		{//app启动时需要启动的一下服务
			//启动MainService!
			startService(new Intent(this, MainService.class));

			//启动Activity管理器服务
//			startService(new Intent(this,ActivityManagerService.class));
			
			//启动蓝牙服务
//			startService(new Intent(this,BlueTeethService.class));
		}
		Toast.makeText(getApplicationContext(), "语音助手启动了", Toast.LENGTH_SHORT).show();
//		FloatWindowManager.getInstance().createFloatButton(getApplicationContext());//创建悬浮按钮
		firmwareVersion=getString();

		//初始化咪咕音乐
		String processName = getProcessName(this);
		if (processName != null) {
			if (getPackageName().equals(processName)) {
				initPlayer();
				MiguCzlySDK.getInstance().init(this)
						.setSmartDeviceId("11ae83f3b3feba")
						.setUid("111111")
						.setPhoneNum("18813976199")
						.setKey("246e74c078e79adf");
				init();
				PlayMusicClient.getInstance().init(this,false).setInitCallback(new InitPlayerServiceCallback() {
					@Override
					public void callback(String code, String msg) {

					}
				});
			}
		}
	}

	private String getProcessName(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps == null) {
			return null;
		}
		for (ActivityManager.RunningAppProcessInfo proInfo : runningApps) {
			if (proInfo.pid == android.os.Process.myPid()) {
				if (proInfo.processName != null) {
					return proInfo.processName;
				}
			}
		}
		return null;
	}

	/**
	 * 初始化播放器
	 */
	private void initPlayer() {

	}

	private void init() {
	}

	/**
	 * 全局获取上下文
	 * @return
	 */
	public static Context getContext(){
		return context;
	}
	
	/**
	 * 获取固件号
	 * @return ro.product.firmware 对应的数据
	 */
	private String getString(){
		String value = null;
		try {
			Class<?> c = Class.forName("android.os.SystemProperties");
			Method get = c.getMethod("get", String.class, String.class);
			value = (String)(get.invoke(c, "ro.product.firmware", "unknown" ));
			LogUtil.e("main", value);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			return value;
		}
	}
}