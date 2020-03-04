package com.szhklt.VoiceAssistant.skill;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;

import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.activity.AlarmListActivity;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.db.AlarmClockDBHelper;
import com.szhklt.VoiceAssistant.db.AlarmDBHandler;
import com.szhklt.VoiceAssistant.impl.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.util.HkAlarmClock;
import com.szhklt.VoiceAssistant.util.LogUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class AlarmSkill extends Skill{	
	private static Context context= MainApplication.getContext();
	private String alarmintent;
	private String answer;
	private String state;
	private String content = null;
	private String suggestDatetime = null;
	private String repeat = null;
	private String alarmtype = null;
	private int alarmstate=1;//表示闹钟的开启或者关闭,默认值为1表示开启,0表示关闭
	private String property = null;
	private String fromTimeValue=null;//代表属性name": "fromTime"的value
	private String fromTime=null;//代表属性name": "fromTime"的suggestDatetime
	private String toTimeValue=null;//代表属性name": "toTime"的value
	private String toTime=null;//代表属性name": "toTime"的suggestDatetime
	private String toPlace=null;
	public AlarmClockDBHelper alarmdbHelper;
	public AlarmDBHandler alarmDBHandler;
	public  SQLiteDatabase adb;

	public AlarmSkill(intent intent) {
		// TODO Auto-generated constructor stub
		super(intent);
		mintent = intent;
		alarmdbHelper = AlarmClockDBHelper.getInstance();//闹钟数据库
		adb = alarmdbHelper.getReadableDatabase();
		alarmDBHandler = AlarmDBHandler.getInstance();
	}

	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		alarmintent = mintent.getSemantic().get(0).getIntent();
		LogUtil.e("alarm", "alarmintent:"+alarmintent+LogUtil.getLineInfo());
		question = mintent.getText();
		answer = mintent.getAnswer().getText();
		state = mintent.getUsed_state().getState();
		LogUtil.e("alarm","state："+state+LogUtil.getLineInfo());
		List<Slot> slots = new ArrayList<>();
		slots = mintent.getSemantic().get(0).getSlots();

		LogUtil.e("alarm","slots.size() ="+slots.size());
		Iterator<Slot> it1 = slots.iterator();
		while (it1.hasNext()) {
			Slot slot = it1.next();
			String name = slot.getName();
			LogUtil.e("alarm","name:"+name+LogUtil.getLineInfo());
			if("repeat".equals(name)){//是否重复
				repeat = slot.getValue();
				LogUtil.e("alarm","repeat:"+repeat+LogUtil.getLineInfo());
			}
			if("content".equals(name)){//提示内容
				content = slot.getValue();
				if(content.equals(null)){
					content="无";
				}
				LogUtil.e("alarm","content:"+content+LogUtil.getLineInfo());
			}
			if("datetime".equals(name)){//设置时间
				String normvalue = slot.getNormValue();
				LogUtil.e("alarm","datetime normvalue:"+normvalue+LogUtil.getLineInfo());
				try {
					//去除斜杠
					JSONObject jsonObject = new JSONObject(normvalue);
					suggestDatetime = jsonObject.getString("suggestDatetime");
					suggestDatetime=suggestDatetime.replace("T", "  ");
				} catch (JSONException e) {
					LogUtil.e("alarm", "解析json出现异常"+LogUtil.getLineInfo(), e);
				}
			}
			if("name".equals(name)){//闹钟类型
				alarmtype = slot.getValue();
				LogUtil.e("alarm","alarmtype:"+alarmtype+LogUtil.getLineInfo());
			}
			if("repeat".equals(name)){
				repeat = slot.getValue();
				LogUtil.e("alarm","repeat:"+repeat+LogUtil.getLineInfo());
			}
			if("property".equals(name)){
				property = slot.getValue();
				LogUtil.e("alarm","property:"+property+LogUtil.getLineInfo());
			}
			if("fromTime".equals(name)){
				String fromTimeNormValue=slot.getNormValue();
				fromTimeValue=slot.getValue();
				try {
					JSONObject jsonObject = new JSONObject(fromTimeNormValue);
					suggestDatetime = jsonObject.getString("suggestDatetime");
					suggestDatetime=suggestDatetime.replace("T", "  ");
					LogUtil.e("alarm","suggestDatetime:"+suggestDatetime+LogUtil.getLineInfo());
					fromTime=suggestDatetime;
					LogUtil.e("alarm","fromTime:"+fromTime+LogUtil.getLineInfo());
				} catch (JSONException e) {
					LogUtil.e("alarm", "解析json出现异常"+LogUtil.getLineInfo(), e);
				}
			}
			
			if("toTime".equals(name)){
				String toTimeNormValue=slot.getNormValue();
				toTimeValue=slot.getValue();
				LogUtil.e("alarm","toTimeValue:"+toTimeValue+LogUtil.getLineInfo());
				try {
					JSONObject jsonObject = new JSONObject(toTimeNormValue);
					suggestDatetime = jsonObject.getString("suggestDatetime");
					suggestDatetime=suggestDatetime.replace("T", "  ");
					toTime=suggestDatetime;
					LogUtil.e("alarm","toTime:"+toTime+LogUtil.getLineInfo());
					LogUtil.e("alarm","suggestDatetime:"+suggestDatetime+LogUtil.getLineInfo());
				} catch (JSONException e) {
					LogUtil.e("alarm", "解析json出现异常"+LogUtil.getLineInfo(), e);
				}
			}
			if("toPlace".equals(name)){
				toPlace=slot.getValue();
			}
		}
		LogUtil.e("alarm",LogUtil.getLineInfo());
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		String[] params;
		try {
			params = alarmClockDispose(alarmintent);
			LogUtil.e("alarm", "params[0]"+params[0]+LogUtil.getLineInfo());
			LogUtil.e("alarm", "params[1]"+params[1]+LogUtil.getLineInfo());
			mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
				@Override
				public void doSomethingsAfterTts() {
					// TODO Auto-generated method stub
					recoveryPlayerState();
				}
			}, params[0],  params[1]);
			Intent intent = new Intent("com.szhklt.activity.AlarmListActivity.UPDATEALARMLIST");
			context.sendBroadcast(intent);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/******************************************/
	public String[] alarmClockDispose(String alarmintent) throws Exception{
		/*
		 * 排除异常情况
		 */
		if(state.contains("clockUnfinished")){//闹钟未设置时间
			LogUtil.e("alarm","闹钟还未设置时间!"+LogUtil.getLineInfo());
			String [] params={"请设置准确的时间和事件",question};
			LogUtil.e("alarm", params[0]+"        "+params[1]+LogUtil.getLineInfo());
			return params;
		}else if(state.contains("reminderUnfinished1")){//提醒未设置时间
			LogUtil.e("alarm","提醒还未设置时间!"+LogUtil.getLineInfo());
			String [] params={"请设置准确的时间和事件",question};
			LogUtil.e("alarm", params[0]+"        "+params[1]+LogUtil.getLineInfo());
			return params;
		}else if(state.contains("reminderUnfinished0")){//提醒未设置事件
			String used_state=null;
//			try {
//				JSONObject xftextJson = new JSONObject(MainService.xftext);
//				used_state=xftextJson.getJSONObject("used_state").toString();
//			} catch (JSONException e) {
//			LogUtil.e("alarm", "json解析错误", e);
//			}
			LogUtil.e("alarm","used_state:"+used_state+LogUtil.getLineInfo());
			//used_state字段中有datetime.INTERVAL表示reminderUnfinished0并且进入多轮对话
			if(used_state.contains("datetime.INTERVAL")){//提醒未设置事件和精确时间
				LogUtil.e("alarm","提醒还未设置事件和精确时间!"+LogUtil.getLineInfo());
				String [] params={"请设置准确的时间和事件",question};
				LogUtil.e("alarm", params[0]+"        "+params[1]+LogUtil.getLineInfo());
				return params;
			}
			if(used_state.contains("datetime.date")){//提醒未设置事件和精确时间
				LogUtil.e("alarm","提醒还未设置事件和精确时间!"+LogUtil.getLineInfo());
				String [] params={"请设置准确的时间和事件",question};
				LogUtil.e("alarm", params[0]+"        "+params[1]+LogUtil.getLineInfo());
				return params;
			}
		}
		/*
		 * 修改闹钟
		 */
		if("CHANGE".equals(alarmintent)){
			LogUtil.e("alarm","修改闹钟"+LogUtil.getLineInfo());
			if(fromTime==null||toTime==null){
				String[] params={"您可以这样说,修改八点起床的闹钟到九点",question};
				return params;
			}
			LogUtil.e("alarm", "fromTime:"+fromTime+"toTime:"+toTime+LogUtil.getLineInfo());
			if(content!=null){
				Cursor cursor = adb.query("alarmlist", null, "datetime="+"\""+fromTime+"\" and content="+"\""+content+"\"" , null, null, null, null, null);
				if(cursor.getCount()==0){
					String[] params={"别骗我哟,您还没有设置"+fromTimeValue+content+"的闹钟呢！",question};
					return params;
				}else if(cursor.getCount()!=0){
					ArrayList<Integer> ids=new ArrayList<>();
					while(cursor.moveToNext()){//1.根据id删除系统闹钟
						int id=cursor.getInt(cursor.getColumnIndex("_id"));
						ids.add(id);
						deleteAlarm(id);
					}
					//2.根据id修改数据库
					ContentValues values=new ContentValues();
					values.put("datetime", toTime);
					if(toPlace!=null){
						values.put("content", toPlace);
					}
					for (Integer id : ids) {//根据id更新所有符合条件的闹钟
						adb.update("alarmlist", values, "_id="+"\""+id+"\"", null);
					}
					//3.根据id查询修改后的数据，然后定系统闹钟
					for (Integer id : ids) {
						LogUtil.e("alarm", "查询id"+id+LogUtil.getLineInfo());
						addAlarm(toTime, id, alarmtype, content, repeat, 1);
					}
					ids.clear();
					String[] params={"修改成功!",question};
					return params;

				}
			}else if(content==null){
				Cursor cursor = adb.query("alarmlist", null, "datetime="+"\""+fromTime+"\"" , null, null, null, null, null);
				if(cursor.getCount()==0){
					LogUtil.e("alarm","对不起,您还没有"+fromTimeValue+"的闹钟"+LogUtil.getLineInfo());
					String[] params={"别骗我哟,您还没有设置"+fromTimeValue+"的闹钟呢！",question};
					return params;
				}else if(cursor.getCount()!=0){
					ArrayList<Integer> ids=new ArrayList<>();
					while(cursor.moveToNext()){//1.根据id删除系统闹钟
						int id=cursor.getInt(cursor.getColumnIndex("_id"));
						ids.add(id);
						deleteAlarm(id);
					}
					//2.根据id修改数据库
					ContentValues values=new ContentValues();
					values.put("datetime", toTime);
					if(toPlace!=null){
						values.put("content", toPlace);
					}else{
						values.put("content", "无");
					}
					for (Integer id : ids) {//根据id更新所有符合条件的闹钟
						adb.update("alarmlist", values, "_id="+"\""+id+"\"", null);
					}
					//3.根据id查询修改后的数据，然后定系统闹钟
					for (Integer id : ids) {
						addAlarm(toTime, id, alarmtype, content, repeat, 1);
					}
					ids.clear();
					String[] params={"修改成功!",question};
					return params;
				}
			}
		}

		//删除闹钟
		if("CANCEL".equals(alarmintent)){
			LogUtil.e("alarm","删除闹钟"+LogUtil.getLineInfo());
			Cursor cursor = adb.query("alarmlist",null,null,null,null,null,null);
			if(cursor.moveToFirst()){
				do {
					if("all".equals(property)){
						int willDeleteId = cursor.getInt(cursor.getColumnIndex("_id"));
						deleteAlarm(willDeleteId);
						continue;
					}
					String cursordatetime = cursor.getString(cursor.getColumnIndex("datetime"));
					String cursoralarmtype = cursor.getString(cursor.getColumnIndex("alarmtype"));
					String cursorcontent = cursor.getString(cursor.getColumnIndex("content"));
					String cursorrepeat = cursor.getString(cursor.getColumnIndex("repeat"));
					if(cursordatetime.equals(suggestDatetime)){
						if(cursoralarmtype.equals(alarmtype)){
							if(cursorcontent.equals(content == null?"null":content)){
								if(cursorrepeat.equals(repeat == null?"null":repeat)){
									int cursorid = cursor.getInt(cursor.getColumnIndex("_id"));
									LogUtil.e("alarm","删除的闹钟的id为:"+cursorid+LogUtil.getLineInfo());
									deleteAlarm(cursorid);
									LogUtil.e("alarm","**********在系统中删除闹钟成功*********"+LogUtil.getLineInfo());
									break;
								}
							}
						}  
					}
				} while (cursor.moveToNext());
			}
			alarmDBHandler.delete(adb,suggestDatetime, alarmtype, content, repeat);
			if("all".equals(property)){
				LogUtil.e("alarm","删除表内全部数据!!!"+LogUtil.getLineInfo());
				adb.execSQL("delete from alarmlist");
			}
			String [] params={answer,question};
			return params;
		}
		/*
		 * 添加闹钟
		 */
		if("CREATE".equals(alarmintent)){
			LogUtil.e("alarm","添加闹钟"+LogUtil.getLineInfo());
			int id = new Random().nextInt(1000000000);//限制到十亿以内
			//存入数据库
			alarmDBHandler.add(adb,id, suggestDatetime, alarmtype, content, repeat,alarmstate);
			addAlarm(suggestDatetime, id, alarmtype, content, repeat, alarmstate);
			String [] params={answer,question};
			return params;
		}/*
		 * 查看闹钟
		 */
		if("VIEW".equals(alarmintent)){
			Context context=MainApplication.getContext();
			Intent intent = new Intent(context, AlarmListActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK );
			context.startActivity(intent);
			String[] params={"已为您打开闹钟列表页面!",question};
			return params;
		}
		String[] params={"您是想使用闹钟吗?您可以说:帮我定一个九点的闹钟",question};
		return params;
	}

	/**
	 * 在系统中增加闹钟
	 * @param datetime
	 * @param id
	 * @param alarmtype
	 * @param content
	 * @param repeat
	 * @param state
	 */
	@SuppressLint("SimpleDateFormat") 
	public static void addAlarm(final String datetime,final int id,final String alarmtype,final String content,final String repeat,final int state){
		LogUtil.e("alarm","在系统中添加闹钟!"+LogUtil.getLineInfo());
		new Thread(new Runnable() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				long timeDifference = 0;//时间差
				LogUtil.e("alarm","suggestDatetime:"+datetime+LogUtil.getLineInfo());
				LogUtil.e("alarm","id:"+id+LogUtil.getLineInfo());
				LogUtil.e("alarm","alarmtype:"+alarmtype+LogUtil.getLineInfo());
				LogUtil.e("alarm","content:"+content+LogUtil.getLineInfo());
				LogUtil.e("alarm","repeat:"+repeat+LogUtil.getLineInfo());
				LogUtil.e("alarm","state:"+state+LogUtil.getLineInfo());
				//解析时间
				String curNetTimeStr = HkAlarmClock.getNetTime();//当前时间
				LogUtil.e("alarm","当前网络时间为:"+curNetTimeStr+LogUtil.getLineInfo());
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				Date fromDate;
				Date toDate = null;
				try {
					fromDate = sdf.parse(curNetTimeStr);
					toDate = sdf.parse(datetime.replace("T"," "));
					timeDifference = toDate.getTime() - fromDate.getTime();
					LogUtil.e("alarm","时间差为:"+timeDifference+LogUtil.getLineInfo());
					if(timeDifference<=0){
						LogUtil.e("alarm", "时间差小于等于0，不设置闹钟"+LogUtil.getLineInfo());
						return;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} 
				Intent intent = new Intent("android.alarm.alarmclock.MainService");
				LogUtil.e("alarm","id:"+id+LogUtil.getLineInfo());
				intent.putExtra("id",id);//随机产生id
				intent.putExtra("datetime",datetime);
				intent.putExtra("alarmtype",alarmtype);

				String content1 = content;
				boolean isrepeat=false;
				if("无".equals(content1)){//在读取数据库进行系统闹钟设置时会有用
					content1=null;
				}
				intent.putExtra("content",content1);
				LogUtil.e("alarm", "闹钟state:"+state+LogUtil.getLineInfo());
				intent.putExtra("state",state);
				if("null".equals(repeat)||repeat==null){//在读取json语义进行系统闹钟设置时会有用
					intent.putExtra("repeat","单次");
					isrepeat=false;
				}else{
					intent.putExtra("repeat",repeat);
					isrepeat=true;
				}
				if(MainApplication.firmwareVersion.contains("pmu_IRQ")){//如果是xiaoba_ac108_v1.8_pmu_IRQ_wakeup 这个版本或者更高，就用setAlarmClock设置闹钟
					setAlarmClock(id, state, toDate.getTime(), 1000*60*60*24,isrepeat, content1);
				}else{

					PendingIntent sender = PendingIntent.getBroadcast(context, id,intent, PendingIntent.FLAG_CANCEL_CURRENT);
					if(repeat == null||repeat.equals("单次")){//设置单次闹钟
						LogUtil.e("alarm","设置单次闹钟!!!"+LogUtil.getLineInfo());
						AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
						int interval = (int) intent.getLongExtra("intervalMillis",0);
						if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
							am.setWindow(AlarmManager.RTC_WAKEUP,
									System.currentTimeMillis()+timeDifference,
									interval,
									sender);
						}
					}else if("EVERYDAY".equals(repeat)){//设置重复闹钟
						LogUtil.e("alarm","设置重复闹钟!!!"+LogUtil.getLineInfo());
						AlarmManager am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
						am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+timeDifference,24*60*60*1000,sender);
					}
				}

			}
		}).start();
	}
	
	/**
	 * 在系统中删除闹钟
	 */
	public static void deleteAlarm(int id){
		LogUtil.e("alarm","在系统中删除闹钟!"+LogUtil.getLineInfo());
		Intent intent = new Intent("android.alarm.alarmclock.MainService");
		PendingIntent pi = PendingIntent.getBroadcast(context, id, intent, PendingIntent.FLAG_CANCEL_CURRENT);
		AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		am.cancel(pi);
	}
	
	public static void resetAlarmClock(){
		LogUtil.e("alarm","网络重连之后重新读取数据库设置闹钟!"+LogUtil.getLineInfo());
		SQLiteDatabase adb=AlarmClockDBHelper.getInstance().getReadableDatabase();
		Cursor cursor=adb.query("alarmlist",  null, null, null, null, null, null);
		if(cursor.moveToFirst()){
			do{
				LogUtil.e("alarm","重新设置闹钟"+LogUtil.getLineInfo());
				int resetid = cursor.getInt(cursor.getColumnIndex("_id"));
				String resetDatetime = cursor.getString(cursor.getColumnIndex("datetime"));
				String resetalarmtype = cursor.getString(cursor.getColumnIndex("alarmtype"));
				String resetcontent = cursor.getString(cursor.getColumnIndex("content"));
				String resetrepeat = cursor.getString(cursor.getColumnIndex("repeat"));
				int resetstate=cursor.getInt(cursor.getColumnIndex("state"));
				addAlarm(resetDatetime, resetid, resetalarmtype, resetcontent, resetrepeat, resetstate);
			}while(cursor.moveToNext());
		}
	}
	
	/**
	 * 用于设置可开机的闹钟（注意：当isrepeat参数为false时，代表单次闹钟，intervalMillis没有意义）
	 * 
	 * @param id 闹钟id
	 * @param flags May be FLAG_ONE_SHOT, FLAG_NO_CREATE, FLAG_CANCEL_CURRENT, FLAG_UPDATE_CURRENT, or any of 
	 the flags as supported by Intent.fillIn() to control which unspecified parts of the intent that can be supplied when 
	 the actual send happens.
	 * @param triggerAtMillis 目标时间
	 * @param intervalMillis
	 * @param isrepeat
	 * @param content
	 */
	public static void setAlarmClock(int id,int flags,long triggerAtMillis,long intervalMillis,boolean isrepeat,String content){
		LogUtil.e("alarm", "setAlarmClock()设置可以开机的闹钟"+LogUtil.getLineInfo());
		Intent intent = new Intent("com.szhklt.shutdownwakeup");
		intent.putExtra("action", "android.alarm.alarmclock.MainService");
		intent.putExtra("requestCode", id);
		intent.putExtra("flags", flags);
		intent.putExtra("triggerAtMillis", triggerAtMillis);
		intent.putExtra("isrepeat", isrepeat);
		intent.putExtra("tag", content);
		if(isrepeat){
			intent.putExtra("intervalMillis", intervalMillis);
		}
		LogUtil.e("alarm", "requestCode:"+id+"flags:"+flags+"triggerAtMillis:"+triggerAtMillis+"isrepeat:"+isrepeat+"tag:"+content+LogUtil.getLineInfo());
		context.sendBroadcast(intent);
	}
}
