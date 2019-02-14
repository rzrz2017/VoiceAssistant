package com.szhklt.www.voiceassistant.util;

import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.szhklt.www.voiceassistant.beam.SemanticUnderstandResultData;
import com.szhklt.www.voiceassistant.db.AlarmClockDBHelper;

public class HkAlarmClock {
	public SQLiteDatabase AlarmClockdb;
	private ContentValues values;
	private int[] Date_Array;// 当前
	public String[] sTomorrowDateArray;
	public Calendar c;
	public int[] twelfthHour = { 12, 0, 0 };
	public JsonParser mjsonparse = new JsonParser();
	String[] YMDHMS;
	private AlarmClockDBHelper dbHelper;

	public String[] AlarmClockRc0(Context context, String xftext) {
		dbHelper = AlarmClockDBHelper.getInstance();
		AlarmClockdb = dbHelper.getWritableDatabase();
		values = new ContentValues();
		Date_Array = Date_Func_Array();
		sTomorrowDateArray = _GetTomorrowDate();
		if (Date_Array[3] > 12) {// 转换为12小时制
			Date_Array[3] -= 12;
		}
		SemanticUnderstandResultData mResponseMsg = mjsonparse.parseUnderstander(xftext);
		if ("CREATE".equals(mResponseMsg.getintent())) {// 添加闹钟
			// 分离字符串
			String action = mResponseMsg.getscheduleXaction();
			String temp = mResponseMsg.getsuggestDatetime();
			String sourceStr = mResponseMsg.getsuggestDatetime() + ":" + action;
			String[] sourceStrArray = sourceStr.split("T");
			String[] YMD = new String[3];
			String[] HMS = new String[4];
			if (temp.indexOf("-") != -1) {
				YMD = sourceStrArray[0].split("-");
			}
			if (temp.indexOf(":") != -1) {
				HMS = sourceStrArray[1].split(":");
			}
			if (Integer.valueOf(YMD[1]) > Integer.valueOf(_GetMonth())
					|| Integer.valueOf(YMD[2]) > Integer.valueOf(_GetDays())) {
				LogUtil.i("111***************************", "日期不是今天");

			} else {
				// 转换为12小时制
				if (Integer.valueOf(HMS[0]) > 12) {// 转换为12小时制
					HMS[0] = (Integer.valueOf(HMS[0]) - 12) + "";
				}
				if (YMD[0] != "") {// 日期不为空的情况
					if (get_AMorPM() < 12) {// 上午
						if (Integer.valueOf(HMS[0]) * 3600
								+ Integer.valueOf(HMS[1]) * 60
								+ Integer.valueOf(HMS[2]) < Date_Array[3]
								* 3600 + Date_Array[4] * 60 + Date_Array[5]) {
							HMS[0] = String
									.valueOf(Integer.valueOf(HMS[0]) + 12);// +12
						}
						YMD[0] = _GetYear();
						YMD[1] = _GetMonth();
						YMD[2] = _GetDays();
					} else if (get_AMorPM() >= 12) {// 下午
						if (Integer.valueOf(HMS[0]) * 3600
								+ Integer.valueOf(HMS[1]) * 60
								+ Integer.valueOf(HMS[2]) < Date_Array[3]
								* 3600 + Date_Array[4] * 60 + Date_Array[5]) {// 明天上午//小
							YMD[0] = sTomorrowDateArray[0];
							YMD[1] = sTomorrowDateArray[1];
							YMD[2] = sTomorrowDateArray[2];
						}
						if (Integer.valueOf(HMS[0]) * 3600
								+ Integer.valueOf(HMS[1]) * 60
								+ Integer.valueOf(HMS[2]) > Date_Array[3]
								* 3600 + Date_Array[4] * 60 + Date_Array[5]) {// 下午//大于当前时间
							YMD[0] = _GetYear();
							YMD[1] = _GetMonth();
							YMD[2] = _GetDays();
							if (Integer.valueOf(HMS[0]) < 12) {
								HMS[0] = String.valueOf((Integer
										.valueOf(HMS[0]) + 12));
							}
						}
					}
				}
			}
			// 合并数组
			YMDHMS = new String[YMD.length + HMS.length + 1];
			System.arraycopy(YMD, 0, YMDHMS, 0, YMD.length);
			System.arraycopy(HMS, 0, YMDHMS, YMD.length, HMS.length);
			// 过滤比当前时间小的闹钟
			if (Integer.valueOf(HMS[0]) * 3600 + Integer.valueOf(HMS[1]) * 60
					+ Integer.valueOf(HMS[2]) > Date_Array[3] * 3600
					+ Date_Array[4] * 60 + Date_Array[5]) {
				// 获取系统日期
				if (YMD[0] == "") {
					String y = _GetYear();
					String m = _GetMonth();
					String d = _GetDays();
					values.put("year", y);
					values.put("month", m);
					values.put("day", d);
				} else {
					values.put("year", YMD[0]);
					values.put("month", YMD[1]);
					values.put("day", YMD[2]);
				}
				values.put("hour", HMS[0]);
				values.put("minute", HMS[1]);
				values.put("second", HMS[2]);
				values.put("action", HMS[3]);
				AlarmClockdb.insert("alarmclock", null, values);
				values.clear();
			}
		}
		return YMDHMS;
	}

