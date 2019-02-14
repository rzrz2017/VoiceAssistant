package com.szhklt.www.voiceassistant.view;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;

import com.szhklt.www.voiceassistant.service.MainService;

public class ViewVolume extends View{

    private int mRectWidth;

    private Paint mPaint;
    
    private LinearGradient mLinearGradient;

    private int mRectCount = 5;

    private int offset = 1;

    private double mRandom;
    
    public Resources resources = this.getResources();
    
    public DisplayMetrics dm = resources.getDisplayMetrics();  
    
	public int width = dm.widthPixels;
	
	public int height = dm.heightPixels;

    public ViewVolume(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public ViewVolume(Context context, AttributeSet attrs,
                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        // 创建绘制矩形的画笔
        mPaint = new Paint();
        // 蓝色
        mPaint.setColor(Color.CYAN);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        mScreenWidth = w;
//        mScreenHeight = h;
//        mRectWidth = (int) (mScreenWidth * 0.6 / mRectCount);
        mRectWidth = 4;
        mLinearGradient = new LinearGradient(
        		160,
        		266,
        		160,
        		280,
        		Color.MAGENTA,//洋红
        		Color.CYAN,// 氰基
        		TileMode.CLAMP);
        mPaint.setShader(mLinearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {//重绘
        super.onDraw(canvas);
        for (int i = 0; i < mRectCount; i++) {
            mRandom = Math.random();
//            LogUtil.e("MainService.volume_value",String.valueOf(MainService.volume_value));
            canvas.drawRect(
      	          (float) (160 + mRectWidth*i + offset*i),
      	          (float) (277 - MainService.volume_value*mRandom),
      	          (float) (164 + mRectWidth*i + offset*i),
      	          (float) 282,
      	          mPaint);
            }   
            // 每隔 0.3 秒重绘一次
//            postInvalidateDelayed(100);  
        	invalidate();
    }
    
    public int getStatusBarHeight() {
	    int result = 0;
	    int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
	    if (resourceId > 0) {
	    	result = getResources().getDimensionPixelSize(resourceId);
	    }
	    return result;
    }
}