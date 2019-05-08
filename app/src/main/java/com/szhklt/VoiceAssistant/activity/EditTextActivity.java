package com.szhklt.VoiceAssistant.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class EditTextActivity extends Activity implements OnClickListener{
	private String TAG = "EditTextActivity";
	
	private EditText editText1;
	private EditText editText2;
	private EditText editText3;
	private Button button1;
	private Button button2;
	private Button button3;
	
	private LinearLayout field1;
	private LinearLayout field2;
	private LinearLayout field3;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edittext);
		
		field1 = (LinearLayout)findViewById(R.id.field1);
		field2 = (LinearLayout)findViewById(R.id.field2);
		field3 = (LinearLayout)findViewById(R.id.field3);
		
		editText1 = (EditText)findViewById(R.id.edit_text1);
		editText2 = (EditText)findViewById(R.id.edit_text2);
		editText3 = (EditText)findViewById(R.id.edit_text3);
		
		button1= (Button)findViewById(R.id.button1);
		button2= (Button)findViewById(R.id.button2);
		button3= (Button)findViewById(R.id.button3);
		
		button1.setOnClickListener(this);
		button2.setOnClickListener(this);
		button3.setOnClickListener(this);
		
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		Intent intent = getIntent();
		String types = intent.getStringExtra("type");
		LogUtil.e(TAG,"types:"+types+ LogUtil.getLineInfo());
		if(types.equals("hour")){
			editText1.setText(intent.getStringExtra("data"));
			field2.setVisibility(View.GONE);
			field3.setVisibility(View.GONE);
		}else if(types.equals("minute")){
			editText2.setText(intent.getStringExtra("data"));
			field1.setVisibility(View.GONE);
			field3.setVisibility(View.GONE);
		}else if(types.equals("prompt")){
			editText3.setText(intent.getStringExtra("data"));
			field1.setVisibility(View.GONE);
			field2.setVisibility(View.GONE);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.button1){
			Intent intent = new Intent();
			intent.putExtra("data",editText1.getText().toString());
			setResult(RESULT_OK, intent);
			editText1.clearFocus();
			
		}else
		if(id == R.id.button2){
			Intent intent = new Intent();
			intent.putExtra("data",editText2.getText().toString());
			setResult(RESULT_OK, intent);
			editText2.clearFocus();
		}else
		if(id == R.id.button3){
			Intent intent = new Intent();
			intent.putExtra("data",editText3.getText().toString());
			setResult(RESULT_OK, intent);
			editText3.clearFocus();
		}
		
		finish();
	}
}
