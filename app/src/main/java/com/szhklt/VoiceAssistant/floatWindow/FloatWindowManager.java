package com.szhklt.VoiceAssistant.floatWindow;

import java.util.Random;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.service.MainService;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.FloatingActionsMenu.FabLayoutRanger;

/**
 * 现在圆球窗口和海豚窗口统一在该类里面处理
 * @author rz
 *
 */
public class FloatWindowManager {  
	private static String TAG = "FloatWindowManager";

	private static FloatWindowManager floatWindowManager = new FloatWindowManager();

	private Context context = MainApplication.getContext();

	public View volumeview;

	public AnimationDrawable animationDrawable;

	private Handler handler = new Handler();

	/** 
	 * 大海豚View的实例 
	 */  
	public FloatAssistantView bigWindow;  

	/** 
	 * 小海豚View的实例 
	 */  
	private FloatSmallView smallWindow;  

	/** 
	 * 提示悬浮窗View的实例 
	 */  
	private FloatPromptView promptWindow;  

	/** 
	 * 聊天悬浮窗View的实例 
	 */  
	private FloatQuestionView questionWindow; 
	private FloatYellowView yellowWindow; ; 

	/** 
	 * 大海豚View的参数 
	 */  
	private LayoutParams bigWindowParams;  

	/**
	 * 悬浮按钮的实例
	 */
	public static FloatActionButtomView fabButton;
	/**
	 * 悬浮按钮的参数
	 */
	private static LayoutParams floatButtonParams;
	/** 
	 * 用于控制在屏幕上添加或移除悬浮窗 
	 */  
	private WindowManager mWindowManager;  

	/**
	 * 私有构造方法
	 */
	private FloatWindowManager(){
		//各个悬浮窗创建实例化
		createBigWindow(context);
	}

	/**
	 * 单例
	 * @return
	 */
	public static FloatWindowManager getInstance(){
		return floatWindowManager;
	}

	/**
	 * 创建一个大悬浮窗。
	 * @param context 必须为应用程序的Context.
	 *
	 */
	public void createBigWindow(Context context) {  
		WindowManager windowManager = getWindowManager(context);  
		if (bigWindow == null) {  
			bigWindow = new FloatAssistantView(context,this);  
			animationDrawable = FloatAssistantView.animationDrawable;
			volumeview = bigWindow.volumeview;
			if (bigWindowParams == null) {  
				bigWindowParams = new LayoutParams();  
				bigWindowParams.type = LayoutParams.TYPE_PHONE;  
				bigWindowParams.format = PixelFormat.RGBA_8888;  
				bigWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL| LayoutParams.FLAG_NOT_FOCUSABLE;  
				bigWindowParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;  
				bigWindowParams.width = bigWindow.viewWidth;  
				bigWindowParams.height = bigWindow.viewHeight; 
			}
			windowManager.addView(bigWindow, bigWindowParams); 
		}  
		bigWindow.setVisibility(View.GONE);
	}

	public void Volume_SetVisibility(int v){
		if(this.volumeview != null){
			if(v == 0){
				this.volumeview.setVisibility(View.GONE);
			}else {
				this.volumeview.setVisibility(View.VISIBLE);
			}
		}
	}

	FabLayoutRanger ranger=new FabLayoutRanger() {

		@Override
		public void expandLayout() {
			updateViewLayout(FloatActionButtomView.viewWidth, FloatActionButtomView.viewHeight);
		}

		@Override
		public void collapseLayout() {
			updateViewLayout(72,72);
		}
	};

