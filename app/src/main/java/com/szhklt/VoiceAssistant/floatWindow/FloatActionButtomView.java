package com.szhklt.VoiceAssistant.floatWindow;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.util.LineInControler;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.FloatingActionButton;
import com.szhklt.VoiceAssistant.view.FloatingActionsMenu;
import com.szhklt.VoiceAssistant.view.GuideView;


public class FloatActionButtomView extends LinearLayout{
	public static Boolean auxinStatus = false;//表示auxin状态
	private Context context;

	/**
	 * 记录小悬浮窗的宽度
	 */
	public static int viewWidth;

	/**
	 * 记录小悬浮窗的高度
	 */
	public static int viewHeight;

	/**
	 * 记录系统状态栏的高度
	 */
	private static int statusBarHeight;

	/**
	 * 用于更新小悬浮窗的位置
	 */
	private WindowManager windowManager;

	/**
	 * 小悬浮窗的参数
	 */
	private WindowManager.LayoutParams mParams;

	/**
	 * 记录当前手指位置在屏幕上的横坐标值
	 */
	private float xInScreen;

	/**
	 * 记录当前手指位置在屏幕上的纵坐标值
	 */
	private float yInScreen;

	/**
	 * 记录手指按下时在屏幕上的横坐标的值
	 */
	private float xDownInScreen;

	/**
	 * 记录手指按下时在屏幕上的纵坐标的值
	 */
	private float yDownInScreen;

	/**
	 * 记录手指按下时在小悬浮窗的View上的横坐标的值
	 */
	private float xInView;

	/**
	 * 记录手指按下时在小悬浮窗的View上的纵坐标的值
	 */
	private float yInView;

	public FloatingActionsMenu thisMenu;
	private FloatingActionButton update,alarmlist,wakeup,reboot,hidebutton;
	public static FloatingActionButton auxin,bletooth;
	public FloatActionButtomView(Context context, FloatingActionsMenu.FabLayoutRanger ranger) {
		super(context);
		// TODO Auto-generated constructor stub
		this.context = context;
		windowManager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
		LayoutInflater.from(context).inflate(R.layout.float_window_small_1, this);
		View view = findViewById(R.id.small_window_layout);
		thisMenu = (FloatingActionsMenu)view.findViewById(R.id.right_labels);
		thisMenu.setfabRanger(ranger);
		viewWidth = view.getLayoutParams().width;
		viewHeight = view.getLayoutParams().height;
		//		percentView.setText(MyWindowManager.getUsedPercentValue(context));
		//添加按键
		//	    FloatingActionButton addedOnce = new FloatingActionButton(context);
		//	    percentView.addButton(addedOnce);
		update = (FloatingActionButton)view.findViewById(R.id.floatupdate);
		alarmlist = (FloatingActionButton)view.findViewById(R.id.floatalarmlist);
		wakeup = (FloatingActionButton)view.findViewById(R.id.floatswitch_wake);
		wakeup.setTitle(getSwicthState()==true?"关闭唤醒":"开启唤醒");
		reboot = (FloatingActionButton)view.findViewById(R.id.floatreboot);
		update.setOnClickListener(listener);
		alarmlist.setOnClickListener(listener);
		wakeup.setOnClickListener(listener);
		reboot.setOnClickListener(listener);
		bletooth = (FloatingActionButton)view.findViewById(R.id.bletooth);
		bletooth.setOnClickListener(listener);
		auxin = (FloatingActionButton)view.findViewById(R.id.auxin);
		auxin.setOnClickListener(listener);
		hidebutton = (FloatingActionButton)view.findViewById(R.id.hidebutton);
		hidebutton.setOnClickListener(listener);
	}

