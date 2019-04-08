package com.szhklt.VoiceAssistant.activity;

import java.util.Calendar;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.szhklt.VoiceAssistant.impl.OnWheelChangedListener;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.adapter.ArrayWheelAdapter;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.SwitchView;
import com.szhklt.VoiceAssistant.view.WheelView;
import com.szhklt.VoiceAssistant.view.SwitchView.OnStateChangedListener;

public class RebootSetActivity extends Activity implements OnClickListener{
	private WheelView hourWV = null;
	private WheelView minuteWV = null;
	private SwitchView switchView;
	private TextView time_TV,suretime_TV = null;
	private String[] hourArrayString = null;
	private String[] minuteArrayString = null;
	private Calendar c = null;
	private LinearLayout settimeLayout;
	private ImageView gotosettime_IV,back_IV;
	private Button sure_bt;
	private String reboottime="";

	private TextView am_pm,defauttime;
	private SharedPreferences.Editor editor;
	private LinearLayout secondLine;
	private LinearLayout firstLine;
	private String boottime;
	private String time;
	private int yorkcolor = Color.argb(170,232, 212, 99);//不透明黄色
	private int yorkcolor1=Color.argb(51,232, 212, 99);//透明黄色
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rebootset);
		//      MainService.isLauncherAcyivity=false;
		editor= getSharedPreferences("rebootdate",Activity.MODE_PRIVATE).edit();
		boottime = getRebootTime();

		initView();
		if(boottime==null){
			editor.putString("reboottime", "04时00分");
			editor.commit();
		}else{
			String[] SC=boottime.split("时");
			String time = null;
			if(Integer.parseInt(SC[0])>=0&&Integer.parseInt(SC[0])<6){
				time="凌晨 "+boottime;
			}else if(Integer.parseInt(SC[0])>=6&&Integer.parseInt(SC[0])<12){
				time="上午 "+boottime;
			}else if(Integer.parseInt(SC[0])==12){
				time="中午 "+boottime;
			}else if(Integer.parseInt(SC[0])>12&&Integer.parseInt(SC[0])<=18){
				time="下午 "+boottime;		
			}else if(Integer.parseInt(SC[0])>18){
				time="晚上 "+boottime;
			}
			if(time!=null){
				suretime_TV.setText(time);
			}
		}
	}
	
	private void initView(){
		secondLine = (LinearLayout)findViewById(R.id.secondLine);
		secondLine.setOnClickListener(this);
		firstLine=(LinearLayout)findViewById(R.id.firstLine);
		firstLine.setOnClickListener(this);

		switchView = (SwitchView)findViewById(R.id.v_switch);
		switchView.setOnStateChangedListener(stateChangedListener);
		
		hourWV = (WheelView) findViewById(R.id.time_hour);
		minuteWV = (WheelView) findViewById(R.id.time_minute);
		time_TV = (TextView) findViewById(R.id.time_chose);
		suretime_TV=(TextView)findViewById(R.id.suretime);
		defauttime=(TextView)findViewById(R.id.defauttime_tv);
		gotosettime_IV=(ImageView)findViewById(R.id.gotosettime);
		back_IV=(ImageView)findViewById(R.id.back);
		sure_bt=(Button)findViewById(R.id.sure);
		am_pm=(TextView)findViewById(R.id.am_pm);
		sure_bt.setOnClickListener(this);
		gotosettime_IV.setOnClickListener(this);
		back_IV.setOnClickListener(this);
		settimeLayout=(LinearLayout)findViewById(R.id.timeset);
		settimeLayout.setVisibility(View.GONE);
		hourWV.setVisibleItems(5);
		minuteWV.setVisibleItems(5);
		hourWV.setCyclic(true);
		minuteWV.setCyclic(true);
		hourArrayString = getHMArray(24);
		minuteArrayString = getHMArray(60);	
		// 获取当前系统时间
		c = Calendar.getInstance();
		setData();
	}
	
	private OnStateChangedListener stateChangedListener = new OnStateChangedListener() {
		
		@Override
		public void toggleToOn(SwitchView view) {
			// TODO Auto-generated method stub
			switchView.toggleSwitch(true);
			firstLine.setClickable(true);
			secondLine.setClickable(true);
			
			if(time!=null){
				editor.putString("reboottime", time);
				LogUtil.e("reboot","time:"+time);
				editor.commit();
			}

			LogUtil.e("reboot","发送REBOOT广播"+LogUtil.getLineInfo());
			Intent intent=new Intent();
			intent.putExtra("count", "REBOOT");
			intent.setAction("com.szhklt.FloatWindow.FloatSmallView");
			sendBroadcast(intent);
			Toast.makeText(getBaseContext(), "启用定时清理", Toast.LENGTH_SHORT).show();
		}
		
		@Override
		public void toggleToOff(SwitchView view) {
			// TODO Auto-generated method stub
			switchView.toggleSwitch(false);
			Intent intent=new Intent();
			intent.putExtra("count", "cancleREBOOT");
			intent.setAction("com.szhklt.FloatWindow.FloatSmallView");
			sendBroadcast(intent);
			
			firstLine.setClickable(false);
			secondLine.setClickable(false);
			
			if(settimeLayout != null){
				settimeLayout.setVisibility(View.INVISIBLE);
			}
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.gotosettime:
			settimeLayout.setVisibility(View.VISIBLE);
			break;
		case R.id.sure:
			settimeLayout.setVisibility(View.GONE);
			suretime_TV.setVisibility(View.VISIBLE);
			suretime_TV.setText(reboottime);

			if(time!=null){
				editor.putString("reboottime", time);
				LogUtil.e("reboot","time:"+time);
				editor.commit();
			}

			LogUtil.e("reboot","发送REBOOT广播"+LogUtil.getLineInfo());
			Intent intent=new Intent();
			intent.putExtra("count", "REBOOT");
			intent.setAction("com.szhklt.FloatWindow.FloatSmallView");
			sendBroadcast(intent);
			Toast.makeText(this, "语音助手：设定成功", Toast.LENGTH_SHORT).show();
			break;
		case R.id.back:
			finish();
			break;
		case R.id.suretime:
			settimeLayout.setVisibility(View.VISIBLE);
			suretime_TV.setText("");
			break;
		case R.id.firstLine:
			settimeLayout.setVisibility(View.GONE);
			editor.putString("reboottime", "04时00分");
			editor.commit();
			defauttime.setTextColor(yorkcolor);
			suretime_TV.setTextColor(yorkcolor1);
			LogUtil.e("reboot","发送REBOOT广播"+LogUtil.getLineInfo());
			Intent setRebootIntent=new Intent();
			setRebootIntent.putExtra("count", "REBOOT");
			setRebootIntent.setAction("com.szhklt.FloatWindow.FloatSmallView");
			sendBroadcast(setRebootIntent);
			Toast.makeText(this, "语音助手：已将清理时间设为默认时间", Toast.LENGTH_SHORT).show();
			break;
		case R.id.secondLine:
			defauttime.setTextColor(yorkcolor1);
			suretime_TV.setTextColor(yorkcolor);
			settimeLayout.setVisibility(View.VISIBLE);
			Toast.makeText(this, "语音助手：请设置定时清理时间", Toast.LENGTH_SHORT).show();
			break;
		default:
			break;
		}
	}

	/**
	 * 给滚轮提供数据
	 */
	private void setData() {
		// 给滚轮提供数据
		hourWV.setAdapter(new ArrayWheelAdapter<String>(hourArrayString));
		minuteWV.setAdapter(new ArrayWheelAdapter<String>(minuteArrayString));
		hourWV.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				showDate();
			}
		});

		// 当分钟变化时，显示的时间
		minuteWV.addChangingListener(new OnWheelChangedListener() {
			@Override
			public void onChanged(WheelView wheel, int oldValue, int newValue) {
				showDate();
			}
		});
		setOriTime();
	}
	// 设定初始时间
	void setOriTime() {
		hourWV.setCurrentItem(getNumData(c.get(Calendar.HOUR_OF_DAY) + "",
				hourArrayString));
		minuteWV.setCurrentItem(getNumData(c.get(Calendar.MINUTE) + "",
				minuteArrayString));
	}

	// 根据数字生成一个字符串数组
	public String[] getHMArray(int day) {
		String[] dayArr = new String[day];
		for (int i = 0; i < day; i++) {
			dayArr[i] = i + "";
		}
		return dayArr;
	}
	// 在数组Array[]中找出字符串s的位置
	int getNumData(String s, String[] Array) {
		int num = 0;
		for (int i = 0; i < Array.length; i++) {
			if (s.equals(Array[i])) {
				num = i;
				break;
			}
		}
		return num;
	}

	// 显示时间
	void showDate() {
		createDate(
				hourArrayString[hourWV.getCurrentItem()],
				minuteArrayString[minuteWV.getCurrentItem()]);
	}
	
	// 生成时间
	void createDate(String hour,String minute) {
		String dateStr="";
		if(Integer.parseInt(hour)<10){
			hour="0"+hour;
		}
		if(Integer.parseInt(minute)<10){
			minute="0"+minute;
		}
		if(Integer.parseInt(hour)>=0&&Integer.parseInt(hour)<6){
			am_pm.setText("凌晨");
			dateStr ="凌晨 "+hour + "时"+ minute + "分";
		}else if(Integer.parseInt(hour)>=6&&Integer.parseInt(hour)<12){
			am_pm.setText("上午");
			dateStr ="上午 "+hour + "时"+ minute + "分";
		}else if(Integer.parseInt(hour)==12){
			am_pm.setText("中午");
			dateStr ="中午 "+hour + "时"+ minute + "分";
		}else if(Integer.parseInt(hour)>12&&Integer.parseInt(hour)<=18){
			am_pm.setText("下午");
			dateStr ="下午 "+hour + "时"+ minute + "分";
		}else if(Integer.parseInt(hour)>18){
			am_pm.setText("晚上");
			dateStr ="晚上"+hour + "时"+ minute + "分";
		}
		time=hour + "时"+ minute + "分";
		time_TV.setText(dateStr);
		reboottime=dateStr;
	}
	
	private String getRebootTime(){
		SharedPreferences pref = getSharedPreferences("rebootdate", MODE_PRIVATE);
		String string = pref.getString("reboottime", "04时00分");
		return string;
	}
}
