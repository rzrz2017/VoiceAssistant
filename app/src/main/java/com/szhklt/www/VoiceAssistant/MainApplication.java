package com.szhklt.www.VoiceAssistant;

import java.lang.reflect.Method;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.szhklt.www.VoiceAssistant.service.MainService;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

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

//	/**
//	 * setter
//	 * @param mBleDataReceiver
//	 */
//	public void setmBleDataReceiver(BleDataReceiver mBleDataReceiver) {
//		this.mBleDataReceiver = mBleDataReceiver;
//	}
	
//	/**
//	 * 获取串口读取线程
//	 * @return
//	 */
//	public ReadThread getReadThread(){
//		if(mReadThread != null){
//			return mReadThread;
//		}else{
//			return null;
//		}
//	}

	@Override
	public void onCreate() {
		super.onCreate();
		LogUtil.e(TAG,"onCreate()"+LogUtil.getLineInfo());
		context = getApplicationContext();
		{
			crashHandler = new CrashHandler();
			CrashHandler catchHandler = crashHandler.getInstance();
			catchHandler.init(getApplicationContext());
		}
		{//初始化appid
			StringBuffer param = new StringBuffer();
			param.append("appid="+getString(R.string.app_id));
			param.append(",");

			param.append(SpeechConstant.ENGINE_MODE+"="+SpeechConstant.MODE_MSC);
			param.append("server_url=http://dz-szhklt.xf-yun.com/msp.do");//修改了APPID后需要修改这里
			SpeechUtility.createUtility(MainApplication.this, param.toString());
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