	private View.OnClickListener listener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.floatupdate:
//				context.startService(new Intent(MainApplication.getContext(),checkAPKUpdataService.class));//启动apk更新检查服务
//				LogUtil.e("updata","MainApplication.UpdataMark :"+MainApplication.UpdataMark+LogUtil.getLineInfo());
//				if(MainApplication.UpdataMark == true){
//					Intent intent=new Intent(context,UpdateActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//					context.startActivity(intent);
//				}else{
//					Toast.makeText(context, "主人,当前版本为"+DownLoadUtils.getAPPVersionCode(context)+",无需更新呢!", Toast.LENGTH_SHORT).show();
//					Intent intent=new Intent(context,ADCandJetActivity.class);
//					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//					context.startActivity(intent);
//				}
				break;
			case R.id.floatalarmlist:
//				Intent intent=new Intent(context,AlarmListActivity.class);
//				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//				context.startActivity(intent);
				break;
			case R.id.floatswitch_wake:
				if("关闭唤醒"==wakeup.getTitle()){
					wakeup.setColorNormalResId(R.color.gold);
					Intent intent1=new Intent();
					intent1.putExtra("count", "STOP_PCMRECORD");
					intent1.setAction("com.szhklt.FloatWindow.FloatSmallView");
					context.sendBroadcast(intent1);
					saveSwicthState(false, "swstate");
					wakeup.setTitle("开启唤醒");
					Toast.makeText(context, "语音助手：已关闭唤醒", Toast.LENGTH_SHORT).show();
				}else if("开启唤醒"==wakeup.getTitle()){
					wakeup.setColorNormalResId(R.color.default_color_1);
					saveSwicthState(true, "swstate");
					Intent intent1=new Intent();
					intent1.putExtra("count", "WRITE_AUDIO");
					intent1.setAction("com.szhklt.FloatWindow.FloatSmallView");
					context.sendBroadcast(intent1);
					saveSwicthState(true, "swstate");
					wakeup.setTitle("关闭唤醒");
				}
				break;
			case R.id.floatreboot:
//				Intent intent1=new Intent(context,RebootSetActivity.class);
//				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//				context.startActivity(intent1);
				break;
			case R.id.bletooth:
//				disposeBluePush();
				//测试蓝牙
//				Intent bleIntent = new Intent(context, BlueToothActivity.class); 
//				bleIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
//				context.startActivity(bleIntent); 
				break;
			case R.id.auxin://切换为auxin
				LogUtil.e("auxin",auxinStatus+LogUtil.getLineInfo());
//				disposeAuxin();
				break;
			case R.id.hidebutton:
				thisMenu.collapse();
				FloatWindowManager.getInstance().removeFloatButton(getContext());
				return;
			}
			thisMenu.collapse();
			FloatWindowManager.getInstance().updateViewLayout(72,72);
		}
	};
	
	/**
	 * Auxin按钮被点击时处理
	 */
//	public static void disposeAuxin(){
//		if(auxinStatus == true){
//			LogUtil.e("auxinStatus","auxinStatus变成false了");
//			turnOffAuxin();LogUtil.e("auxinStatus","turnOffAuxin()"+LogUtil.getLineInfo());
//		}else{
//			LogUtil.e("auxinStatus","auxinStatus变成true了");
//			turnOnAuxin();
//		}
//	}
	
	/**
	 * BluePush按钮点击时处理
	 */
//	public static void disposeBluePush(){
//		if(getSwicthState("blestate")){
//			turnOnBluePush();
//		}else{
//			turnOffBluePush();
//		}
//	}
	
	/**
	 * 打开蓝牙
	 */
//	public static void turnOnBluePush(){
//		LogUtil.e("turnOnBluePush", "turnOnBluePush()");
//		//关闭auxin
//		turnOffAuxin();LogUtil.e("auxinStatus","turnOffAuxin()"+LogUtil.getLineInfo());
//		//关闭酷我音乐
//		KwSdk.getInstance().pause();
//		//关闭多媒体播放器
//		MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
//
////		bletooth.setColorNormalResId(R.color.default_color_1);
//		if(bletooth != null){
//			bletooth.setTitle("关闭蓝牙");
//		}
//		LineInControler.getInstance().switchBle();
//		saveSwicthState(false, "blestate");
//
//		Intent bleIntent = new Intent(MainApplication.getContext(),BlueToothActivity2.class);
//		bleIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
//		MainApplication.getContext().startActivity(bleIntent);
//	}
	
	/**
	 * 打开蓝牙
	 */
