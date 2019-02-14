package com.szhklt.www.voiceassistant.skill;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.media.AudioManager;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.beam.intent;
import com.szhklt.www.voiceassistant.util.LogUtil;
import com.szhklt.www.voiceassistant.beam.intent.Slot;

public class VolumeControlSkill extends Skill{
	private static final String TAG = "VolumeControlSkill";
	private String volumeFlag;

	AudioManager mAudioManager = (AudioManager) MainApplication.getContext().getSystemService(Service.AUDIO_SERVICE);

	public VolumeControlSkill(intent intent) {
		mintent = intent;
	}

	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		List<Slot> slots = new ArrayList<>();
		slots = mintent.getSemantic().get(0).getSlots();
		LogUtil.e("slots", slots.get(0).toString());
		if(slots.size()==2){
			volumeFlag = slots.get(1).getNormValue();
		}else if(slots.size()==3){
			String action=slots.get(1).getNormValue();//动作(L,T,H)
			LogUtil.e("volume", "action:"+action+LogUtil.getLineInfo());
			String normValue = slots.get(2).getNormValue().replaceAll("%", "");//音量值
			LogUtil.e("volume", "normValue:"+normValue+LogUtil.getLineInfo());
			if(action.equals("T")){
				volumeFlag="T"+normValue;
			}else if(action.equals("L")){
				volumeFlag="-"+normValue;
			}else if(action.equals("H")){
				volumeFlag="+"+normValue;
			}else{
				mTts.doSomethingAfterTts(null,"暂不支持此操作",question);
				return;
			}
		}
		LogUtil.e("volume", "volumeFlag:"+volumeFlag+LogUtil.getLineInfo());
	}

	@Override
	public void execute() {
		extractVaildInformation();
		mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
				// TODO Auto-generated method stub
				recoveryPlayerState();
			}
		},"好的",  question);
//		calculateVolume(volumeFlag);
		calculateVolume2(volumeFlag);
	}

	/**
	 * 新
	 */
	private void calculateVolume2(String volumeFlag){
		int curVol = getCurrentVolume();
		int maxVol = getMaxtVolume();
        StringBuffer letter = new StringBuffer();
        StringBuffer number = new StringBuffer();
        
		char[] strArr = volumeFlag.toCharArray();
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
		LogUtil.e("vol","数字:"+number.toString());
		LogUtil.e("vol","字母:"+letter.toString());
		if(letter.toString().contains("H")){
			int setVol = curVol+(maxVol/100*Integer.valueOf((number.toString())));
			Volume_Control(setVol);
		}else if(letter.toString().contains("L")){
			int setVol = curVol-(maxVol/100*Integer.valueOf((number.toString())));
			Volume_Control(setVol);
		}else if(letter.toString().contains("T") || letter.toString().contains("")){
			Volume_Control(Integer.valueOf(number.toString()));
		}
	}

	/**
	 * 调节音量
	 * @param volume
	 */
	public void Volume_Control(int volume){
		//获取最大音量
		int max = getMaxtVolume();
		//计算调节到的音量值（适配新老固件，老固件为15级音量，新固件为100级音量）
		volume =(int)((volume*max)/100);
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,volume,AudioManager.FLAG_SHOW_UI);
	}

	/**
	 * 获取当前音量值
	 * @return
	 */
	public int  getCurrentVolume(){
		int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
		return current;
	}
	/**
	 * 获取最大音量值
	 * @return
	 */
	public int  getMaxtVolume(){
		int max =  mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
		return max;
	}
}
