package com.szhklt.VoiceAssistant.skill;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;

import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.activity.SleepActivity;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.floatWindow.FloatActionButtomView;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.util.LogUtil;

public class KeyeventControlSkill extends Skill{
	private static final String TAG = "KeyeventControlSkill";
	private String Kflag;
	//linein control
	private String action;
	private String source;
	private FloatWindowManager mFW = FloatWindowManager.getInstance();
	private KwSdk mKwSdk = KwSdk.getInstance();
	private Context context = MainApplication.getContext();
	public static String skillIntent;
	public KeyeventControlSkill(intent intent){
		mintent = intent;
	}
	
	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		List<Slot> slots = new ArrayList<>();
		slots = mintent.getSemantic().get(0).getSlots();
		skillIntent = mintent.getSemantic().get(0).getIntent();
		if("event_ctr".equals(skillIntent)){
			for(Slot slot:slots){
				if("keyevent".equals(slot.getName())){
					Kflag = slot.getNormValue();
					continue;
				}
			}
		}else if("linein".equals(skillIntent)){
			action = mintent.getSemantic().get(0).getSlots().get(0).getNormValue();
			source = mintent.getSemantic().get(0).getSlots().get(1).getNormValue();
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		if("event_ctr".equals(skillIntent)){
			subExecuteForEventCtr();
			recoveryPlayerState();
		}else if("linein".equals(skillIntent)){
			subExecuteForLineinCtr();
		}	
	}
	
	public void subExecuteForLineinCtr(){
		if("on".equals(action)){
			if("ble".equals(source)){
				LogUtil.e(TAG,"打开蓝牙");
//				Intent bleIntent = new Intent(context, BlueToothActivity.class); 
//				bleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//				context.startActivity(bleIntent); 
//				FloatActionButtomView.turnOnBluePush();
			}else if("aux".equals(source)){
				LogUtil.e(TAG,"打开aux");
//				FloatActionButtomView.turnOnAuxin();
			}
		}else if("off".equals(action)){
			if("ble".equals(source)){
				LogUtil.e(TAG,"关闭蓝牙");
//				context.sendBroadcast(new Intent("android.bluetooth.action.FINISH"));
//				FloatActionButtomView.turnOffBluePush();
			}else if("aux".equals(source)){
				LogUtil.e(TAG,"关闭aux");
				FloatActionButtomView.turnOffAuxin();LogUtil.e("auxinStatus","turnOffAuxin()"+LogUtil.getLineInfo());
			}
		}
	}
	
	public void subExecuteForEventCtr(){
		if ("home".equals(Kflag)) {
			mKwSdk.exit();
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			context.startActivity(intent);
		} else if ("sleep".equals(Kflag)) {
//			context.send("gotosleep");
			mKwSdk.exit();
			Intent intent = new Intent(context, SleepActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		}
	}
}
