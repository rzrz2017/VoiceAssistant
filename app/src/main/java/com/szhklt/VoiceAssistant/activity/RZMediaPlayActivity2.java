package com.szhklt.VoiceAssistant.activity;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.rich.czlylibary.bean.MusicInfo;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.adapter.SuperAdapter.ViewHolder;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.RzMusicPkg.MediaPlayerWrapper;
import com.szhklt.VoiceAssistant.RzMusicPkg.RzMediaDownloader.RzMediaDownloadListener;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.RzMusicPkg.RzMediaDownloader;
import com.szhklt.VoiceAssistant.RzMusicPkg.RzMusicLab;
import com.szhklt.VoiceAssistant.adapter.SuperAdapter;
import com.szhklt.VoiceAssistant.beam.Result;
import com.szhklt.VoiceAssistant.multithreadeddownloader.MyDownloadManager;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.view.CircleProgress;
import com.szhklt.VoiceAssistant.view.SlipTextView;

import me.wcy.lrcview.LrcView;

public class RZMediaPlayActivity2 extends Activity implements OnClickListener{
	private static String TAG = "RZMediaPlayActivity2";
	public static String ACTION_FINISH = "android.rzmediaplayact.action.FINISH";
	public static String ACTION_LOAD_DATA = "android.rzmediaplayact.action.LOAD_DATA";
	public static String ACTION_PLAY = "android.rzmediaplayact.action.PLAY";
	public static String ACTION_PAUSE = "android.rzmediaplayact.action.PAUSE";
	public static String ACTION_NEXT = "android.rzmediaplayact.action.NEXT";
	public static String ACTION_PREV = "android.rzmediaplayact.action.PREV";
	
	public static int MESSAGE_DETAILED_LOAD = 8;
	public static int MESSAGE_DETAILED_NEXT = 9;
	public static int MESSAGE_DETAILED_PREV= 10;
	public static int MESSAGE_LOAD_LRC= 11;
	
	private static ListView mediaListView;
	private CircleProgress circleProgress;
	private ImageView mediaToggle;
	private ImageView preImage;
	private ImageView nextImage;
	private LrcView lrcview;
	private LinearLayout searchLinear;

//	private CircleImageView circleImageView;
	private ImageView circleImageView;
	private ImageView lrcSwitch;

