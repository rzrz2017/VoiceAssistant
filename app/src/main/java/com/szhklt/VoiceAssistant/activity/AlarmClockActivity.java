package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.szhklt.VoiceAssistant.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.component.MySynthesizer;
import com.szhklt.VoiceAssistant.view.ClockView;

public class AlarmClockActivity extends Activity{

	public int mwidth;
	public int mheight;
	private TextView main_text,tv_time;
	private ClockView mClockView;
	private RelativeLayout mRelativeLayout;
	private MyReceiver receiver=null;//广播接收者s
	private MySynthesizer mTts= MySynthesizer.getInstance(getBaseContext());
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_alarmclock);	
		mClockView = (ClockView)findViewById(R.id.clockView);
		main_text=(TextView)findViewById(R.id.main_textclock);
		tv_time=(TextView)findViewById(R.id.time);
		mRelativeLayout=(RelativeLayout)findViewById(R.id.all);
		Bundle bundle = this.getIntent().getExtras();  
		if(bundle!=null){
			String content = bundle.getString("content");  
			String time =bundle.getString("time");
			tv_time.setText(time);
			if(content==null){
				main_text.setText("时间到了");
				mTts.doSomethingAfterAlarm(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						finish();
					}
				}, "你好,时间到了!你好,时间到了!",null);
			}else{
				main_text.setText(content);
				int x = 1+(int)(Math.random()*2);
				String tip1;
				String tip2;
				if(x == 1){
					tip1 = "请记得";
					tip2 = "哟";
				}else{
					tip1 = "该";
					tip2 = "了";
				}
				mTts.doSomethingAfterAlarm(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						finish();
					}
				},"你好,时间到了!"+tip1+content+tip2,null);
			}
		}
		mRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				finish();
			}
		});
		receiver= new MyReceiver();//注册广播接收者
		IntentFilter filter1=new IntentFilter();
		filter1.addAction("com.szhklt.service.MainService");
		registerReceiver(receiver,filter1);
	}
	public class MyReceiver extends BroadcastReceiver { 
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle=intent.getExtras();
			String count=bundle.getString("count");
			if(count.equals("wakeup")){
				finish();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		mClockView.start();
	}
	@Override
	protected void onDestroy() {
		mClockView.stop();
		super.onDestroy();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
}
