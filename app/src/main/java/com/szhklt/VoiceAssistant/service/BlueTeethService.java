package com.szhklt.VoiceAssistant.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.InvalidParameterException;
import java.util.Arrays;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.SerialPort;
import com.szhklt.VoiceAssistant.activity.BlueToothActivity.Refresher;
import com.szhklt.VoiceAssistant.floatWindow.FloatActionButtomView;
import com.szhklt.VoiceAssistant.util.LineInControler;
import com.szhklt.VoiceAssistant.util.LogUtil;

/**
 * 通过share传递蓝牙数据
 * @author rz
 *
 */
public class BlueTeethService extends Service{
	public static final String TAG = "BlueTeethService";
	public static final String BLE_ACTION = "android.intent.action.BLUETOOTH"; 
	
	public static Boolean bluePushEndable;
	
	public static Boolean getBluePushStatus() {
		return bluePushEndable;
	}
	
	private OutputStream mOutputStream;
	private InputStream mInputStream;
	private SerialPort mSerialPort;
	private ReadThread mReadThread;

	private BlueToothControlReceiver mBlueToothControlReceiver; 
	private IntentFilter intentFilter;
	private BlueToothBinder blueToothBinder = new BlueToothBinder();
	
	private SharedPreferences sp;
	private SharedPreferences.Editor editor;
	
