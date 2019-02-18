package com.szhklt.VoiceAssistant.multithreadeddownloader;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MyDialog extends Dialog{
	public MyDialog(Context context,int width,int height,View layout,int style) {
		super(context,style);
		// TODO Auto-generated constructor stub
		setContentView(layout);
		Window window = getWindow();
		window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
		WindowManager.LayoutParams params = window.getAttributes();
		params.gravity = Gravity.CENTER;
		window.setAttributes(params);
	}
}
