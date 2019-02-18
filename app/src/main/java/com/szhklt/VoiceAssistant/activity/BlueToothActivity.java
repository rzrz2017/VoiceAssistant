package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.service.BlueTeethService;
import com.szhklt.VoiceAssistant.service.BlueTeethService.BlueToothBinder;
import com.szhklt.VoiceAssistant.util.LineInControler;
import com.szhklt.VoiceAssistant.util.LogUtil;


public class BlueToothActivity extends Activity implements OnClickListener{
	private static final String TAG = "BlueToothActivity";
	private LinearLayout contentView;
	private ImageView switchButton;
	private ImageView nextButton;
	private ImageView preButton;
	private ImageView toggleButton;
	private ImageView editIcon;
	private ImageView pwdEditIcon;
	
	private EditText deviceName;
	private EditText devicePwd;
	private TextView connectName;
	
	private Boolean toggleButtonFlag = false;
	private Boolean switchButtonFlag = false;
	
	private BlueToothBinder blueToothBinder;
	private SharedPreferences blePref;
	private SharedPreferences.Editor editor;
	
	private Handler mHandler = new Handler();
	private BlueToothReceiver blueToothReceiver;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bluetooth);
		
		blePref = MainApplication.getContext().getSharedPreferences("blueteeth_data",MODE_PRIVATE);
		editor = blePref.edit();

		initView();
		
		IntentFilter intentFilter = new IntentFilter();
		intentFilter = new IntentFilter();
		intentFilter.addAction("android.intent.action.REFRESH_UI");
		intentFilter.addAction("android.bluetooth.action.FINISH");
		
		blueToothReceiver = new BlueToothReceiver();
		registerReceiver(blueToothReceiver,intentFilter);
		
		Intent bindIntent = new Intent(this,BlueTeethService.class);
		bindService(bindIntent, connection, BIND_AUTO_CREATE);
	}
	
	private void initView() {
		contentView = (LinearLayout)findViewById(R.id.contentview);
		contentView.setOnTouchListener(touchListener);
		editIcon = (ImageView)findViewById(R.id.edit_icon);
		editIcon.setOnClickListener(this);
		
		pwdEditIcon = (ImageView)findViewById(R.id.pwd_edit_icon);
		pwdEditIcon.setOnClickListener(this);
		
		switchButton = (ImageView)findViewById(R.id.bluebutton);
		switchButton.setOnClickListener(this);
		nextButton = (ImageView)findViewById(R.id.blenext);
		nextButton.setOnClickListener(this);
		preButton = (ImageView)findViewById(R.id.bleprev);
		preButton.setOnClickListener(this);
		toggleButton = (ImageView)findViewById(R.id.blepause);
		toggleButton.setOnClickListener(this);
		
		deviceName = (EditText)findViewById(R.id.devicename);
		deviceName.setOnFocusChangeListener(changeListener);
		
		devicePwd = (EditText)findViewById(R.id.devicepassword);
		devicePwd.setOnFocusChangeListener(changeListener);
		
		connectName = (TextView)findViewById(R.id.bletitle);
	}
	
	private OnTouchListener touchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			editor.putString("devicename",deviceName.getText().toString());
			editor.commit();
			
			editor.putString("devicepassword",devicePwd.getText().toString());
			editor.commit();
			
			contentView.setFocusable(true);
			contentView.setFocusableInTouchMode(true);
			deviceName.clearFocus();
			devicePwd.clearFocus();
			InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(deviceName.getWindowToken(), 0);
			return false;
		}
	}; 
	
	private OnFocusChangeListener changeListener = new OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.devicename:
				if(hasFocus == false){
//					blueToothBinder.bleChangeNameAndPwd(deviceName.getText().toString(),devicePwd.getText().toString());
					blueToothBinder.changeName(deviceName.getText().toString());
				}
				break;
			case R.id.devicepassword:
				if(hasFocus == false){
					blueToothBinder.bleChangeNameAndPwd(deviceName.getText().toString(),devicePwd.getText().toString());
				}
				break;
			default:
				break;
			}

		}
	};

	private ServiceConnection connection = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// TODO Auto-generated method stub
			blueToothBinder = (BlueToothBinder)service;
			blueToothBinder.getDeviceName();//获取蓝牙名字
			blueToothBinder.setBleActRefresher(bleRefresher);
			refreshSwitch();
			connectName.setText(blePref.getString("connecteddevice","未知设备"));
		}
	};
	
	/**
	 * 刷新SwitchIcon
	 */
	private void refreshSwitch(){
		if(blePref.getBoolean("status",false) == true){
			switchButtonFlag = true;
			switchButton.setImageResource(R.drawable.right);
			switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_green));
		}else{
			switchButtonFlag = false;
			switchButton.setImageResource(R.drawable.error);
			switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_red));
		}
	}
	
	private Refresher bleRefresher = new Refresher() {
		@Override
		public void refreshSwitchButtonIcon() {
			// TODO Auto-generated method stub
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					refreshSwitch();
				}
			});
		}
		
		@Override
		public void refreshDeviceName() {
			// TODO Auto-generated method stub
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					LogUtil.e(BlueTeethService.TAG,"refreshDeviceName(),"+blePref.getString("devicename","188-BT")
							+LogUtil.getLineInfo());
					deviceName.setText(blePref.getString("devicename","188-BT"));
				}
			});
		}
		
		@Override
		public void refreshConnectedDevice() {
			// TODO Auto-generated method stub
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					connectName.setText(blePref.getString("connecteddevice","未知设备"));
				}
			});
		}

		@Override
		public void setTitle() {
			// TODO Auto-generated method stub
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					connectName.setText("蓝牙推送");
				}
			});
		}
	};
	
	@Override
	protected void onRestart() {
		super.onRestart();
		LogUtil.e(TAG, "onRestart()");
		blueToothBinder.getDeviceName();//刷新蓝牙名
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtil.e(TAG, "onResume()");
		refreshSwitch();
		//停止多媒体播放
		sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
		//停止酷我音乐
		KwSdk.getInstance().exit();
		LineInControler.getInstance().switchBle();
		if(blePref.getBoolean("status",false) == true){
			switchButtonFlag = true;
			switchButton.setImageResource(R.drawable.right);
			switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_green));
		}else{
			switchButtonFlag = false;
			switchButton.setImageResource(R.drawable.error);
			switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_red));
		}
		deviceName.setText(blePref.getString("devicename","188-BT"));
		devicePwd.setText(blePref.getString("devicepassword",""));
		connectName.setText(blePref.getString("connecteddevice","未知设备"));
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		LogUtil.e("fuuck","蓝牙被关闭"+LogUtil.getLineInfo());
		sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
	}
	
	class BlueToothReceiver extends BroadcastReceiver{
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if("android.intent.action.REFRESH_UI".equals(action)){
				if(blePref.getBoolean("status",false) == true){
					switchButtonFlag = true;
					switchButton.setImageResource(R.drawable.right);
					switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_green));
				}else{
					switchButtonFlag = false;
					switchButton.setImageResource(R.drawable.error);
					switchButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_circle_red));
				}
				deviceName.setText(blePref.getString("devicename","188-BT"));
				connectName.setText(blePref.getString("connecteddevice","未知设备"));
			}else if("android.bluetooth.action.FINISH".equals(action)){
				LogUtil.e(TAG,"关闭蓝牙"+LogUtil.getLineInfo());
				finish();
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e(TAG,"蓝牙onDestroy()");
//		LineInControler.getInstance().stopLineIn();
		unregisterReceiver(blueToothReceiver);
		unbindService(connection);//解除绑定
	}
	
	public interface Refresher{
		void refreshDeviceName();
		void refreshConnectedDevice();
		void setTitle();
//		void refreshToggleButtonIcon();
		void refreshSwitchButtonIcon();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v.getId() == R.id.blenext){
			LogUtil.e(TAG,"蓝牙下一首被点击"+LogUtil.getLineInfo());
			blueToothBinder.bleNext();
		}else if(v.getId() == R.id.bleprev){
			LogUtil.e(TAG,"蓝牙上一首被点击"+LogUtil.getLineInfo());
			blueToothBinder.blePre();
		}else if(v.getId() == R.id.blepause){
			LogUtil.e(TAG,"蓝牙toggle被点击"+LogUtil.getLineInfo());
			blueToothBinder.setToggle();
			if(toggleButtonFlag == false){
				toggleButtonFlag = true;
				toggleButton.setBackgroundResource(R.drawable.rz_play_icon_blue_dark);
			}else{
				toggleButtonFlag = false;
				toggleButton.setBackgroundResource(R.drawable.rz_pause_icon_blue_dark);
			}
		}else if(v.getId() == R.id.bluebutton){
			LogUtil.e(TAG,"蓝牙推送被点击"+LogUtil.getLineInfo());
		}else if(v.getId() == R.id.edit_icon){
			deviceName.requestFocus();
		}else if(v.getId() == R.id.pwd_edit_icon){
			if(pwdEditIcon.getTag().equals("off")){
				//密码可见
				pwdEditIcon.setTag("on");
				pwdEditIcon.setImageResource(R.drawable.icon_blepwd_visble_write);
				devicePwd.setInputType(InputType.TYPE_CLASS_NUMBER);
			}else if(pwdEditIcon.getTag().equals("on")){
				//密码不可见
				pwdEditIcon.setTag("off");
				pwdEditIcon.setImageResource(R.drawable.icon_blepwd_invisible_write);
				devicePwd.setInputType(InputType.TYPE_CLASS_NUMBER|InputType.TYPE_NUMBER_VARIATION_PASSWORD);
			}
		}
	}
}
