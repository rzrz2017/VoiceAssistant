package com.szhklt.VoiceAssistant.skill;

import java.util.ArrayList;
import android.content.ContentResolver;
import android.net.Uri;
import android.provider.Settings;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class LightControlSkill extends Skill{

	private String lightFlag;

	private ContentResolver mResolver = MainApplication.getContext().getContentResolver();

	public LightControlSkill(intent intent) {
		mintent = intent;//构造注入
	}

	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		ArrayList<Slot> slots = new ArrayList<>();
		slots = mintent.getSemantic().get(0).getSlots();
		for(Slot slot:slots){
			if("light2".equals(slot.getName())){
				lightFlag = slot.getNormValue();
				continue;
			}
			if("light".equals(slot.getName())){
				lightFlag = slot.getNormValue();
				continue;
			}
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		LogUtil.e("light","亮度控制标志lightFlag:"+lightFlag);
		mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
				// TODO Auto-generated method stub
				recoveryPlayerState();
			}
		},"好的",  question);
//		Brightness_Control(lightFlag);
		Brightness_Control2(lightFlag);
	}

	/**
	 * 保存亮度
	 * @param brightness
	 */
	public void saveBrightness(int brightness) {
		if(brightness >= 255){
			brightness = 255;
		}else if(brightness <= 0){
			brightness = 0;
		}
		Uri uri = android.provider.Settings.System
				.getUriFor("screen_brightness");
		android.provider.Settings.System.putInt(mResolver, "screen_brightness",
				brightness);
		mResolver.notifyChange(uri, null);
	}	

	/**
	 * 获取当前系统亮度
	 * @return
	 */
	private int getSystemBrightness() {
		int systemBrightness = 0;
		try {
			systemBrightness = Settings.System.getInt(mResolver, Settings.System.SCREEN_BRIGHTNESS);
		} catch (Settings.SettingNotFoundException e) {
			e.printStackTrace();
		}
		return systemBrightness;
	}	
	/**
	 * 新
	 * @param lightFlag
	 */
	public void Brightness_Control2(String lightFlag){
		if(lightFlag.contains("%")){
			lightFlag.replace("%","");
		}
		if(isNumeric(lightFlag)){
			int lightInt = Integer.valueOf(lightFlag);
			saveBrightness(lightInt*255/100);
		}else{
			int curBrightness = getSystemBrightness();
	        StringBuffer letter = new StringBuffer();
	        StringBuffer number = new StringBuffer();
	        
			char[] strArr = lightFlag.toCharArray();
			for(char string:strArr){//迭代
	            // 判断是否为字母
	            if ((string+"").matches("[a-z]") || (string+"").matches("[A-Z]")){
	                letter.append(string+"");
	            }
	            // 判断是否为数字
	            if ((string+"").matches("[0-9]")){
	                number.append(string+"");
	            }
			}
			LogUtil.e("light","数字:"+number.toString());
			LogUtil.e("light","字母:"+letter.toString());
			if(letter.toString().contains("H")){
				int setLight = curBrightness+(255/100*Integer.valueOf(number.toString()));
				LogUtil.e("light","setLight:"+setLight);
				if(setLight > 255){
					setLight = 255;
				}else if(setLight < 0){
					setLight = 0;
				}
				saveBrightness(setLight);
			}else if(letter.toString().contains("L")){
				int setLight = curBrightness-(255/100*Integer.valueOf(number.toString()));
				LogUtil.e("light","setLight:"+setLight);
				if(setLight > 255){
					setLight = 255;
				}else if(setLight < 0){
					setLight = 0;
				}
				saveBrightness(setLight);
			}
		}
	}

	public boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}
}
