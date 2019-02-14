package com.szhklt.www.voiceassistant.floatWindow;

import android.content.Context;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.TextView;

import com.szhklt.www.voiceassistant.R;

public class FloatYellowView extends View {
	
	public int bShow = 0;//用于标记当前悬浮窗是否存在
	  //创建浮动窗口设置布局参数的对象  
	WindowManager mWindowManager; 
	FloatWindowManager mFloatWindowManager;
	public View cmContentView; //代表的是当前的聊天悬浮窗 


	 public FloatYellowView(Context context,String cmd) {
		super(context);
		
		mFloatWindowManager = FloatWindowManager.getInstance();
		// TODO Auto-generated constructor stub
		bShow = 1;//为1时表示悬浮窗存在
		cParams = new WindowManager.LayoutParams();
	    //获取的是WindowManagerImpl.CompatModeWrapper  
        mWindowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);  
		
        //设置window type  
        cParams.type = LayoutParams.TYPE_PHONE;   
        //设置图片格式，效果为背景透明  
        cParams.format = PixelFormat.RGBA_8888;   
        //设置浮动窗口不可聚焦（实现操作除浮动窗口外的其他可见窗口的操作）  
        cParams.flags = LayoutParams.FLAG_NOT_FOCUSABLE;        
        //调整悬浮窗显示的停靠位置为左侧置顶  
        cParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.TOP;         
        // 以屏幕左上角为原点，设置x、y初始值，相对于gravity  
        cParams.x = 0;  
        cParams.y = 0;  
        
        //设置悬浮窗口长宽数据    
        cParams.width = WindowManager.LayoutParams.WRAP_CONTENT;  
        cParams.height = WindowManager.LayoutParams.WRAP_CONTENT;  
        
        //重点：LayoutInflater在实际开发中LayoutInflater这个类还是
        //非常有用的，它的作用类似于findViewById()。
        //不同点是LayoutInflater是用来找res/layout/下的xml布局文件，并且实例化
        
        LayoutInflater inflater = LayoutInflater.from(context);  
        //获取浮动窗口视图所在布局
        //类似setContentView()
        cmContentView = inflater.inflate(R.layout.bubble_top, null);
        
        //改变内容
        TextView records = (TextView) cmContentView.findViewById(R.id.records1);
        records.setText(cmd);
        //添加mFloatLayout  
        mWindowManager.addView(cmContentView, cParams);//显示
           
	}

//	//定义浮动窗口布局  
//    LinearLayout cFloatLayout;  
    WindowManager.LayoutParams cParams;  

	
	public void closeYellowview() {
		bShow = 0;//为0时表示悬浮窗不存在
		mWindowManager.removeView(cmContentView);
		mWindowManager = null;
		//}
	}

}