package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.TimeUtil;
import com.szhklt.VoiceAssistant.beam.WeatherData;
import com.szhklt.VoiceAssistant.component.MySynthesizer;
import com.szhklt.VoiceAssistant.db.WeatherDBHandler;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.timeTask.SleepTimeout;
import com.szhklt.VoiceAssistant.timeTask.Weather;
import com.szhklt.VoiceAssistant.util.JsonParse;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;

public class SleepActivity extends Activity implements OnClickListener {
	private String TAG="SleepActivity";
	private TextClock mTextClock;
//	private AnalogClockView analogClockView;
	private TimeUtil mtimeutil;
	private ImageView weathericon;
	private TextView tv_location,CurrentDate, wind,Currenttemp,CurrentWeek,wind_direction;
	public IntentFilter intentfilter;
	public LauncherReceiver launcherreceiver;
	private WeatherDBHandler mweatherDBHandler;
	private WeatherData todayWeatherData;
	private RelativeLayout all;
	private String UPDATA_RETURNWEATHERINFO="android.action.HKLT_UPDATA_RETURNWEATHERINFO";
	public static final String FINISH = "android.SleepActivity.intent.FINISH";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_sleep);
		LogUtil.e(TAG,"onCreate()");
		
		initview();
		mtimeutil = new TimeUtil(this);
		mweatherDBHandler = new WeatherDBHandler(this);
		intentfilter = new IntentFilter();
		intentfilter.addAction("android.climate.msg.CLIMATE_UPDARE");
		intentfilter.addAction(UPDATA_RETURNWEATHERINFO);
		intentfilter.addAction("com.szhklt.service.MainService");
		intentfilter.addAction(FINISH);
		launcherreceiver = new LauncherReceiver();
		registerReceiver(launcherreceiver, intentfilter);
		MySynthesizer.getInstance(getBaseContext()).destoryTts();//注销tts
		FloatWindowManager.getInstance().removeAll();
		FloatWindowManager.getInstance().removeFloatButton(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtil.e(TAG,"onResume()");
//		analogClockView.start();
		FloatWindowManager.getInstance().removeAll();
		todayWeatherData = mweatherDBHandler.queryTheWeatherFristLine();
		if(todayWeatherData != null){
			LogUtil.e("todayWeatherData",todayWeatherData.toString());
		}
		
		if(todayWeatherData == null){
			SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
			String location = pref.getString("location","深圳");
			LogUtil.e(TAG,"==============location:"+location);
			//默认查询天气
			new Weather().queryWeather();
			UIcontrol();
		}else{
			UIcontrol();
		}
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
//		analogClockView.stop();
		finish();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e(TAG,"onDestroy()");
		unregisterReceiver(launcherreceiver);
		LogUtil.e(TAG,"createFloatButton()"+LogUtil.getLineInfo());
		SleepTimeout.getInstance().restart();//activity被销毁时开启定时任务
	}

	private void initview(){
		mTextClock=(TextClock)findViewById(R.id.deskClock);
//		analogClockView = (AnalogClockView)findViewById(R.id.relativeLayout1);
		all=(RelativeLayout)findViewById(R.id.sleepall);
		weathericon=(ImageView)findViewById(R.id.sleep_weather_image);
		tv_location=(TextView)findViewById(R.id.sleep_location);
		CurrentDate = (TextView)findViewById(R.id.sleep_data);
		wind=(TextView)findViewById(R.id.sleep_wind);
		CurrentWeek = (TextView)findViewById(R.id.sleep_week);
		Currenttemp = (TextView)findViewById(R.id.sleep_temperature);
		wind_direction = (TextView)findViewById(R.id.sleep_weather_text);
		all.setOnClickListener(this);
		mTextClock.setFormat24Hour("hh:mm");
//		analogClockView.toggle();
	}

	private void UIcontrol() {
		SharedPreferences pref = getSharedPreferences("location", MODE_PRIVATE);
		tv_location.setText(pref.getString("location","深圳"));	
		if(mtimeutil!=null){
			CurrentDate.setText(mtimeutil.getTodaysDate());
			CurrentWeek.setText(mtimeutil.getWeek());	
		}
		
		if(todayWeatherData!=null){
			Currenttemp.setText(todayWeatherData.gettempRange().toString());
			wind_direction.setText(todayWeatherData.getweather());
			wind.setText(todayWeatherData.getwind().toString());
			if(todayWeatherData.getweather().length()>4){
				wind_direction.setTextSize(20);
			}
			changeWeatherImage();
		}
	}

	public class LauncherReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			LogUtil.e(TAG, "onReceive()"+LogUtil.getLineInfo());
			if(intent.getAction().equals(FINISH)){
				finish();
				return;
			}
			List<WeatherData> weatherdataarray = new ArrayList<>();
			if(intent.getAction().equals(UPDATA_RETURNWEATHERINFO)){//刷新天气数据
				String json = intent.getStringExtra("weatherjson");
				LogUtil.e(TAG,"weatherjson:"+json+LogUtil.getLineInfo());
				JsonParse jp = new JsonParse(getBaseContext());
				weatherdataarray = jp.WeatherXUnderstander(json);		
				mweatherDBHandler.deleteAllWeatherMsg();
				mweatherDBHandler.insertAWeekOfWeatherMsg(weatherdataarray);
				todayWeatherData = mweatherDBHandler.queryTheWeatherFristLine();
				if(todayWeatherData != null){
					LogUtil.e("todayWeatherData",todayWeatherData.toString());	
				}
				UIcontrol();
			}
			Bundle bundle=intent.getExtras();
			String count=bundle.getString("count");
			if(count.indexOf("wakeup")!=-1){
				finish();
			}else if(count.indexOf("back")!=-1){
				finish();
			}
		}
	}
	
	private void changeWeatherImage(){
		String weather=todayWeatherData.getweather();
		if(weather.contains("小雨")){
			weathericon.setImageResource(R.drawable.iconwslightrain);
		}else if(weather.contains("阴转小雨")){
			weathericon.setImageResource(R.drawable.iconwslightrain);
		}else if(weather.contains("阴")){
			weathericon.setImageResource(R.drawable.iconwcloudy);
		}else if(weather.contains("阴转多云")){
			weathericon.setImageResource(R.drawable.iconwcleartoovercast);
		}else if(weather.contains("多云")){
			weathericon.setImageResource(R.drawable.iconwcleartoovercast);
		}else if(weather.contains("雾")){
			weathericon.setImageResource(R.drawable.iconwfog);
		}else if(weather.contains("晴")){
			weathericon.setImageResource(R.drawable.iconwsunshine);
		}else if(weather.contains("暴雪")){
			weathericon.setImageResource(R.drawable.iconwslightrain);
		}else if(weather.contains("暴雨")){
			weathericon.setImageResource(R.drawable.iconwrainstorm);
		}else if(weather.contains("暴雨转大暴雨")){
			weathericon.setImageResource(R.drawable.iconwheavyturnrainstorm);
		}else if(weather.contains("大暴雨")){
			weathericon.setImageResource(R.drawable.iconwheavyrainstorm);
		}else if(weather.contains("大雪")){
			weathericon.setImageResource(R.drawable.iconwheavysnow);
		}else if(weather.contains("大雪转暴雪")){
			weathericon.setImageResource(R.drawable.iconwheavysnowturnsnowstorm);
		}else if(weather.contains("大雨")){
			weathericon.setImageResource(R.drawable.iconwheavyrain);
		}else if(weather.contains("大雨转暴雨")){
			weathericon.setImageResource(R.drawable.iconheavyrainturnrainstorm);
		}else if(weather.contains("冻雨")){
			weathericon.setImageResource(R.drawable.iconwicerain);
		}else if(weather.contains("浮尘")){
			weathericon.setImageResource(R.drawable.iconwfloatingdust);
		}else if(weather.contains("雷阵雨")){
			weathericon.setImageResource(R.drawable.iconwthundershower);
		}else if(weather.contains("雷阵雨伴有冰雹")){
			weathericon.setImageResource(R.drawable.iconwthunderaccompanyhail);
		}else if(weather.contains("霾")){
			weathericon.setImageResource(R.drawable.iconwhaze);
		}else if(weather.contains("沙尘暴")){
			weathericon.setImageResource(R.drawable.iconwblowingsand);
		}else if(weather.contains("特大暴雨")){
			weathericon.setImageResource(R.drawable.iconwextraordinaryrainstorm);
		}else if(weather.contains("特强沙尘暴")){
			weathericon.setImageResource(R.drawable.iconwseveresandstorm);
		}else if(weather.contains("小雪")){
			weathericon.setImageResource(R.drawable.iconwslightsnow);
		}else if(weather.contains("小雪转中雪")){
			weathericon.setImageResource(R.drawable.iconwslightsnowturnmediumsnow);
		}else if(weather.contains("小雨转中雨")){
			weathericon.setImageResource(R.drawable.iconwslightrainturnmediumrain);
		}else if(weather.contains("阴天")){
			weathericon.setImageResource(R.drawable.iconwcloudy);
		}else if(weather.contains("雨夹雪")){
			weathericon.setImageResource(R.drawable.iconwsleet);
		}else if(weather.contains("阵雪")){
			weathericon.setImageResource(R.drawable.iconwshowerysnow);
		}else if(weather.contains("阵雨")){
			weathericon.setImageResource(R.drawable.iconwshoweryrain);
		}else if(weather.contains("中雪")){
			weathericon.setImageResource(R.drawable.iconwmoderatesnow);
		}else if(weather.contains("中雪转大雪")){
			weathericon.setImageResource(R.drawable.iconnediumsnowturnheavysnow);
		}else if(weather.contains("中雨")){
			weathericon.setImageResource(R.drawable.iconwmoderaterain);
		}else if(weather.contains("中雨转大雨")){
			weathericon.setImageResource(R.drawable.iconwmediumturnheavyrain);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sleepall://点击整个布局界面
			finish();
		default:
			break;
		}
	}
}