	/**
	 * 创建悬浮按钮的方法
	 * @param context
	 */
	public void createFloatButton(final Context context) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				mWindowManager = getWindowManager(context);
				if (fabButton == null) {
					fabButton = new FloatActionButtomView(context,ranger);
					if (floatButtonParams == null) {
						floatButtonParams = new LayoutParams();
						floatButtonParams.type = LayoutParams.TYPE_PHONE;
						floatButtonParams.format = PixelFormat.RGBA_8888;
						floatButtonParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL| LayoutParams.FLAG_NOT_FOCUSABLE;
						floatButtonParams.gravity = Gravity.LEFT | Gravity.BOTTOM;
						floatButtonParams.width = 72;
						floatButtonParams.height = 72;
					}
					mWindowManager.addView(fabButton, floatButtonParams);
				}
				updateViewLayout(72,72);
			}
		});
	}
	
	/**
	 * 更新悬浮按钮的方法
	 * @param width
	 * @param height
	 */
	public void updateViewLayout(final int width,final int height){
		handler.post(new Runnable() {
			@Override
			public void run() {
				floatButtonParams.width = width;
				floatButtonParams.height = height;
				mWindowManager.updateViewLayout(fabButton, floatButtonParams);
			}
		});
	}
	
	/**
	 * 移除悬浮按钮的方法
	 * @param context
	 */
	public  void removeFloatButton(final Context context) {
		handler.post(new Runnable() {
			@Override
			public void run() {
				if (fabButton != null) {
					WindowManager windowManager = getWindowManager(context);
					windowManager.removeView(fabButton);
					fabButton = null;
				}
			}

		});
	}
	
	/** 
	 * 创建一个小悬浮窗。
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 */  
	//    public void createSmallWindow(Context context) {  
	//        WindowManager windowManager = getWindowManager(context);  
	//        if (smallWindow == null) {  
	//        	smallWindow = new FloatSmallView(context,this);  
	//            if (smallWindowParams == null) {  
	//                smallWindowParams = new LayoutParams();  
	//                smallWindowParams.type = LayoutParams.TYPE_PHONE;  
	//                smallWindowParams.format = PixelFormat.RGBA_8888;  
	//                smallWindowParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL  
	//                        | LayoutParams.FLAG_NOT_FOCUSABLE;  
	//                smallWindowParams.gravity = Gravity.RIGHT | Gravity.BOTTOM;  
	//                smallWindowParams.width = smallWindow.viewWidth;  
	//                smallWindowParams.height = smallWindow.viewHeight;  
	//            }
	//            windowManager.addView(smallWindow, smallWindowParams); 
	//        }  
	//    }

	/** 
	 * 将海豚从屏幕上移除。 
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 */  
	public void removeBigWindow(final Context context) {  
		LogUtil.e(TAG, "removeBigWindow删除海豚");
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (bigWindow != null) {  
					WindowManager windowManager = getWindowManager(context); 
					//                         bigWindow.animationDrawable.stop(); 
					windowManager.removeView(bigWindow);  
					bigWindow = null;  
				}  
			}
		});
	}  

	/** 
	 * 将小海豚从屏幕上移除。 
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 */  
	public void removesmallWindow(final Context context) {  
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (smallWindow != null) {  
					WindowManager windowManager = getWindowManager(context);  
					windowManager.removeView(smallWindow);  
					smallWindow = null;  
				}  
			}
		});
	}  

	/** 
	 * 创建一个提示悬浮窗。
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 */  
	public FloatPromptView createAnswerWindow(final Context context,final int layout_id,final String pro) {  
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if (promptWindow == null) {
					promptWindow = new FloatPromptView(context);  //创建对象
					promptWindow.setLayout(layout_id);//设置布局
					promptWindow.show(pro);
				}
			}
		});
		return promptWindow;
	}

	/** 
	 * 将提示悬浮窗从屏幕上移除。 
	 *  
	 * @param context 
	 *         
	 */  
	public void removeAnswerWindow(Context context){
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(promptWindow != null && promptWindow.isShow() == true){
					promptWindow.closePromptview();
					promptWindow = null;
				}       
			}
		});
	}

	/**
	 * 创建聊天窗悬浮窗
	 * @param context
	 * @param cc
	 */
	public void createQuestionWindow(final Context context,final String cc) {
		LogUtil.e("now","createQuestionWindow()"+LogUtil.getLineInfo());
		handler.post(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(questionWindow == null){
					questionWindow = new FloatQuestionView(context, cc);
				}else{
					questionWindow.records.setText(cc);
				}
			}
		});

	}
	/** 
	 * 将聊天悬浮窗从屏幕上移除。 
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 */ 
	public void removeQuestionWindow(Context context){
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(questionWindow != null){
					questionWindow.closeQuestionview();
					questionWindow = null;//清空对象
				}
			}
		});
	}

	public void removeYellowWindow(Context context){
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(yellowWindow != null){
					yellowWindow.closeYellowview();
					yellowWindow = null;//清空对象
				}
			}
		});
	}

	/** 
	 * 如果WindowManager还未创建，则创建一个新的WindowManager返回。否则返回当前已创建的WindowManager。 
	 *  
	 * @param context 
	 *            必须为应用程序的Context. 
	 * @return WindowManager的实例，用于控制在屏幕上添加或移除悬浮窗。 
	 */  
	private WindowManager getWindowManager(Context context) {  
		if (mWindowManager == null) {  
			mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
		}  
		return mWindowManager;  
	}  

	/*******************************高级封装************************************/
	/**
	 * 播放海豚说话动画
	 */
	public void startTtsAnimation(){
		if(MainService.isLauncherAcyivity == true){
			return;
		}

		this.removesmallWindow(context);
		startAnimation();//开始播放动画
	}

	/**
	 * 开始播放理解动画
	 */
	public void startComprehendAnimation(){
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				if(bigWindow==null){
					createBigWindow(MainApplication.getContext());
				}
				bigWindow.setVisibility(View.VISIBLE);
				removeQuestionWindow(context);
//				removeAnswerWindow(context);
				removesmallWindow(context);
				if(promptWindow == null){
					createAnswerWindow(context, R.layout.bubble_answer,context.getResources().getString(R.string.prompt_title) + "\n"+ context.getResources().getStringArray(R.array.tips)[new Random(System.currentTimeMillis()).nextInt(5)]);
				}
				stopAnimation();//停止播放动画
				if (null != bigWindow&& null != bigWindow.animationIV) {
					bigWindow.animationIV.setImageResource(R.drawable.listen);
					volumeview.setVisibility(View.VISIBLE);
				}
			}
		});

	}

	/**
	 * 开始动画
	 */
	private void startAnimation(){
		handler.post(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				bigWindow.animationIV.setImageResource(R.drawable.speaking);
				animationDrawable = (AnimationDrawable)bigWindow.animationIV.getDrawable();
				animationDrawable.start();
			}
		});
	}


	/**
	 * 停止动画
	 */
	public void stopAnimation(){
		if(animationDrawable == null){
			return;
		}
		if(this.animationDrawable.isRunning()){
			this.animationDrawable.stop();
			this.bigWindow.animationIV.setImageResource(R.drawable.speak1);
		}
	}

	/**
	 * 开始播放思考动画
	 */
	public void startThinkAnimation(){
		if(MainService.isLauncherAcyivity == true){
			return;
		}

		if (null != this.bigWindow&& null != this.bigWindow.animationIV) {
			this.bigWindow.animationIV.setImageResource(R.drawable.think);
			this.volumeview.setVisibility(View.VISIBLE);
		}
	}

	/**
	 * 删除所有窗口
	 */
	public void removeAll(){
		LogUtil.e(TAG, "removeAll()被执行了");
		//		removeBigWindow(context);
		bigWindow.setVisibility(View.GONE);
		removeQuestionWindow(context);
		removeAnswerWindow(context);
	}

	/**
	 * 刷新问答窗口
	 */
	public void flushQandAWindow(String answer,String question) {
		LogUtil.e("now","flushQandAWindow()"+LogUtil.getLineInfo());
		if(question != null){
//			removeQuestionWindow(context);
			createQuestionWindow(context,question);
		}

		if(answer != null){
			removeAnswerWindow(context);
			createAnswerWindow(context,R.layout.bubble_answer,answer);
		}
	}

	/**
	 * 设置大海豚监听者
	 */
	public void  setOnClickListener(OnClickListener listener){
		bigWindow.setOnClickListener(listener);	
	}
}  