//	public static void turnOffBluePush(){
////		bletooth.setColorNormalResId(R.color.gold);
//		if(bletooth != null){
//			bletooth.setTitle("打开蓝牙");
//		}
//		LineInControler.getInstance().stopLineIn();
//		saveSwicthState(true, "blestate");
//
//		MainApplication.getContext().sendBroadcast(new Intent(BlueToothActivity2.CLOSE_ACTIVITY));
//	}
	
	/**
	 * 关闭auxin
	 */
	public static void turnOffAuxin(){
		LogUtil.e("auxinStatus","关闭auxin");
		if(auxinStatus == true){
			LogUtil.e("auxinStatus","auxinStatus变成false了");
			auxinStatus = false;
			LineInControler.getInstance().stopLineIn();
		}
	}
	
	/**
	 * 打开auxin
	 */
//	public static void turnOnAuxin(){
//		LogUtil.e("auxinStatus","打开auxin");
//		//关闭蓝牙
//		turnOffBluePush();
//		if(auxinStatus == false){
//			LogUtil.e("auxinStatus","auxinStatus变成true了");
//			//关闭蓝牙推送
////			MainApplication.getContext().sendBroadcast(new Intent("android.bluetooth.action.FINISH").putExtra("fab",true));
//			BlueToothActivity2.openAux = true;
//			MainApplication.getContext().sendBroadcast(new Intent(BlueToothActivity2.CLOSE_ACTIVITY));
//			//关闭酷我音乐
//			KwSdk.getInstance().pause();
//			//关闭多媒体播放器
//			MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
//			auxinStatus = true;
//			LogUtil.e("auxinStatus","switchLineIn()"+LogUtil.getLineInfo());
//			LineInControler.getInstance().switchLineIn();
//		}
//	}
	
	/**
	 * 刷新auxin按键UI
	 */
	public static void flushAuxinFab(Boolean status){
		LogUtil.e("auxin","flushAuxinFab("+status+")"+LogUtil.getLineInfo());
		if(status == true){
			if(auxin != null){
				auxin.setTitle("打开Auxin");
			}
		}else{
			if(auxin != null){
				auxin.setTitle("关闭Auxin");
			}
		}
	}
	
	private GuideView guideView;
	private void setGuideView() {
		Typeface typeface=Typeface.createFromAsset(MainApplication.getContext().getAssets(),"fonts/digital-7.ttf");
        // 使用文字
        TextView tv1 = new TextView(context);
        tv1.setText("点击这里打开菜单");
        tv1.setTextColor(getResources().getColor(R.color.white1));
        tv1.setTextSize(30);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTypeface(typeface);
        
        guideView = GuideView.Builder
                .newInstance((Activity)getContext())
                .setTargetView(thisMenu)//设置目标
                .setCustomGuideView(tv1)
                .setDirction(GuideView.Direction.RIGHT)
                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
//                .setTargetView(tv)
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                    	guideView.hide();
                    }
                })
                .build();
        guideView.show();
    }
	
	//保存按键状态
	public static void saveSwicthState(boolean state,String key){
		SharedPreferences.Editor editor = MainApplication.getContext().getSharedPreferences("keystatus",Context.MODE_PRIVATE).edit();
		if(state){
			editor.putString(key,"on");
		}else{
			editor.putString(key,"off");
		}
		editor.commit();
	}
	
	private static boolean getSwicthState(String key){
		SharedPreferences pref = MainApplication.getContext().getSharedPreferences("keystatus",Context.MODE_PRIVATE);
		String state = pref.getString("blestate","on");
		LogUtil.e("bluepush", state+LogUtil.getLineInfo());
		if(state.equals("on")){
			return true;
		}else{
			return false;	
		}
	}
	
	private boolean getSwicthState(){
		SharedPreferences pref = context.getSharedPreferences("keystatus", Context.MODE_PRIVATE);
		String state = pref.getString("swstate","on");//默认打开
		if("on".equals(state)){
			return true;
		}else if("off".equals(state)){
			return false;
		}else {
			return false;
		}
	}
}