	private SeekBar seekbar;
	private SlipTextView titleTextView;
	private TextView totaltime,curtimer;
	StringBuffer totalText;
	StringBuffer curtimerText;
	public int durSec = 0;
	private IntentFilter intentFilter;
	private SuperAdapter<Result> myAdapter;
	private String service;
	private RzMediaDownloader<Result> mRzMediaDownloader;
	private Handler responseHandler;
	private MediaPlayReceiver mediaPlayReceiver;
	private MyDownloadManager downloadManager;
	public static int raInt; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LogUtil.e(TAG,"onCreate()");
		setContentView(R.layout.activity_rzmediaplay);
		sendBroadcast(new Intent("android.bluetooth.action.FINISH"));//停止蓝牙推送
		responseHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
//				// TODO Auto-generated method stub
				LogUtil.e("dskg","responseHandler handleMessage:"+LogUtil.getLineInfo());
				if(msg.what == MESSAGE_DETAILED_LOAD) {
					if (myAdapter == null) {
						myAdapter = new SuperAdapter<Result>(null, R.layout.view_item_play_medie) {
							@Override
							public void bindView(ViewHolder holder, Result obj) {
								// TODO Auto-generated method stub
								setHolderByMediaType(holder, obj);
							}
						};
						mediaListView.setAdapter(myAdapter);
					}
					RzMusicLab.get().addRzMusic((Result) msg.obj);
					myAdapter.add((Result) msg.obj);

					//播放
					if (RzMusicLab.get().isCurData((Result) msg.obj)) {
						Result obj = (Result) msg.obj;
						myAdapter.notifyDataSetChanged();
						myAdapter.setSelectItem(RzMusicLab.get().getRzMusics().indexOf(obj));
						loadImage((Result)msg.obj);

						mediaToggle.setVisibility(View.GONE);
						circleProgress.setVisibility(View.VISIBLE);
						circleProgress.startAnim();
						if (msg.arg1 == 1){
							//带语音播报
							mRzMediaDownloader.queueRzMedia(obj,true);
						}else{
							//不带语音播报
							mRzMediaDownloader.queueRzMedia(obj,false);
						}
					}
				}else if(msg.what == MESSAGE_DETAILED_NEXT){
					next();
				}else if(msg.what == MESSAGE_DETAILED_PREV){
					prev();
				}else if(msg.what == MESSAGE_LOAD_LRC){//加载歌词
					lrcview.loadLrc(getLrcText("currentfile.lrc"));
				}
			}
		};
		
		mRzMediaDownloader = new RzMediaDownloader<>(this,responseHandler);
		mRzMediaDownloader.start();
		mRzMediaDownloader.getLooper();
		mRzMediaDownloader.setmRzMediaDownloadListener(new RzMediaDownloadListener<Result>() {

			@Override
			public void onRzMediaPrepared(MediaPlayerWrapper mediaPlayer) {
				// TODO Auto-generated method stub
				//更新左边界面UI
				circleProgress.reset();
				circleProgress.setVisibility(View.GONE);
				mediaToggle.setVisibility(View.VISIBLE);
				if(service == null){
					titleTextView.setText(mediaPlayer.getTmp().getName());
				}else if("news".equals(service)){
					titleTextView.setText(mediaPlayer.getTmp().getTitle());
				}else{
					titleTextView.setText(mediaPlayer.getTmp().getName());
				}
				
				if("pause".equals(mediaToggle.getTag().toString())){
					mediaToggle.setTag("play");
					mediaToggle.setImageResource(R.drawable.ic_pause);
				}

				int dur = mediaPlayer.getDuration();
				if((dur/1000)%60 < 10){
					totalText = new StringBuffer((dur/1000)/60+":0"+(dur/1000)%60);
				}else{
					totalText = new StringBuffer((dur/1000)/60+":"+(dur/1000)%60);
				}
				totaltime.setText(totalText);
				durSec = (dur/1000);
				seekbar.setMax(durSec);
				myAdapter.setSelectItem(RzMusicLab.get().getRzMusics().indexOf(mediaPlayer.getTmp()));
			}

			@Override
			public void onRzMediaCurPos(int pos) {
				// TODO Auto-generated method stub
				seekbar.setProgress((pos/1000));
				if(lrcview.getVisibility() == View.VISIBLE){
					lrcview.updateTime((long)pos);
				}
				if((pos/1000)%60 < 10){
					curtimerText = new StringBuffer((pos/1000)/60+":0"+(pos/1000)%60);
				}else{
					curtimerText = new StringBuffer((pos/1000)/60+":"+(pos/1000)%60);
				}
				curtimer.setText(curtimerText);
			}

			@Override
			public void onRzMediaSecondaryProgress(int percent) {
				// TODO Auto-generated method stub
				LogUtil.e("mediaplayerlistener","媒体缓存百分比:"+percent+"%");
				seekbar.setSecondaryProgress(seekbar.getMax()*percent/100);
			}
		});
		
		intentFilter = new IntentFilter();
		intentFilter.addAction(ACTION_FINISH);
		intentFilter.addAction(ACTION_LOAD_DATA);
		intentFilter.addAction(ACTION_NEXT);
		intentFilter.addAction(ACTION_PREV);
		intentFilter.addAction(ACTION_PAUSE);
		intentFilter.addAction(ACTION_PLAY);
		intentFilter.addAction("android.rzmediaplayact.action.OTHER_ACTION");
		intentFilter.addAction("android.rzmediaplayer.action.PLAYER_ACTION");
		mediaPlayReceiver = new MediaPlayReceiver();
		registerReceiver(mediaPlayReceiver, intentFilter);
		initView();
		
		if(getIntent().getSerializableExtra("media_data") != null){
			loadData((intent)getIntent().getSerializableExtra("media_data"));
			return;
		}
		
		if(getIntent().getStringExtra("kg_data") != null){
			loadDataFromReceiver(getIntent().getStringExtra("kg_data"),
					 getIntent().getIntExtra("kg_temp",0),
					 new Random().nextInt());
		}

		if(getIntent().getSerializableExtra("cocheer") != null){
			String singer = getIntent().getStringExtra("singer");
			LogUtil.e("singer",singer+LogUtil.getLineInfo());
			loadCocheer((List<Result>)getIntent().getSerializableExtra("cocheer"),singer);
		}

		if(getIntent().getSerializableExtra("migu") != null){
			loadMiGu((List<MusicInfo>)getIntent().getSerializableExtra("migu"),
					getIntent().getStringExtra("index"),true);
		}
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		KwSdk.getInstance().exit();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		LogUtil.e(TAG,"onNewIntent");
		/********************************************/
		intent newIntent = (intent)intent.getSerializableExtra("media_data");
		if(newIntent != null){
			loadData(newIntent);
			return;
		}
		
		if(intent.getStringExtra("kg_data") != null){
			loadDataFromReceiver(intent.getStringExtra("kg_data"),
					 intent.getIntExtra("kg_temp",0),
					 new Random().nextInt());
		}

		if(intent.getSerializableExtra("cocheer") != null){
			String singer = intent.getStringExtra("singer");
			LogUtil.e("singer",singer+LogUtil.getLineInfo());
			loadCocheer((List<Result>)intent.getSerializableExtra("cocheer"),singer);
		}

		if(intent.getSerializableExtra("migu") != null){
			loadMiGu((List<MusicInfo>)intent.getSerializableExtra("migu"),
					intent.getStringExtra("index"),true);
		}
	}

	public void loadDataFromReceiver(String kgData,int kgTemp,int raInt){
		RZMediaPlayActivity2.raInt = raInt;
		final JSONArray mJSONArray;
		try {
			RzMusicLab.get().getRzMusics().clear();
			if(myAdapter != null){
				myAdapter.clear();
			}
			
			mJSONArray = new JSONArray(kgData);
			for(int i = 0;i < mJSONArray.length();i++){
				JSONObject mJSONObject1=(JSONObject) mJSONArray.get(i);
				final String name = mJSONObject1.optString("name");
				final String auther = mJSONObject1.optString("singer");
				
				if(mRzMediaDownloader != null){
					if(i == kgTemp){
						RzMusicLab.get().setCurName(name);
						RzMusicLab.get().setCurAuthor(auther);
					}
					String[] msg = {name,auther};
					//请求加载歌曲
					mRzMediaDownloader.queueLoadURL(msg, raInt);
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 加载酷旗
	 * @param resultList
	 */
	public void loadCocheer(List<Result> resultList,String singer){
		Result obj = null;

		RzMusicLab.get().clear();
		if(myAdapter != null){
			myAdapter.clear();
		}

		for(int i = 0;i < resultList.size();i++){
            if (myAdapter == null) {
                myAdapter = new SuperAdapter<Result>(null, R.layout.view_item_play_medie) {
                    @Override
                    public void bindView(ViewHolder holder, Result obj) {
                        // TODO Auto-generated method stub
                        setHolderByMediaType(holder, obj);
                    }
                };
                mediaListView.setAdapter(myAdapter);
            }
            RzMusicLab.get().addRzMusic(resultList.get(i));
            LogUtil.e(TAG,resultList.get(i).toString());
            myAdapter.add(resultList.get(i));
            if(singer != null){
            	if(resultList.get(i).getAuthor().equals(singer)){
            		obj = resultList.get(i);
				}
			}

		}
		playMedia(obj == null?resultList.get(0):obj);
	}

	/**
	 * 加载咪咕
	 * @param resultList
	 */
	public void loadMiGu(List<MusicInfo> resultList,String scg,boolean isPrompt){
		LogUtil.e(TAG,"loadMiGu()"+LogUtil.getLineInfo());
		RzMusicLab.get().clear();
		if(myAdapter != null){
			myAdapter.clear();
		}

		if(scg.equals("random")){
			//选定歌曲
			int index = new Random().nextInt(resultList.size());
			RzMusicLab.get().setCurName(resultList.get(index).getMusicName());
			RzMusicLab.get().setCurAuthor(resultList.get(index).getSingerName());
		}
		if(isNumeric(scg)){
			int index = Integer.valueOf(scg);
			RzMusicLab.get().setCurName(resultList.get(index).getMusicName());
			RzMusicLab.get().setCurAuthor(resultList.get(index).getSingerName());
		}

		for(int i = 0;i < resultList.size();i++){
			if(mRzMediaDownloader != null){
				String[] msg = new String[4];
				msg[0] = resultList.get(i).getMusicName();
				LogUtil.e(TAG,msg[0]);
				msg[1] = resultList.get(i).getSingerName();
				LogUtil.e(TAG,msg[1]);
				msg[2] = resultList.get(i).getMusicId();
				LogUtil.e(TAG,msg[2]);
				if(isPrompt){
					msg[3] = "true";
					LogUtil.e(TAG,msg[3]);
				}else{
					msg[3] = "false";
				}

				//请求加载歌曲
				mRzMediaDownloader.queueLoadMiGuURL(msg);
			}
		}

//		for(int i = 0;i < resultList.size();i++){
//			if (myAdapter == null) {
//				myAdapter = new SuperAdapter<Result>(null, R.layout.view_item_play_medie) {
//					@Override
//					public void bindView(ViewHolder holder, Result obj) {
//						// TODO Auto-generated method stub
//						setHolderByMediaType(holder, obj);
//					}
//				};
//				mediaListView.setAdapter(myAdapter);
//			}
//			RzMusicLab.get().addRzMusic(resultList.get(i));
//			LogUtil.e(TAG,resultList.get(i).toString());
//			myAdapter.add(resultList.get(i));
//		}
//		playMedia(obj == null?resultList.get(0):obj);
	}


	/**
	 * 播放媒体资源
	 * @param obj
	 */
	private void playMedia(Result obj){
		LogUtil.e(TAG,"playMedia()"+LogUtil.getLineInfo());
		myAdapter.notifyDataSetChanged();
		LogUtil.e(TAG,"SelectItem:"+RzMusicLab.get().getRzMusics().indexOf(obj)	);
		myAdapter.setSelectItem(RzMusicLab.get().getRzMusics().indexOf(obj));
		loadImage(obj);
		mediaToggle.setVisibility(View.GONE);
		circleProgress.setVisibility(View.VISIBLE);
		circleProgress.startAnim();
		mRzMediaDownloader.queueRzMedia(obj,false);
	}

	/**
	 * 初始化view
	 */
	private void initView(){
		LogUtil.e(TAG,"initView()");
		circleProgress = (CircleProgress)findViewById(R.id.progress);
		circleProgress.startAnim();
		
		mediaListView = (ListView)findViewById(R.id.play_list_rz);
		mediaListView.setOnItemClickListener(itemClicklistener);
		
		seekbar = (SeekBar)findViewById(R.id.seekBar);
		seekbar.setOnSeekBarChangeListener(mediaSeekBarChangeListener);
		
		mediaToggle = (ImageView)findViewById(R.id.media_toggle);
		mediaToggle.setOnClickListener(this);
		mediaToggle.setVisibility(View.GONE);
		
		preImage = (ImageView)findViewById(R.id.prev);
		preImage.setOnClickListener(this);
		
		nextImage = (ImageView)findViewById(R.id.next);
		nextImage.setOnClickListener(this);

		searchLinear = (LinearLayout)findViewById(R.id.search_layout);
		searchLinear.setOnClickListener(this);

		titleTextView = (SlipTextView)findViewById(R.id.text_media_name);
		
		totaltime = (TextView)findViewById(R.id.totaltime);
		curtimer = (TextView)findViewById(R.id.curtime);
		circleImageView = findViewById(R.id.media_civ);
//		circleImageView.setBorderColor(Color.argb(255, 0, 0, 0));
		circleImageView.setImageResource(R.drawable.ic_playing);

		lrcview = (LrcView)findViewById(R.id.lrc_view);
		lrcview.setOnPlayClickListener(new LrcView.OnPlayClickListener() {
			@Override
			public boolean onPlayClick(long time) {
				mRzMediaDownloader.queueSetSeekBar((int) time);
				seekbar.setProgress((int)time);
				if(!mRzMediaDownloader.isPlaying()){
					mRzMediaDownloader.queuePlayMedia();

				}
				return true;
			}
		});

		lrcSwitch = (ImageView) findViewById(R.id.lrc_switch);
		lrcSwitch.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(lrcview.getVisibility() == View.VISIBLE){
					lrcview.setVisibility(View.GONE);
					circleImageView.setVisibility(View.VISIBLE);
				}else{
					lrcview.setVisibility(View.VISIBLE);
					circleImageView.setVisibility(View.GONE);
				}
			}
		});
	}
	
	private OnItemClickListener itemClicklistener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			LogUtil.e(TAG,"position:"+position+LogUtil.getLineInfo());
			loadImage(RzMusicLab.get().getRzMusics().get(position));
			mediaToggle.setVisibility(View.GONE);
			circleProgress.setVisibility(View.VISIBLE);
			circleProgress.startAnim();
			mRzMediaDownloader.queueRzMedia(RzMusicLab.get().getRzMusics().get(position),false);
			myAdapter.setSelectItem(position);
			myAdapter.notifyDataSetChanged();
		}
	};
	
	/**
	 * 初始化数据
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void loadData(intent intent){
		LogUtil.e(TAG,"initData()");
		if(intent == null){
			LogUtil.e(TAG,"intent == null"+LogUtil.getLineInfo());
			return;
		}
		service = intent.getService();
		
		RzMusicLab mRzMusicLab = RzMusicLab.get();
		List<Result> dataList = mRzMusicLab.getRzMusics();
		dataList.clear();
		dataList = intent.getData().getResult();
		mRzMusicLab.setRzMusics(dataList);
		
		if(myAdapter == null){
			myAdapter = new SuperAdapter<Result>((ArrayList)dataList,R.layout.view_item_play_medie){
				@Override
				public void bindView(ViewHolder holder,Result obj) {
					// TODO Auto-generated method stub
					setHolderByMediaType(holder, obj);
				}
			};
			mediaListView.setAdapter(myAdapter);
		}else{
			myAdapter.clear();
			myAdapter.addAll(dataList);
		}
		
		myAdapter.setSelectItem(0);
		loadImage(dataList.get(0));
		mediaToggle.setVisibility(View.GONE);
		circleProgress.setVisibility(View.VISIBLE);
		circleProgress.startAnim();
		mRzMediaDownloader.queueRzMedia(dataList.get(0),false);
	}
	
	/**
	 * 设置视图通过多媒体类型 
	 */
	public void setHolderByMediaType(ViewHolder holder, Result obj){
		//区分媒体种类
		if("story".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getCategory());
		}else if("news".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getTitle());
			holder.setText(R.id.tv_media_auther, obj.getKeyWords());
		}else if("crossTalk".equals(service)){//相声,小品
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getActor());
		}else if("drama".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getCategory());
		}else if("history".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getAlbum());
		}else if("storyTelling".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getTitle());
			holder.setText(R.id.tv_media_auther, obj.getActor());
		}else if("radio".equals(service)){
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getCategory());
		}else{//酷狗dslauncher
			holder.setText(R.id.tv_media_name,obj.getName());
			holder.setText(R.id.tv_media_auther, obj.getAuthor());
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		LogUtil.e(TAG,"onDestroy()");
		RzMusicLab.get().clear();
		mRzMediaDownloader.clearQueue();
		mRzMediaDownloader.relesePlayer();
		mRzMediaDownloader.quit();
		responseHandler.removeCallbacksAndMessages(null);
		unregisterReceiver(mediaPlayReceiver);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		LogUtil.e(TAG,"onActivityResult()");
		switch (requestCode){
			case 1:
				if(resultCode == RESULT_OK){
					LogUtil.e(TAG,"resultCode == RESULT_OK");
					//加载搜索界面的音乐
					if(data.getSerializableExtra("migu") != null && data.getSerializableExtra("index") != null){
						loadMiGu((List<MusicInfo>)data.getSerializableExtra("migu"),
								data.getStringExtra("index"),
								false);
					}
				}
				break;
			default:
		}
	}

	/**
	 * 启动自己
	 * @param context
	 * @param data1
	 * @param data2
	 * @param data3
	 */
	public static void actionStart(Context context,String data1,String data2,Serializable data3){
		LogUtil.e(TAG,"actionStart()");
		Intent intent = new Intent(context,RZMediaPlayActivity2.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra("param1",data1);
		intent.putExtra("param2",data2);
		intent.putExtra("media_data",data3);
		LogUtil.e(TAG,"media_data:"+data3);
		context.startActivity(intent);
		intent = null;
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id == R.id.media_toggle){
			toggle();
		}else if(id == R.id.next){
			next();
		}else if(id == R.id.prev){
			prev();
		}else if(id == R.id.search_layout){
			startSearchAct();
		}
	}

	private void startSearchAct(){
		Intent intent = new Intent(this,SearchActivity.class);
		startActivityForResult(intent,1);
	}
	
	private void toggle(){
		if("pause".equals(mediaToggle.getTag().toString())){
			play();
		}else{
			pause();
		}
	}
	
	private void play(){
		mRzMediaDownloader.queuePlayMedia();
		mediaToggle.setTag("play");
		mediaToggle.setImageResource(R.drawable.ic_pause);
	}
	
	private void pause(){
		mRzMediaDownloader.queuePauseMedia();
		mediaToggle.setTag("pause");
		mediaToggle.setImageResource(R.drawable.ic_play);
	}

	/**
	 * 播放下一首
	 */
	private void next(){
		if(myAdapter == null){
			return;
		}
		int pos = myAdapter.getSelectItem();
		if(pos >= myAdapter.getCount()-1){
			Toast.makeText(RZMediaPlayActivity2.this, "已经是最下面了！", Toast.LENGTH_SHORT).show();
			return;
		}
		myAdapter.setSelectItem(pos+1);
		myAdapter.notifyDataSetChanged();
		Result tmp = RzMusicLab.get().getRzMusics().get(pos+1);
		loadImage(tmp);
		mediaToggle.setVisibility(View.GONE);
		circleProgress.setVisibility(View.VISIBLE);
		circleProgress.startAnim();
		mRzMediaDownloader.queueRzMedia(tmp,false);
	}
	
	/**
	 * 播放上一首
	 */
	private void prev(){
		if(myAdapter == null){
			return;
		}
		int pos = myAdapter.getSelectItem();
		if(pos <= 0){
			Toast.makeText(RZMediaPlayActivity2.this, "已经是最上面了！", Toast.LENGTH_SHORT).show();
			return;
		}
		myAdapter.setSelectItem(pos-1);
		myAdapter.notifyDataSetChanged();
		Result tmp = RzMusicLab.get().getRzMusics().get(pos-1);
		loadImage(tmp);
		mediaToggle.setVisibility(View.GONE);
		circleProgress.setVisibility(View.VISIBLE);
		circleProgress.startAnim();
		mRzMediaDownloader.queueRzMedia(tmp,false);
	}
	
	private OnSeekBarChangeListener mediaSeekBarChangeListener = new OnSeekBarChangeListener() {
		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			final int progress = seekBar.getProgress();
			LogUtil.e("seekbar","onStopTrackingTouch()"+"progress:"+progress+LogUtil.getLineInfo());

			mRzMediaDownloader.queueSetSeekBar(progress*1000);
		}
		
		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
			// TODO Auto-generated method stub
			LogUtil.e("seekbar","onStartTrackingTouch()"+LogUtil.getLineInfo());
		}
		@Override
		public void onProgressChanged(SeekBar seekBar, int progress,
				boolean fromUser) {
			// TODO Auto-generated method stub
			LogUtil.e("seekbar","onProgressChanged("+progress+")"+LogUtil.getLineInfo());
		}
	};
	
	private class MediaPlayReceiver extends BroadcastReceiver{

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			String action = intent.getAction();
			if(action.equals(RZMediaPlayActivity2.ACTION_FINISH)){
				finish();
			}
			
			if(action.equals("android.rzmediaplayact.action.OTHER_ACTION")){
			String playerAction = intent.getStringExtra("playeraction");
				if("play".equals(playerAction)){//继续播放
					play();
				}else if("pause".equals(playerAction)){//暂停播放
					pause();
				}else if("stop".equals(playerAction)){//停止播放
					finish();
				}else if("next".equals(playerAction)){//下一首
					next();
				}else if("prev".equals(playerAction)){//上一首
					prev();
				}
			}
			
			if("android.rzmediaplayer.action.PLAYER_ACTION".equals(action)){
				String playerAction = intent.getStringExtra("playeraction");
				if("play".equals(playerAction)){//继续播放
					play();
				}else if("pause".equals(playerAction)){//暂停播放
					pause();
				}else if("stop".equals(playerAction)){//停止播放
					finish();
				}
			}
		}
	} 
	
	/***********************************************************************/
	private void loadImage(Result data){
		String imgUrlStr = null;
		if("news".equals(service)){
			imgUrlStr = data.getImgUrl();
		}else if("radio".equals(service)){
			imgUrlStr = data.getImg();
		}else{
			imgUrlStr = data.getImgUrl();
		}
		LogUtil.e("img","imgUrlStr:"+imgUrlStr+LogUtil.getLineInfo());
		if(imgUrlStr != null){
			Glide.with(MainApplication.getContext())
					.asBitmap()
					.load(imgUrlStr)
					.into(circleImageView);
		}
	}

	private String getLrcText(String fileName) {
		String lrcText = null;
		try {
			File file = new File(Environment.getExternalStorageDirectory(), fileName);
//			InputStream is = getAssets().open(fileName);
			InputStream is = new FileInputStream(file);
			int size = is.available();
			byte[] buffer = new byte[size];
			is.read(buffer);
			is.close();
			lrcText = new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lrcText;
	}


	/**
	 * 检查是否有有有效的媒体数据
	 * @param data
	 * @return
	 */
	public static Boolean checkVaildMediaData(List<Result> data){
		LogUtil.e(TAG,"检查是否有有有效的媒体数据 --"+"checkVaildMediaData()"+LogUtil.getLineInfo());
		for(int i = 0;i < data.size();i++){
			LogUtil.e(TAG,"--"+data.get(i).getName());
			if(data.get(i).getUrl() != null ||
					data.get(i).getPlayUrl()	!= null){
				return true;
			}
		}
		return false;
	}

	public boolean isNumeric(String str){
		for (int i = 0; i < str.length(); i++){
			System.out.println(str.charAt(i));
			if (!Character.isDigit(str.charAt(i)))
			{
				return false;
			}
		}
		return true;
	}

}