	public void AlarmClockRc3(String xftext) {

		SemanticUnderstandResultData responseMsg = mjsonparse.parseUnderstander(xftext);
		if (responseMsg.awr.getAnsw().indexOf("呢") == -1) {// 有具体时间
			if (responseMsg.getintent().indexOf("CHANGE") != -1) {// 改变
				AlarmClockdb.delete("alarmclock", null, null);
			} else if (responseMsg.getintent().indexOf("CANCEL") != -1) {// 取消
				AlarmClockdb.delete("alarmclock", null, null);
			}
		}
	}

	// 获取上下午
	private int get_AMorPM() {
		Calendar calendar = Calendar.getInstance();
		int hour = calendar.get(Calendar.HOUR_OF_DAY);
		return hour;
	}

	// 获取明天的日期
	public Date getNextDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +1);// +1今天的时间加一天
		date = calendar.getTime();
		return date;
	}

	/**
	 * 获取明天的日期
	 * @return
	 */
	public String[] _GetTomorrowDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
		String TomorrowDate = sdf.format(getNextDay(new Date()));
		String[] TomorrowDateArray = new String[3];
		TomorrowDateArray = TomorrowDate.split("-");
		int[] iTomorrowDateArray = new int[3];
		for (int i = 2; i >= 0; i--) {
			iTomorrowDateArray[i] = Integer.valueOf(TomorrowDateArray[i]);
			LogUtil.e("reboot","iTomorrowDateArray["+i+"]"+iTomorrowDateArray[i]);
		}
		return TomorrowDateArray;
	}

	public Date getTaDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.DAY_OF_MONTH, +0);// +1今天的时间加一天
		date = calendar.getTime();
		return date;
	}

	@SuppressLint("SimpleDateFormat")
	public String[] _GetTadayDate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");// 24小时制
		String TomorrowDate = sdf.format(getTaDay(new Date()));
		String[] TomorrowDateArray = new String[3];
		TomorrowDateArray = TomorrowDate.split("-");
		int[] iTomorrowDateArray = new int[3];
		for (int i = 2; i >= 0; i--) {
			iTomorrowDateArray[i] = Integer.valueOf(TomorrowDateArray[i]);
		}
		return TomorrowDateArray;
	}

	// 获取系统日期
	@SuppressLint("SimpleDateFormat")
	public String _GetYear() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
		String s = sdf.format(new Date());
		return s;
	}

	@SuppressLint("SimpleDateFormat")
	public String _GetMonth() {
		SimpleDateFormat sdf = new SimpleDateFormat("MM");
		String s = sdf.format(new Date());
		return s;
	}

	@SuppressLint("SimpleDateFormat")
	public String _GetHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String s = sdf.format(new Date());
		return s;
	}

	@SuppressLint("SimpleDateFormat")
	public int getHour() {
		SimpleDateFormat sdf = new SimpleDateFormat("HH");
		String s = sdf.format(new Date());
		int Hour = Integer.parseInt(s);
		return Hour;
	}

	@SuppressLint("SimpleDateFormat")
	public String _GetMinute() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		String s = sdf.format(new Date());
		return s;
	}

	@SuppressLint("SimpleDateFormat")
	public int getMinute() {
		SimpleDateFormat sdf = new SimpleDateFormat("mm");
		String s = sdf.format(new Date());
		int min = Integer.parseInt(s);
		return min;

	}

	@SuppressLint("SimpleDateFormat")
	public String _GetSecond() {
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		String s = sdf.format(new Date());
		return s;
	}

	@SuppressLint("SimpleDateFormat")
	public int getSecond() {
		SimpleDateFormat sdf = new SimpleDateFormat("ss");
		String s = sdf.format(new Date());
		int Sec = Integer.parseInt(s);
		return Sec;
	}

	@SuppressLint("SimpleDateFormat")
	public String _GetDays() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd");
		String s = sdf.format(new Date());
		return s;
	}

	// 获取当前时间
	private int[] Date_Func_Array() {
		int[] Date_Array = new int[6];
		Date_Array[0] = Integer.valueOf(_GetYear());
		Date_Array[1] = Integer.valueOf(_GetMonth());
		Date_Array[2] = Integer.valueOf(_GetDays());
		Date_Array[3] = Integer.valueOf(_GetHour());
		Date_Array[4] = Integer.valueOf(_GetMinute());
		Date_Array[5] = Integer.valueOf(_GetSecond());
		return Date_Array;
	}
	
	//该函数不能在主线程运行
    public static String getNetTime() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
            LogUtil.e("reboot","网络时间:"+format);
    		return format;
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    Toast.makeText(MainActivity.this, "当前网络时间为: \n" + format, Toast.LENGTH_SHORT).show();
//                    tvNetTime.setText("当前网络时间为: \n" + format);
//                }
//            });
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * 获取网络日期
     * @return
     */
    public static String getNetDateStr() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
            final String format = formatter.format(calendar.getTime());
            LogUtil.e("reboot","网络日期:"+format);
    		return format;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static Date getNetDate() {
        URL url = null;//取得资源对象
        try {
            url = new URL("http://www.baidu.com");
            //url = new URL("http://www.ntsc.ac.cn");//中国科学院国家授时中心
            //url = new URL("http://www.bjtime.cn");
            URLConnection uc = url.openConnection();//生成连接对象
            uc.connect(); //发出连接
            long ld = uc.getDate(); //取得网站日期时间
            Calendar calendar = Calendar.getInstance();
            calendar.setTimeInMillis(ld);
    		return calendar.getTime();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
