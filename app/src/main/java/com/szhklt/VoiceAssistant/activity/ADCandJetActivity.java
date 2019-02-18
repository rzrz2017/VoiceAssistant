package com.szhklt.VoiceAssistant.activity;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.multithreadeddownloader.DownLoadUtils;
import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class ADCandJetActivity extends Activity{
	TextView title;
	TextView adc;
	TextView jet;
	TextView curver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_adcandjet);
		initView();initDatas();
	
	}
	
	void initView(){
		title = (TextView) findViewById(R.id.title);
		adc = (TextView)findViewById(R.id.adctype);
		jet = (TextView)findViewById(R.id.jettype);
		curver = (TextView)findViewById(R.id.curver);
	}
	
	void initDatas(){
		title.setText("当前版本已是最新,无需更新!");
		adc.setText("ADC类型:"+MainApplication.ADCTPYE);
		if(MainApplication.JETNAME.contains("dingdong")){
			jet.setText("唤醒词语:"+"叮咚叮咚");
		}else if(MainApplication.JETNAME.contains("xiaoba")){
			jet.setText("唤醒词语:"+"小巴小巴");
		}
		curver.setText("当前版本:"+ DownLoadUtils.getAPPVersionCode(getBaseContext()));
	}
	
}
