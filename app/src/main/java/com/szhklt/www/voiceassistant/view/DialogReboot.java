package com.szhklt.www.voiceassistant.view;

import java.util.Timer;
import java.util.TimerTask;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import com.szhklt.www.voiceassistant.R;

public class DialogReboot extends Activity implements OnClickListener {
	private Timer mTimer = new Timer(); // 计时器
	private TextView timeview;
	private Button yes,no;
	private int time=20;
	private String ACTION_GLOBAL_REBOOT_SYSTEM = "android.action.GLOBAL_REBOOT_SYSTEM";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_reboot);
		timeview=(TextView) findViewById(R.id.textView3);
		yes=(Button)findViewById(R.id.sure);
		no=(Button)findViewById(R.id.no);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		mTimer.schedule(timerTask, 0, 1000);
	}
	TimerTask timerTask = new TimerTask() {

		@Override
		public void run() {
			if(time>0){				
				handler.sendEmptyMessage(0);
			}else{
				Intent intent = new Intent();  
		        intent.setAction(ACTION_GLOBAL_REBOOT_SYSTEM);   
		        sendBroadcast(intent); 
			}
		}		
	};
	Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			time--;
			timeview.setText(time+"");
		};
	};
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.no:
			timerTask.cancel();
			finish();
			break;
		case R.id.sure:
			Intent intent = new Intent();  
	        intent.setAction(ACTION_GLOBAL_REBOOT_SYSTEM);   
	        sendBroadcast(intent);
	        timerTask.cancel();
	        finish();
			break;
		default:
			break;
		}
	}
	 @Override
		protected void onDestroy() {
		    super.onDestroy();		  
	 }
}