	private Refresher bleActRefresher;
	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return blueToothBinder;
	}

	public class BlueToothBinder extends Binder{
		/**
		 * 改名字
		 */
		public void changeName(String name){
			changeNameByDS(name);
		}
		
		/**
		 * 获取蓝牙名字
		 */
		public void getDeviceName(){
			requestDeviceName();
		}
		/**
		 * 获取已连接的设备名字
		 */
		public void getConnectedDevice(){
			requestConnectedDevice();
		}
		/**
		 * 播放/暂停
		 */
		public void setToggle(){
			requestToggle();
		}
		/**
		 * 播放上一首
		 */
		public void bleNext(){
			requestNext();
		}
		/**
		 * 播放下一首
		 */
		public void blePre(){
			requestPre();
		}
		/**
		 * 增加音量
		 */
		public void bleDecreaseVol(){
			requestDecrease();
		}
		/**
		 * 减小音量
		 */
		public void bleIncreaseVol(){
			requestIncrease();
		}
		
		/**
		 * 改变蓝牙名字
		 */
		public void bleChangeNameAndPwd(String name,String password){
			LogUtil.e(TAG, "改变蓝牙名字,"+"password:"+password+LogUtil.getLineInfo());
			changeBleName(name,password);
		}
		
		public void setBleActRefresher(Refresher bleRefresher) {
			bleActRefresher = bleRefresher;
		}
	} 

	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();	
		LogUtil.e(TAG,"onCreate()");
		sp = MainApplication.getContext().getSharedPreferences("blueteeth_data",MODE_PRIVATE);
		editor = sp.edit();
		
		LogUtil.e(TAG,"Share被创建");
		try {
			mSerialPort = getSerialPort();
			mOutputStream = mSerialPort.getOutputStream();
			mInputStream = mSerialPort.getInputStream();

			mReadThread = new ReadThread();
			mReadThread.start();
		} catch (InvalidParameterException e) {
			// TODO Auto-generated catch block
			LogUtil.e(TAG, "没有配置串口");
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			LogUtil.e(TAG, "没有串口读取权限");
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.e(TAG,"串口不能被打开");
			e.printStackTrace();
		}
		
		requestStatus();
		requestDeviceName();
		
		intentFilter = new IntentFilter();
		intentFilter.addAction(BLE_ACTION);
		mBlueToothControlReceiver = new BlueToothControlReceiver();
		registerReceiver(mBlueToothControlReceiver, intentFilter);
		
		bluePushEndable = true;
	}
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"onDestroy()"+LogUtil.getLineInfo());
		if(FloatActionButtomView.auxinStatus == false){
			LineInControler.getInstance().stopLineIn();
		}
		
		bluePushEndable = false;
		if(mReadThread != null){
			mReadThread.interrupt();
		}
		closeSerialPort();
		mSerialPort = null;
		bleActRefresher = null;
		super.onDestroy();
	}

	private class ReadThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			while(!isInterrupted()){
				int size;
				try {
					byte[] buffer = new byte[64];
					if(mInputStream == null) return;
					size = mInputStream.read(buffer);
					if(size > 0){
						bleDataDispose(buffer, size, editor);
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
			}
		}
	}

	/**
	 * 蓝牙数据处理
	 * @param buffer
	 * @param size
	 */
	private void bleDataDispose(byte[] buffer,int size,SharedPreferences.Editor bleEditor){
		byte flag = buffer[2];//功能判断
		LogUtil.e(TAG, "flag:"+flag+","+Integer.toBinaryString(flag));
		
		byte[] buf = new byte[size];
		for(int i = 0;i < size;i++){
			buf[i] = buffer[i];
		}
		
		LogUtil.e(TAG, "-------------------"+bytesToHexFun1(buf));
		LogUtil.e(TAG, "size:"+size);
		
		byte nameBuf[] = new byte[32];
		for(int i = 0;i < size;i++){
			int t = i +3;
			if(t >= size){
				break;
			}
			nameBuf[i] = buf[t];
		}
		
		if(flag == 64){
			LogUtil.e(TAG,"密码或名字修改成功");
		}
		
		//请求当前是否为蓝牙状态
		if(flag == 18){
			byte bleConnectStatus = buffer[3];//功能判断
			if(bleConnectStatus == 0){
				LogUtil.e(TAG,"没有连接");
				//切换音源
				bleEditor.putBoolean("status",false);
				bleEditor.commit();
				if(bleActRefresher != null){
					bleActRefresher.refreshSwitchButtonIcon();
					bleActRefresher.setTitle();
				}
			}else if(bleConnectStatus == 1){
				LogUtil.e(TAG,"已连接");
				//切换音源
				LineInControler.getInstance().switchBle();
				bleEditor.putBoolean("status",true);
				bleEditor.commit();
				requestConnectedDevice();
				if(bleActRefresher != null)
					bleActRefresher.refreshSwitchButtonIcon();
			}
		}

		//获取蓝牙名
		if(size == 32){//32字节
			if(flag == 16){//蓝牙名字
				int index  = 0;
				byte[] bleNameArray = new byte[29];
				bleNameArray = Arrays.copyOfRange(buffer, 8, 32);
				for(int i = bleNameArray.length-1;i >= 0;i --){
					if(bleNameArray[i] != 0){
						index = i;
						break;
					}
				}
				String bleNameStr = new String(bleNameArray, 0,index+1);
				LogUtil.e(TAG,"蓝牙名字：---"+bleNameStr+"----");
				bleEditor.putString("devicename",bleNameStr);
				bleEditor.commit();
				if(bleActRefresher != null)
					bleActRefresher.refreshDeviceName();
			}else if(flag == 17){//手机名字
				int index  = 0;
				byte[] clientNameArray = new byte[29];
				clientNameArray = Arrays.copyOfRange(buffer, 3, 32);
				for(int i = clientNameArray.length-1;i >= 0;i --){
					if(clientNameArray[i] != 0){
						index = i;
						break;
					}
				}
				String clientNameStr = new String(clientNameArray, 0, index+1);
				LogUtil.e(TAG,"手机名字:"+clientNameStr);
				bleEditor.putString("connecteddevice",clientNameStr);
				bleEditor.commit();
				if(bleActRefresher != null)
					bleActRefresher.refreshConnectedDevice();
			}
		}
//		sendBroadcast(new Intent("android.intent.action.REFRESH_UI"));
	}

	/**
	 * 获取串口
	 */
	public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
		if (mSerialPort == null) {
			/* Read serial port parameters */
			SharedPreferences sp = getSharedPreferences("android_serialport_api.sample_preferences", MODE_PRIVATE);
			String path = sp.getString("DEVICE", "");
			LogUtil.e(TAG,"path:"+path+LogUtil.getLineInfo());
			path = "/dev/ttyS2";
//			int baudrate = Integer.decode(sp.getString("BAUDRATE", "-1"));
			int baudrate = 115200;//写死
			
			/* Check parameters */
			if ( (path.length() == 0) || (baudrate == -1)) {
				throw new InvalidParameterException();
			}

			/* Open the serial port */
			mSerialPort = new SerialPort(new File(path), baudrate, 0);
		}
		return mSerialPort;
	}

	/**
	 * 关闭串口
	 */
	public void closeSerialPort() {
		if (mSerialPort != null) {
			mSerialPort.close();
			mSerialPort = null;
		}
	}

	/**
	 * 蓝牙控制广播接收者
	 * @author rz
	 *
	 */
	private class BlueToothControlReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(BLE_ACTION.equals(action)){
				String bleAction = intent.getStringExtra("bleaction");
				if(bleAction.equals("requestConnectedDevice")){//获取已连接的设备名
					requestConnectedDevice();
				}else if(bleAction.equals("requestDeviceName")){//获取蓝牙名
					requestDeviceName();
				}else if(bleAction.equals("requestStatus")){//请求蓝牙状态
					requestStatus();
				}else if(bleAction.equals("requestToggle")){//播放/暂停
					requestToggle();
				}else if(bleAction.equals("requestPre")){//上一曲
					requestPre();
				}else if(bleAction.equals("requestNext")){//下一曲
					requestNext();
				}else if(bleAction.equals("requestIncrease")){//音量加
					requestIncrease();
				}else if(bleAction.equals("requestDecrease")){//音量减
					requestDecrease();
				}
			}
		}
	}

	/************控制操作************/
	private void changeNameByDS(String name){
		StringBuilder stringBuilder = new StringBuilder();
		String nameLen;
		if(name.length() < 10){
			nameLen = String.valueOf("0"+name.length());
		}else{
			nameLen = String.valueOf(name.length());
		}
		stringBuilder.append("AT+NAME=");
		stringBuilder.append(nameLen);
		stringBuilder.append(",");
		stringBuilder.append(name);
		LogUtil.e(TAG,"stringBuilder:"+stringBuilder.toString());
		
		try {
			mOutputStream.write(stringBuilder.toString().getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取蓝牙名字
	 */
	private void requestDeviceName(){
		LogUtil.e(TAG,"requestDeviceName() --- 请求蓝牙名");
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x10;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 请求连接的设备名
	 */
	private void requestConnectedDevice(){
		LogUtil.e(TAG,"requestConnectedDevice() --- 请求手机名");
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x11;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 请求连接的状态
	 */
	private void requestStatus(){
		LogUtil.e(TAG,"requestStatus() --- 请求连接的状态");
		LogUtil.e(TAG,"请求连接的状态"+LogUtil.getLineInfo());
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x12;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 播放/暂停
	 */
	private void requestToggle(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x20;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 播放下一首
	 */
	private void requestNext(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x21;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 播放上一首
	 */
	private void requestPre(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x22;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 减少音量
	 */
	private void requestDecrease(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x24;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 增加音量
	 */
	private void requestIncrease(){
		byte[] bytes = new byte[5];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x23;
		bytes[3] = (byte)0xFE;
		bytes[4] = (byte)0xFE;

		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 更改蓝牙名与密码
	 * @param name 蓝牙名
	 * @param password 密码
	 */
	private void changeBleName(String name,String password){
		byte[] nameBytes = new byte[32];
		
		byte[] temp = name.getBytes();
		if(temp.length < 16){
			for(int i= 0; i < temp.length;i++){
				nameBytes[i] = temp[i];
			}
		}else{
			for(int i= 0; i < 16;i++){
				nameBytes[i] = temp[i];
			}
		}
		
		byte[] bytes = new byte[42];
		bytes[0] = (byte)0xFB;
		bytes[1] = (byte)0xFB;
		bytes[2] = (byte)0x40;
		
		bytes[3] = (byte)0x00;//模式
		//密码
		bytes[4] = (byte)0x00;
		bytes[5] = (byte)0x00;
		bytes[6] = (byte)0x00;
		bytes[7] = (byte)0x00;
		
		if("".equals(password) == false){
			bytes[3] = (byte)0x01;
			byte[] pwdBytes = password.getBytes();
			if(pwdBytes.length >= 4){
				for(int i=0;i < 4;i++){
					bytes[i+4] = pwdBytes[i];
				}
			}else{
				for(int i=0;i < pwdBytes.length;i++){
					bytes[i+4] = pwdBytes[i];
				}
			}
			//LogUtil.e(TAG,"输入的密码:Str="+new String(pwdBytes)+",bytes="+bytesToHexFun1(pwdBytes));
		}
		
		
		for(int i = 8;i < 40;i++){
			bytes[i] = nameBytes[i-8];
		}
		
		bytes[40] = (byte)0xFE;
		bytes[41] = (byte)0xFE;
		
		try {
			mOutputStream.write(bytes);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			LogUtil.e(TAG, "修改蓝牙名出错！");
			e.printStackTrace();
		}
	}
	
    private static final char[] HEX_CHAR = {'0', '1', '2', '3', '4', '5', 
        '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
	
    /**
     * byte[] to hex string 主要用于测试
     * 
     * @param bytes
     * @return
     */
    public static String bytesToHexFun1(byte[] bytes) {
        // 一个byte为8位，可用两个十六进制位标识
        char[] buf = new char[bytes.length * 2];
        int a = 0;
        int index = 0;
        for(byte b : bytes) { // 使用除与取余进行转换
            if(b < 0) {
                a = 256 + b;
            } else {
                a = b;
            }
            buf[index++] = HEX_CHAR[a / 16];
            buf[index++] = HEX_CHAR[a % 16];
        }
        return new String(buf);
    }
}
