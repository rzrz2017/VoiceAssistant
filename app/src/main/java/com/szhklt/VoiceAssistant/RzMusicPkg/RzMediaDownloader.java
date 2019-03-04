package com.szhklt.VoiceAssistant.RzMusicPkg;

import org.json.JSONException;
import org.json.JSONObject;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import com.google.gson.Gson;
import com.rich.czlylibary.bean.MusicInfo;
import com.rich.czlylibary.bean.MusicinfoResult;
import com.rich.czlylibary.sdk.ResultCallback;
import com.szhklt.VoiceAssistant.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.activity.RZMediaPlayActivity2;
import com.szhklt.VoiceAssistant.beam.Result;
import com.szhklt.VoiceAssistant.beam.Song;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.component.MySynthesizer;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.MiGuSearcher;

public class RzMediaDownloader<T> extends HandlerThread{
	private static final String TAG = "RzMediaDownloader";
	private static final int MESSAGE_LOADMUSIC = 0;//播放音乐
	private static final int MESSAGE_PAUSEMUSIC = 4;
	private static final int MESSAGE_PLAYMUSIC = 5;
	private static final int MESSAGE_SETSEEKBAR = 6;
	private static final int MESSAGE_LOADURL = 7;
	private static final int MESSAGE_LOADMIGUURL = 8;
	
	private Context context;
	private MediaPlayerWrapper player;
	
	private boolean mHasQuit = false;

	private Handler mRequestHandler;
	
	
	private Handler mResponseHandler;
	private RzMediaDownloadListener<T> mRzMediaDownloadListener;
	private KGHandler kgHandler;
	private Gson gson;
	public static int count = 0;

	public RzMediaDownloadListener<T> getmRzMediaDownloadListener() {
		return mRzMediaDownloadListener;
	}

	public void setmRzMediaDownloadListener(
			RzMediaDownloadListener<T> mRzMediaDownloadListener) {
		this.mRzMediaDownloadListener = mRzMediaDownloadListener;
	}

	public RzMediaDownloader(Context context,Handler handler) {
		super(TAG);
		// TODO Auto-generated constructor stub
		LogUtil.e(TAG, "RzMediaDownloader()"+LogUtil.getLineInfo());
		this.context = context;
		kgHandler = new KGHandler();
		gson = new Gson();
		mResponseHandler = handler;
		player = new MediaPlayerWrapper();
		player.setOnCompletionListener(completionListener);
		player.setOnBufferingUpdateListener(bufferingUpdateListener);
		player.setOnInfoListener(infoListener);
		player.setOnPreparedListener(preparedListener);
		player.setOnErrorListener(errorListener);
	}
	
