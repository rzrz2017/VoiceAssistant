package com.szhklt.www.voiceassistant.view;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.szhklt.www.voiceassistant.R;

public class DialogWIFI extends Activity implements OnClickListener{
	public static boolean dailogWIFIisTOP = false;
	TextView yes,no;
	private MyReceiver receiver = null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_ds);
		yes=(TextView)findViewById(R.id.okok);
		no=(TextView)findViewById(R.id.nono);
		yes.setOnClickListener(this);
		no.setOnClickListener(this);
		receiver = new MyReceiver();	
		IntentFilter filter = new IntentFilter();
		filter.addAction("com.szhklt.service.MainService");
		DialogWIFI.this.registerReceiver(receiver, filter);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		dailogWIFIisTOP = true;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.nono:
			dailogWIFIisTOP = false;
			finish();
			break;
		case R.id.okok:
			//已经离开launcher界面
			Intent intent1=new Intent();
			intent1.putExtra("count", "ISNOTLAUNCHER");
			intent1.setAction("com.szhklt.FloatWindow.FloatSmallView");
			sendBroadcast(intent1);

			dailogWIFIisTOP = false;
			Intent intent =  new Intent(Settings.ACTION_WIFI_SETTINGS);  
			startActivity(intent);
			break;
		default:
			break;
		}
	}
	public class MyReceiver extends BroadcastReceiver { 
		@Override
		public void onReceive(Context context, Intent intent) {
			Bundle bundle = intent.getExtras();
			String count = bundle.getString("count");
			if("internetok".equals(count)){
				finish();
			}
		}
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		dailogWIFIisTOP = false;
		finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();		  
		if(receiver!=null){
			unregisterReceiver(receiver);
		}
	}
}
