package com.szhklt.www.voiceassistant.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.szhklt.www.voiceassistant.MainApplication;
import com.szhklt.www.voiceassistant.R;
import com.szhklt.www.voiceassistant.db.AlarmClockDBHelper;
import com.szhklt.www.voiceassistant.skill.AlarmSkill;
import com.szhklt.www.voiceassistant.util.LogUtil;
import com.szhklt.www.voiceassistant.view.AlarmListClock;
import com.szhklt.www.voiceassistant.view.DialogDS;
import com.szhklt.www.voiceassistant.view.GuideView;
import com.szhklt.www.voiceassistant.view.RoundButton;

public class AlarmListActivity extends Activity implements OnClickListener{
	public static final String UpdateListAct = "com.szhklt.activity.AlarmListActivity.UPDATEALARMLIST";

	SQLiteDatabase adb;
	ListView listview;
	ImageView imageview;
	AlarmListClock alarmListClock;
	RoundButton addAlarm;

	//存储闹钟数据库alarmlist表中id值
	private ArrayList<Integer> idList=new ArrayList<>();; 
	//定义一个列表集合
	List<Map<String,Object>> listitems=new ArrayList<Map<String,Object>>();
	//定义一个simpleAdapter,供列表使用
	AlarmListViewAdapter myAdapter;


	BroadcastReceiver myReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if(UpdateListAct.equals(intent.getAction())){
				updateListView();
				LogUtil.e("alarm","闹钟列表已更新"+LogUtil.getLineInfo());
			}
		}
	};
	OnItemClickListener onItemClickListener=new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view,
				final int position, long id) {

			DialogDS.Builder builder=new DialogDS.Builder(AlarmListActivity.this);
			//				AlertDialog.Builder builder=new Builder(AlarmListActivity.this);
			builder.setMessage("确定删除此闹钟?");
			//				builder.setTitle("提示");
			builder.setPositiveButton("确定删除", new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog,int which) {
					LogUtil.e("alarm", "position:"+position+LogUtil.getLineInfo());
					Integer id = idList.get(position);
					delete(id);//从数据库中删除数据
					updateListView();//刷新listview
					AlarmSkill.deleteAlarm(id);//从系统中删除数据
					dialog.dismiss();
					Toast.makeText(getBaseContext(), "语音助手:闹钟已删除", Toast.LENGTH_SHORT).show();
				}
			});
			builder.setNegativeButton("不,点错了", new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					LogUtil.e("alarm", "取消删除"+LogUtil.getLineInfo());
				}
			});
			builder.create().show();
		}
	};
	
	private OnClickListener addAlarmClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			Intent intent = new Intent(getBaseContext(), AddAlarmActivty2.class);
			startActivity(intent);
		}
	};
	
	@SuppressLint("HandlerLeak") @Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_alarmlist);
		adb= AlarmClockDBHelper.getInstance().getReadableDatabase();
		//注册广播
		IntentFilter filter = new IntentFilter();
		filter.addAction(UpdateListAct);
		filter.setPriority(Integer.MAX_VALUE);
		registerReceiver(myReceiver, filter);
		listview = (ListView)findViewById(R.id.alarmlist);
		imageview = (ImageView)findViewById(R.id.back);
		addAlarm = (RoundButton)findViewById(R.id.addalarm);
		imageview.setOnClickListener(this);
		alarmListClock =(AlarmListClock)findViewById(R.id.alarmlistcolok);
		String [] columns = {"datetime","content"};
		int [] resourceIds = {R.id.datetime,R.id.content};
		flush();
		myAdapter =new AlarmListViewAdapter(getBaseContext(), listitems, R.layout.list_item_alarm, columns, resourceIds);
		listview.setAdapter(myAdapter);
		//listView单击删除事件
		listview.setOnItemClickListener(onItemClickListener);
