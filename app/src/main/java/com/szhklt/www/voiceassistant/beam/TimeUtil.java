package com.szhklt.www.voiceassistant.beam;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import android.annotation.SuppressLint;
import android.content.Context;

public class TimeUtil {
	public Context context;
	public Date date;
	//DateFormat
	String YEAR = "yyyy";
	String MONTH = "MM";
	String DAY = "dd";
	String HOUR = "HH";
	String MINUTE = "mm";
	String SECOND = "ss";
	
	public TimeUtil(Context context) {
		this.context = context;
		date = new Date();
	}
	
	//获取当前时间
    public int[] Date_Func_Array(){
		int[] Date_Array = new int[6];
		Date_Array[0] = Integer.valueOf(_GetYear());
		Date_Array[1] = Integer.valueOf(_GetMonth());
		Date_Array[2] = Integer.valueOf(_GetDays());
		Date_Array[3] = Integer.valueOf(_GetHour());
		Date_Array[4] = Integer.valueOf(_GetMinute());
		Date_Array[5] = Integer.valueOf(_GetSecond());
		return Date_Array;
    }
    
    //获取当前日期
    @SuppressLint("SimpleDateFormat") 
    public String getTodaysDate(){
    	SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd");  
    	String date = sDateFormat.format(new java.util.Date()); 
    	return date;
    }
    
	//获取系统日期
    @SuppressLint("SimpleDateFormat")
    public String _GetYear(){
        SimpleDateFormat sdf = new SimpleDateFormat(YEAR);
        String s = sdf.format(date);
        return s;
    }
    @SuppressLint("SimpleDateFormat")
    public String _GetMonth(){
        SimpleDateFormat sdf = new SimpleDateFormat(MONTH);
        String s = sdf.format(date);
        return s;
    }
    @SuppressLint("SimpleDateFormat")
    public String _GetHour(){
        SimpleDateFormat sdf = new SimpleDateFormat(HOUR);
        String s = sdf.format(date);
        return s;
    }
    @SuppressLint("SimpleDateFormat")
    public String _GetMinute(){
        SimpleDateFormat sdf = new SimpleDateFormat(MINUTE);
        String s = sdf.format(date);
        return s;
    }
    @SuppressLint("SimpleDateFormat")
    public String _GetSecond(){
        SimpleDateFormat sdf = new SimpleDateFormat(SECOND);
        String s = sdf.format(date);
        return s;
    }
    @SuppressLint("SimpleDateFormat")
    public String _GetDays(){
        SimpleDateFormat sdf = new SimpleDateFormat(DAY);
        String s = sdf.format(date);
        return s;
    }
    
    //获取周几
    @SuppressLint("SimpleDateFormat")
    public String _GetWeek(){
        SimpleDateFormat sdf = new SimpleDateFormat("E ");
        String s = sdf.format(date);
        return s;
    }

    //获取星期几      
    public String getWeek(){  
    	String mWay;
        final Calendar c = Calendar.getInstance();  
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
        if("1".equals(mWay)){  
            mWay ="天";  
        }else if("2".equals(mWay)){  
            mWay ="一";  
        }else if("3".equals(mWay)){  
            mWay ="二";  
        }else if("4".equals(mWay)){  
            mWay ="三";  
        }else if("5".equals(mWay)){  
            mWay ="四";  
        }else if("6".equals(mWay)){  
            mWay ="五";  
        }else if("7".equals(mWay)){  
            mWay ="六";  
        }  
        return "星期"+mWay;  
    }  
        
    public String getWeek(Date curDate){  
    	String mWay;
        final Calendar c = Calendar.getInstance(); 
        c.setTime(curDate);
        c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));  
        mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));  
        if("1".equals(mWay)){  
            mWay ="天";  
        }else if("2".equals(mWay)){  
            mWay ="一";  
        }else if("3".equals(mWay)){  
            mWay ="二";  
        }else if("4".equals(mWay)){  
            mWay ="三";  
        }else if("5".equals(mWay)){  
            mWay ="四";  
        }else if("6".equals(mWay)){  
            mWay ="五";  
        }else if("7".equals(mWay)){  
            mWay ="六";  
        }  
        return "星期"+mWay;  
    }
     
    
	//获取明天的日期
    public Date getNextDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, +1);//+1今天的时间加一天
        date = calendar.getTime();
        return date;
    }

    @SuppressLint("SimpleDateFormat")
    public String[] _GetTomorrowDate(){
    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");//24小时制
        String TomorrowDate = sdf.format(getNextDay(new Date()));
        String[] TomorrowDateArray = new String[3];
        TomorrowDateArray = TomorrowDate.split("-");
        int[] iTomorrowDateArray = new int[3];
        for(int i = 2;i >= 0;i--){
            iTomorrowDateArray[i] = Integer.valueOf(TomorrowDateArray[i]);
        }
		return TomorrowDateArray;
    }
    
	//获取上下午
    public int get_AMorPM(){
		long time = System.currentTimeMillis();
		final Calendar mCalendar = Calendar.getInstance();
		mCalendar.setTimeInMillis(time);
		int apm = mCalendar.get(Calendar.AM_PM);
		return apm;
	}
    
	/**
	 * 获取当前日期是星期几<br>
	 * 
	 * @param dt
	 * @return 当前日期是星期几
	 */
	public int getWeekOfDate() {
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
	    if (w < 0)
	        w = 0;
	    return w;
	}
    
	//日期排序
	public String[] getsortWeekOfDate(){
	    String[] weekDays = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
	    String[] sortWeekDays = new String[7];
		for(int i = 0;i < 7;i++){
			sortWeekDays[i] = weekDays[(getWeekOfDate()+i)%7];
		}
		return sortWeekDays;
	}
}
