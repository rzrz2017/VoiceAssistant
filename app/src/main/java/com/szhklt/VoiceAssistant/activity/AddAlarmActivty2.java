package com.szhklt.VoiceAssistant.activity;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.RadioButton;

import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.MyRadioGroup;
import com.szhklt.VoiceAssistant.view.RoundButton;

public class AddAlarmActivty2 extends Activity implements OnClickListener{
	private static final String TAG = "AddAlarmActivty2";
	private EditText editHour;
	private EditText editMinute;
	private EditText editPrompt;
	private MyRadioGroup group;
	private RadioButton monday;
	private Boolean mondayState = false;
	private RadioButton tuesday;
	private Boolean tuesdayState = false;
	private RadioButton wednesday;
	private Boolean wednesdayState = false;
	private RadioButton thursday;
	private Boolean thursdayState = false;
	private RadioButton friday;
	private Boolean fridayState = false;
	private RadioButton staturday;
	private Boolean staturdayState = false;
	private RadioButton sunday;
	private Boolean sundayState = false;
	private Calendar calendar;
	private int hour;
	private int minute;
	
	private RoundButton save;
//	private MyTextUnderstander myTextUnderstander;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_addalarm2);
		
		calendar = Calendar.getInstance();
		hour = calendar.get(Calendar.HOUR_OF_DAY);
		minute = calendar.get(Calendar.MINUTE);

//		myTextUnderstander = new MyTextUnderstander();
		