//		setGuideView();//开启引导界面
		addAlarm.setOnClickListener(addAlarmClickListener);
	}

	/**
	 * 重写适配器
	 * @author wuhao
	 *
	 */
	public class AlarmListViewAdapter extends SimpleAdapter{
		private LayoutInflater mInflater;

		public AlarmListViewAdapter(Context context, List<? extends Map<String, ?>> data,
				int resource, String[] from, int[] to) {
			super(context, data, resource, from, to);
			this.mInflater = LayoutInflater.from(context);
		}
		@Override
		public int getCount() {
			return listitems.size();
		}
		@Override
		public Object getItem(int position) {
			return null;
		}
		@Override
		public long getItemId(int position) {
			return 0;
		}
		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if(convertView==null){
				holder=new ViewHolder();
				convertView = mInflater.inflate(R.layout.list_item_alarm, null);
				holder.datetime = (TextView) convertView.findViewById(R.id.datetime);
				holder.cutline = (LinearLayout) convertView.findViewById(R.id.cutline);
				holder.content =(TextView) convertView.findViewById(R.id.content);
				holder.alarmstate = (TextView)convertView.findViewById(R.id.alarmstate);
				convertView.setTag(holder);	
			}else{
				holder = (ViewHolder)convertView.getTag();
			}
			holder.datetime.setText((String)listitems.get(position).get("datetime"));
			holder.content.setText((String)listitems.get(position).get("content"));
			holder.alarmstate.setTag(position);
			holder.alarmstate.setTextColor(getAlarmState(idList.get(position))==false?Color.argb(170,232, 212, 99):Color.argb(51,232, 212, 99));
			holder.alarmstate.setText(getAlarmState(idList.get(position))==true?"关闭":"开启");
			holder.alarmstate.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					final Integer id = idList.get(position);
					if(!getAlarmState(id)){
						adb.execSQL("update  alarmlist set state=1 where _id="+id);//改变state的值
						updateListView();
						/*
						 * 在系统中添加闹钟
						 */
						Cursor cursor=adb.query("alarmlist",  null, "_id="+id, null, null, null, null);
						if(cursor.moveToFirst()){
							do{
								 String datetime=cursor.getString(cursor.getColumnIndex("datetime"));
								 String content=cursor.getString(cursor.getColumnIndex("content"));
								 String alarmtype=cursor.getString(cursor.getColumnIndex("alarmtype"));
								 String repeat=cursor.getString(cursor.getColumnIndex("repeat"));
								 int state=cursor.getInt(cursor.getColumnIndex("state"));
								AlarmSkill.addAlarm(datetime, id, alarmtype, content, repeat, state);

							}while(cursor.moveToNext());
						}
						cursor.close();
						LogUtil.e("alarm", "开启成功"+LogUtil.getLineInfo());
						Toast.makeText(getApplicationContext(), "语音助手:闹钟已开启", Toast.LENGTH_SHORT).show();
					}else if(getAlarmState(id)){
						adb.execSQL("update  alarmlist set state=0 where _id="+id);
						updateListView();
						/*
						 * 在系统中删除闹钟
						 */
						AlarmSkill.deleteAlarm(id);
						LogUtil.e("alarm", "关闭成功"+LogUtil.getLineInfo());
						Toast.makeText(getApplicationContext(), "语音助手:闹钟已关闭", Toast.LENGTH_SHORT).show();
					}
				}
			});
			return convertView;
		}
		public  class ViewHolder {
			public TextView datetime;
			public LinearLayout cutline;
			public TextView content;
			public TextView alarmstate;
		}
	}
	private boolean getAlarmState(Integer id) {
		Cursor cursor=adb.query("alarmlist",  new String[]{"state"}, "_id="+id, null, null, null, null);
		int state=0;
		if(cursor.moveToFirst()){
			do{
				state =cursor.getInt(cursor.getColumnIndex("state")) ;
			}while(cursor.moveToNext());
		}
		cursor.close();
		if(state==0){
			return false;
		}else if(state==1){
			return true;
		}
		return true;
	}
	@Override
	protected void onResume() {
		super.onResume();
		updateListView();
		alarmListClock.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		unregisterReceiver(myReceiver);
	}

	/**
	 * 
	 */
	private void updateListView(){
		ListView listView = (ListView)findViewById(R.id.alarmlist);
		SimpleAdapter listaAdapter = (SimpleAdapter)listView.getAdapter();
		idList.clear();
		listitems.clear();
		listaAdapter.notifyDataSetChanged();//刷新列表,很关键,否则会出现删除不成功的情况
		flush();
	}

	public void flush(){
		Cursor cursor = null;
		cursor = adb.query("alarmlist",null, null, null, null, null, null);
		while(cursor.moveToNext()){
			String id=cursor.getString(cursor.getColumnIndex("_id"));
			String datetime=cursor.getString(cursor.getColumnIndex("datetime"));
			datetime=datetime.substring(datetime.indexOf("-")+1);
			String content=cursor.getString(cursor.getColumnIndex("content"));
			Map<String,Object> map=new HashMap<String,Object>();
			map.put("datetime",datetime );
			map.put("content",content );
			listitems.add(map);
			//将id转换为int类型
			int parseInt = Integer.parseInt(id);
			idList.add(parseInt);
			//			LogUtil.e("alarm", "id:"+id);
		}
	}
	public void delete(Integer index){
		adb.execSQL("delete from alarmlist where _id="+index);
	}

	@Override
	public void onClick(View v) {
		finish();
	}
	private GuideView guideView1;
    private GuideView guideView2;
    private GuideView guideView3;
	private void setGuideView() {
		Typeface typeface=Typeface.createFromAsset(MainApplication.getContext().getAssets(),"fonts/digital-7.ttf");
        // 使用图片
//        ImageView iv = new ImageView(this);
//        iv.setImageResource(R.drawable.img_new_task_guide);
//        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//        iv.setLayoutParams(params);

        // 使用文字
        TextView tv1 = new TextView(this);
        tv1.setText("\n\n\n\n\n\n\n在这里您可以\n\n查看当前时间");
        tv1.setTextColor(getResources().getColor(R.color.white1));
        tv1.setTextSize(30);
        tv1.setGravity(Gravity.CENTER);
        tv1.setTypeface(typeface);
        // 使用文字
        TextView tv2 = new TextView(this);
        tv2.setText("\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\t\t\t\t\t\t\t\t\t点击这里可以\n\n\t\t\t\t\t\t\t\t\t删除某个闹钟");
//        tv2.setText("点击这里可以\n\n删除某个闹钟");
        tv2.setTextColor(getResources().getColor(R.color.white1));
        tv2.setTextSize(30);
        tv2.setGravity(Gravity.CENTER);
        tv2.setTypeface(typeface);
        // 使用文字
        TextView tv3 = new TextView(this);
        tv3.setText("点击此处关闭页面");
        tv3.setTextColor(getResources().getColor(R.color.white1));
        tv3.setTextSize(30);
        tv3.setGravity(Gravity.CENTER);
        tv3.setTypeface(typeface);


        guideView1 = GuideView.Builder
                .newInstance(this)
                .setTargetView(alarmListClock)//设置目标
                .setCustomGuideView(tv1)
                .setDirction(GuideView.Direction.RIGHT)
                .setShape(GuideView.MyShape.CIRCULAR)   // 设置圆形显示区域，
                .setBgColor(getResources().getColor(R.color.shadow))
//                .setTargetView(tv)
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                    	guideView1.hide();
                        guideView2.show();
                    }
                })
                .build();


        guideView2 = GuideView.Builder
                .newInstance(this)
                .setTargetView(listview)
                .setCustomGuideView(tv2)
                .setDirction(GuideView.Direction.BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置椭圆形显示区域，setShape(GuideView.MyShape.ELLIPSE)
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView2.hide();
                        guideView3.show();
                    }
                })
                .build();


        guideView3 = GuideView.Builder
                .newInstance(this)
                .setTargetView(imageview)
                .setCustomGuideView(tv3)
                .setDirction(GuideView.Direction.RIGHT_BOTTOM)
                .setShape(GuideView.MyShape.RECTANGULAR)   // 设置矩形显示区域，
                .setRadius(80)          // 设置圆形或矩形透明区域半径，默认是targetView的显示矩形的半径，如果是矩形，这里是设置矩形圆角大小
                .setBgColor(getResources().getColor(R.color.shadow))
                .setOnclickListener(new GuideView.OnClickCallback() {
                    @Override
                    public void onClickedGuideView() {
                        guideView3.hide();
                    }
                })
                .build();

        guideView1.show();
    }
}