	@Override
	protected void onLooperPrepared() {
		// TODO Auto-generated method stub
		mRequestHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what == MESSAGE_LOADMUSIC) {
					T target = (T) msg.obj;
					//请求加载音乐
					mResponseHandler.removeCallbacks(curPosRunable);
					if(msg.arg1 == 1){
						handleRequest(target,true);
					}else{
						handleRequest(target,false);
					}
				}else if(msg.what == MESSAGE_LOADMIGUURL){
					final String song = msg.getData().getString("song");
					String author = msg.getData().getString("author");
					String musicId = msg.getData().getString("musicid");

					MiGuSearcher.findMusicInfoByid(musicId, new ResultCallback<MusicinfoResult>() {
						@Override
						public void onStart() {
							count++;
						}

						@Override
						public void onSuccess(MusicinfoResult musicinfoResult) {
							LogUtil.e(TAG,"onSuccess"+LogUtil.getLineInfo());
							Result temp;
							temp = toResult(musicinfoResult.getMusicInfo());
							Message msg = new Message();
							msg.what = RZMediaPlayActivity2.MESSAGE_DETAILED_LOAD;
							msg.obj = temp;
							LogUtil.e(TAG,"temp.toString:"+temp.toString());
							msg.arg1 = 1;//表示需要提示
							mResponseHandler.sendMessage(msg);
//							mResponseHandler.obtainMessage(RZMediaPlayActivity2.MESSAGE_DETAILED_LOAD,temp)
//									.sendToTarget();

						}

						@Override
						public void onFailed(String s, String s1) {

						}

						@Override
						public void onFinish() {
							if(count == 1){
								//播放
							}
						}
					});

				}else if(msg.what == MESSAGE_LOADURL){
					final String song = msg.getData().getString("song");
					String author = msg.getData().getString("author");
					final int random = msg.getData().getInt("random");
					if(random != RZMediaPlayActivity2.raInt){
						return;
					}
					
					kgHandler.search(song,author, new KGHandler.SongInfoCallBack() {
						@Override
						public void onFailed(int error, String errorMsg) {
							// TODO Auto-generated method stub
						}
						
						@Override
						public void onComplete(Song result) {
							// TODO Auto-generated method stub
							if(random != RZMediaPlayActivity2.raInt){
								return;
							}
							String gjson = "{\"name\":"+"\""+song+"\""+",\"author\":"+"\""+result.singername+"\""+",\"url\":"+"\""+result.url+"\""+"}";
							LogUtil.e("gjson","gjson:"+gjson);
							Result obj = gson.fromJson(gjson, Result.class);
							mResponseHandler.obtainMessage(RZMediaPlayActivity2.MESSAGE_DETAILED_LOAD,obj).sendToTarget();
						}
					});

				}else if(msg.what == MESSAGE_PAUSEMUSIC){
					sendMsgToDsPhoneStatus(0);
					sendtoDS(null,
							RzMusicLab.get().getCurName(),
							false);
					player.pause();
				}else if(msg.what == MESSAGE_PLAYMUSIC){
					sendMsgToDsPhoneStatus(1);
					sendtoDS(null,
							RzMusicLab.get().getCurName(),
							true);
					player.play();
				}else if(msg.what == MESSAGE_SETSEEKBAR){
					int pos = (int)msg.obj;
					player.setSeekBarPos(pos);
				}
			}
		};
	}

	private Result toResult(MusicInfo musicInfo) {
		LogUtil.e(TAG,"toResult"+LogUtil.getLineInfo());
		Result tmp = new Result();
		tmp.setAuthor(musicInfo.getSingerName());
		tmp.setName(musicInfo.getMusicName());
		LogUtil.e(TAG,"musicInfo.getListenUrl():"+musicInfo.getListenUrl());
		tmp.setUrl(musicInfo.getListenUrl());
		tmp.setImgUrl(musicInfo.getPicUrl());
		return tmp;
	}

	@Override
	public boolean quit() {
		// TODO Auto-generated method stub
		LogUtil.e(TAG,"quit()"+LogUtil.getLineInfo());
		mHasQuit = true;
		mRequestHandler.removeCallbacksAndMessages(null);
		mResponseHandler.removeCallbacksAndMessages(null);

		RzMusicLab.get().setCurAuthor(null);
		RzMusicLab.get().setCurName(null);
		RzMusicLab.get().setCurData(null);
		return super.quit();
	}
	
	public void queueLoadURL(String []msg,int raInt){
		Bundle bundle = new Bundle();
		Message mesg = new Message();
		mesg.what = MESSAGE_LOADURL;
		bundle.putString("song",msg[0]);
		bundle.putString("author",msg[1]);
		bundle.putInt("random",raInt);
		mesg.setData(bundle);
		if(mRequestHandler != null){
			mRequestHandler.sendMessage(mesg);
		}
	}

	/**
	 * 咪咕加载Url
	 * @param msg
	 */
	public void queueLoadMiGuURL(String []msg){
		Bundle bundle = new Bundle();
		Message mesg = new Message();
		mesg.what = MESSAGE_LOADMIGUURL;
		bundle.putString("song",msg[0]);
		bundle.putString("author",msg[1]);
		bundle.putString("musicid",msg[2]);
		mesg.setData(bundle);
		if(mRequestHandler != null){
			mRequestHandler.sendMessage(mesg);
		}
	}
	
	public void queueRzMedia(T target,Boolean isprompt){
		LogUtil.e(TAG,"queueRzMedia"+LogUtil.getLineInfo());
		if(isprompt){
			Message msg = new Message();
			msg.obj = target;
			msg.what = MESSAGE_LOADMUSIC;
			msg.arg1 = 1;
			mRequestHandler.sendMessage(msg);
		}else{
			mRequestHandler.obtainMessage(MESSAGE_LOADMUSIC,target).sendToTarget();
		}

	}

	public void queuePauseMedia(){
		mRequestHandler.obtainMessage(MESSAGE_PAUSEMUSIC).sendToTarget();
	}
	
	public void queuePlayMedia(){
		mRequestHandler.obtainMessage(MESSAGE_PLAYMUSIC).sendToTarget();
	}
	public void queueSetSeekBar(int pos){
		mRequestHandler.obtainMessage(MESSAGE_SETSEEKBAR,pos).sendToTarget();
	}
	
	public void clearQueue(){
		mRequestHandler.removeMessages(MESSAGE_LOADMUSIC);
	}
	
	public void relesePlayer(){
		if(player != null){
			player.stop();
			player.release();
		}
	}
	
	private void handleRequest(final T target,Boolean isPrompt) {
		LogUtil.e(TAG, "handleRequest   target.toString()"+target.toString());
		if(target instanceof Result){
			player.reset();//清除缓存
			Result tmp = (Result) target;
			RzMusicLab.get().setCurName(tmp.getName());
			RzMusicLab.get().setCurAuthor(tmp.getAuthor());
			RzMusicLab.get().setCurData(tmp);

			if(isPrompt){
				LogUtil.e("arg1","需要提示");
				MySynthesizer.getInstance(context).doSomethingAfterTts(new DoSomethingAfterTts() {
					@Override
					public void doSomethingsAfterTts() {
						player.setDataSource(context,tmp);
						FloatWindowManager.getInstance().removeAll();
						MyAIUI.WRITEAUDIOEABLE = false;
						LogUtil.e("now","----------------"+LogUtil.getLineInfo());
					}
				},"请欣赏,"+tmp.getAuthor()+"的"+tmp.getName(),null);
			}else{
				LogUtil.e("arg1","不需要提示");
				player.setDataSource(context,tmp);
			}

			mResponseHandler.postDelayed(curPosRunable,1000);
			sendMsgToDsPhoneStatus(1);
			sendtoDS(RzMusicLab.get().getCurData().getImgUrl(),
					RzMusicLab.get().getCurName(),
					true);
			sendMsgToDsPhone(RzMusicLab.get().getCurAuthor(),
					         RzMusicLab.get().getCurName(),
					         RzMusicLab.get().getCurData().getUrl(),
					         "kg");
		}
	}
	
	private Runnable curPosRunable = new Runnable() {
		
		@Override
		public void run() {
			LogUtil.e(TAG,"in curPosRunable:"+LogUtil.getLineInfo());
			// TODO Auto-generated method stub
			if(player.isPlaying()){
				mRzMediaDownloadListener.onRzMediaCurPos(player.getCurrentPosition());
			}
			mResponseHandler.postDelayed(curPosRunable,1000);
		}
	};
	
	/***************************MediaPlayer状态监听族********************************/
	private OnCompletionListener completionListener = new OnCompletionListener() {
		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			RzMusicLab.get().setCurAuthor(null);
			RzMusicLab.get().setCurName(null);
			RzMusicLab.get().setCurData(null);
			mResponseHandler.sendEmptyMessage(RZMediaPlayActivity2.MESSAGE_DETAILED_NEXT);
		}
	};
	private OnBufferingUpdateListener bufferingUpdateListener = new OnBufferingUpdateListener() {
		@Override
		public void onBufferingUpdate(MediaPlayer mp, int percent) {
			// TODO Auto-generated method stub
			mRzMediaDownloadListener.onRzMediaSecondaryProgress(percent);
		}
	};
	
	private OnInfoListener infoListener = new OnInfoListener() {
		@Override
		public boolean onInfo(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			switch (what) {
			case MediaPlayer.MEDIA_INFO_BUFFERING_START:
				LogUtil.e("mediaplayerlistener","开始缓存");

				break;
			case MediaPlayer.MEDIA_INFO_BUFFERING_END:
				LogUtil.e("mediaplayerlistener","缓存好一部分了");

				break;
			}
			return false;
		}
	};
	
	private OnPreparedListener preparedListener = new OnPreparedListener() {
		@Override
		public void onPrepared(MediaPlayer mp) {
			// TODO Auto-generated method stub
			player.start();
			mResponseHandler.post(new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					if(mHasQuit){
						return;
					}
					mRzMediaDownloadListener.onRzMediaPrepared(player);
				}
			});
		}
	};
	
	private OnErrorListener errorListener = new OnErrorListener() {
		@Override
		public boolean onError(MediaPlayer mp, int what, int extra) {
			// TODO Auto-generated method stub
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				LogUtil.e("mediaplayerlistener","媒体后台有问题");
				return true;
			case MediaPlayer.MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK:
				LogUtil.e("mediaplayerlistener","播放发送错误!");
				return true;
			}
			//跳曲问题：
			//切歌时，mediaPlayer有时会报Error(0,38)这个错误，这个错误又会调用上面的方法。
			//解觉办法拦截这个错误
			return true;
		}
	};
	
	//设计一个接口
	public interface RzMediaDownloadListener<T>{
		void onRzMediaPrepared(MediaPlayerWrapper mediaPlayer);
		void onRzMediaSecondaryProgress(int percent);
		void onRzMediaCurPos(int pos);
	} 
	
	/**********************************************/
	/**
	 * 发送给典声，用于更新他们界面的ui（主要是圆形图片）
	 */
	private void sendtoDS(String imageurl,String songname,Boolean isMusicplaying){
		try {
			JSONObject mJsonObject=new JSONObject();
			mJsonObject.put("imageurl",imageurl);
			mJsonObject.put("name", songname);
			mJsonObject.put("ispaly",isMusicplaying);

			Intent intent=new Intent();
			intent.putExtra("count", mJsonObject.toString());
			intent.setAction("com.szhklt.ds.MUSIC");
			context.sendBroadcast(intent);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 发送当前多媒体音乐的状态
	 * @param state 暂停是0 播放是1
	 */
	private void sendMsgToDsPhoneStatus(int state){
		AudioManager mAudioManager = (AudioManager)context.getSystemService(Context.AUDIO_SERVICE);
		JSONObject o = new JSONObject();
		try {
			o.put("state",state);
			o.put("position",player.getCurrentPosition());
			o.put("duration",player.getDuration());
			o.put("volume", mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC));
			LogUtil.e("dsplay","send json:"+o.toString());
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LogUtil.e(TAG,"json:"+o.toString()+LogUtil.getLineInfo());
		MainApplication.getContext().sendBroadcast(new Intent("com.hklt.play_song").putExtra("type",2).putExtra("data",o.toString()));
		o = null;
	}

	/**
	 * 发送歌曲信息给典声
	 * @param singer
	 * @param song
	 * @param url
	 * @param player
	 */
	private void sendMsgToDsPhone(String singer,String song,String url,String player){
		JSONObject o2 = new JSONObject();
		try {
			o2.put("singer",singer);
			o2.put("song", song);
			o2.put("img_url",url);
			o2.put("player", player);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		context.sendBroadcast(new Intent("com.hklt.play_song").putExtra("type",1).putExtra("data",o2.toString()));
		o2 = null;
	}
}