		initView();
	}

	private void initView() {
		save = (RoundButton)findViewById(R.id.btn_save);
		save.setOnClickListener(this);
		group = (MyRadioGroup)findViewById(R.id.myradiagroip);
		monday = (RadioButton)findViewById(R.id.monday);
		tuesday = (RadioButton)findViewById(R.id.tuesday);
		wednesday = (RadioButton)findViewById(R.id.wednesday);
		thursday = (RadioButton)findViewById(R.id.thursday);
		friday = (RadioButton)findViewById(R.id.friday);
		staturday = (RadioButton)findViewById(R.id.staturday);
		sunday = (RadioButton)findViewById(R.id.sunday);
		
		monday.setOnClickListener(this);
		tuesday.setOnClickListener(this);
		wednesday.setOnClickListener(this);
		thursday.setOnClickListener(this);
		friday.setOnClickListener(this);
		staturday.setOnClickListener(this);
		sunday.setOnClickListener(this);
		
		editHour = (EditText)findViewById(R.id.edit_hour);
		editMinute = (EditText)findViewById(R.id.edit_minute);

		editHour.setText(String.valueOf(hour));
		editMinute.setText(String.valueOf(minute));

		editPrompt = (EditText)findViewById(R.id.edit_prompt);		
		
		editHour.setOnFocusChangeListener(changeListener);
		editMinute.setOnFocusChangeListener(changeListener);
		editPrompt.setOnFocusChangeListener(changeListener);
	}

	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		myTextUnderstander.destory();
	}

	private OnFocusChangeListener changeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.edit_hour:
				if(hasFocus){
					Intent intent = new Intent(AddAlarmActivty2.this,EditTextActivity.class);
					intent.putExtra("type", "hour");
					intent.putExtra("data",editHour.getText().toString());
					startActivityForResult(intent,1);
				}else{

				}
				break;
			case R.id.edit_minute:
				if(hasFocus){
					Intent intent = new Intent(AddAlarmActivty2.this,EditTextActivity.class);
					intent.putExtra("type", "minute");
					intent.putExtra("data",editMinute.getText().toString());
					startActivityForResult(intent,2);
				}else{

				}
				break;
			case R.id.edit_prompt:
				if(hasFocus){
					Intent intent = new Intent(AddAlarmActivty2.this,EditTextActivity.class);
					intent.putExtra("type", "prompt");
					intent.putExtra("data",editPrompt.getText().toString());
					startActivityForResult(intent,3);
				}else{

				}
			default:
				break;
			}
		}
	};


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		editHour.clearFocus();
		editMinute.clearFocus();
		editPrompt.clearFocus();
		
		switch (requestCode) {
		case 1:
			if(resultCode == RESULT_OK){
				String returnedData = data.getStringExtra("data");
				LogUtil.d("returnedData",returnedData);
				editHour.setText(returnedData);
			}
			break;
		case 2:
			if(resultCode == RESULT_OK){
				String returnedData = data.getStringExtra("data");
				LogUtil.d("returnedData",returnedData);
				editMinute.setText(returnedData);
			}
			break;
		case 3:
			if(resultCode == RESULT_OK){
				String returnedData = data.getStringExtra("data");
				LogUtil.d("returnedData",returnedData);
				editPrompt.setText(returnedData);
			}
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.monday:
			if(mondayState == true){
				mondayState = false;
				group.clearCheck();
			}else{
				mondayState = true;
				tuesdayState = false;
				wednesdayState = false;
				thursdayState = false;
				fridayState = false;
				staturdayState = false;
				sundayState = false;
			}
			break;
		case R.id.tuesday:
			if(tuesdayState == true){
				tuesdayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = true;
				wednesdayState = false;
				thursdayState = false;
				fridayState = false;
				staturdayState = false;
				sundayState = false;
			}
			break;
		case R.id.wednesday:
			if(wednesdayState == true){
				wednesdayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = false;
				wednesdayState = true;
				thursdayState = false;
				fridayState = false;
				staturdayState = false;
				sundayState = false;
			}
			break;
		case R.id.thursday:
			if(thursdayState == true){
				thursdayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = false;
				wednesdayState = false;
				thursdayState = true;
				fridayState = false;
				staturdayState = false;
				sundayState = false;
			}
			break;
		case R.id.friday:
			if(fridayState == true){
				fridayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = false;
				wednesdayState = false;
				thursdayState = false;
				fridayState = true;
				staturdayState = false;
				sundayState = false;
			}
			break;
		case R.id.staturday:
			if(staturdayState == true){
				staturdayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = false;
				wednesdayState = false;
				thursdayState = false;
				fridayState = false;
				staturdayState = true;
				sundayState = false;
			}
			break;
		case R.id.sunday:
			if(sundayState == true){
				sundayState = false;
				group.clearCheck();
			}else{
				mondayState = false;
				tuesdayState = false;
				wednesdayState = false;
				thursdayState = false;
				fridayState = false;
				staturdayState = false;
				sundayState = true;
			}
			break;
		case R.id.btn_save:
			String understandText;
			understandText = buildUnderstandText(group.getCheckedRadioButtonId(),editPrompt.getText().toString());
			LogUtil.e(TAG,"understandText:"+understandText);
			
			MyAIUI.getInstance().understandText(understandText);
//			myTextUnderstander.understandText(understandText, textUnderstanderListener);
			finish();
			break;
		default:
			break;
		}
	}
	
	/**
	 * 构建理解语句
	 */
	private String buildUnderstandText(int weekId,String prompt){
		String returndata = new String();
		switch (weekId) {
		case R.id.monday:
			returndata = checkPromptAndReturn(monday);
			break;
		case R.id.tuesday:
			returndata = checkPromptAndReturn(tuesday);
			break;
		case R.id.wednesday:
			returndata = checkPromptAndReturn(wednesday);
			break;
		case R.id.thursday:
			returndata = checkPromptAndReturn(thursday);
			break;
		case R.id.friday:
			returndata = checkPromptAndReturn(friday);
			break;
		case R.id.staturday:
			returndata = checkPromptAndReturn(staturday);
			break;
		case R.id.sunday:
			returndata = checkPromptAndReturn(sunday);
			break;
		case -1:
			returndata = checkPromptAndReturn();
			break;
		default:
			LogUtil.e(TAG,"buildUnderstandText failed!"+LogUtil.getLineInfo());
			break;
		}
		LogUtil.e("save",returndata);
		return returndata;
	}
	
	/**
	 * 检查提示是否为空，并返回语句
	 * @return
	 */
	private String checkPromptAndReturn(RadioButton btn){
		if(editPrompt.getText().toString().isEmpty()){
			return "设置"+editHour.getText().toString()+"点"+editMinute.getText().toString()+"分"+btn.getText()+"的闹钟";
		}else{
			return editHour.getText().toString()+"点"+editMinute.getText().toString()+"分"+btn.getText()+"提醒我"+editPrompt.getText().toString();
		}
	}
	
	private String checkPromptAndReturn(){
		if(editPrompt.getText().toString().isEmpty()){
			return "设置每天"+editHour.getText().toString()+"点"+editMinute.getText().toString()+"分"+"的闹钟";
		}else{
			return "每天"+editHour.getText().toString()+"点"+editMinute.getText().toString()+"分"+"提醒我"+editPrompt.getText().toString();
		}
	}
}
