package com.szhklt.www.voiceassistant;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import cn.kuwo.autosdk.api.KWAPI;
import cn.kuwo.autosdk.api.OnEnterListener;
import cn.kuwo.autosdk.api.OnExitListener;
import cn.kuwo.autosdk.api.OnPlayEndListener;
import cn.kuwo.autosdk.api.OnPlayerStatusListener;
import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.PlayEndType;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.autosdk.api.PlayState;
import cn.kuwo.autosdk.api.PlayerStatus;
import cn.kuwo.base.bean.Music;
import com.szhklt.www.voiceassistant.util.LineInControler;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class KwSdk {
	private static KwSdk instance;

//	public static PlayerStatus PREKWSTATUS = PlayerStatus.PAUSE;
	public static Boolean PREKWSTATUS = false;
	private String TAG="KwSdk";

	public KWAPI mKwapi;
	public Context context;

	public String singer;
	public String song;
	public String album;
	public String theme;
	//当年播放状态1:播放0:暂停
	public int curKwMusicStatus = 0;
//	public Music curMusic;
	private IKwSdk iKwSdk;
	public void setIKwSdk(IKwSdk iKwSdk) {
		this.iKwSdk = iKwSdk;
	}

	private KwSdk(){
		LogUtil.e(TAG, "KwSdk构造方法！");
		context = MainApplication.getContext();
		mKwapi = KWAPI.createKWAPI(context, "yuliang-hklt-ds");
		initKwApi(context);
	}

	public static KwSdk getInstance(){
		if(instance == null){
			instance = new KwSdk();
		}
		return instance;
	}

	private void initKwApi(final Context context){
		LogUtil.e(TAG,"initKwApi()"+LogUtil.getLineInfo());
		if(mKwapi.bindAutoSdkService()){
			LogUtil.i(TAG,"绑定音乐盒后台服务成功");
		}else{
			LogUtil.i(TAG,"绑定音乐盒后台服务失败");
		}

		mKwapi.registerEnterListener(new OnEnterListener() {
			@Override
			public void onEnter() {
				LogUtil.i(TAG, "应用启动收到了"+LogUtil.getLineInfo());		
				
				//启动酷我时linein切回linein == 0；
				LineInControler.getInstance().stopLineIn();//媒体播放时切回Linein == 0
				
				//启动酷我必须关闭多媒体播放器
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
				LogUtil.e("fuuck","蓝牙被关闭"+LogUtil.getLineInfo());
				iKwSdk.onEnter();
				mKwapi.bindAutoSdkService();
			}
		});

		//注册歌曲播放记录的监听，这个接口用于计算播歌数，一般不需要设置！！！
		mKwapi.registerPlayEndListener(new OnPlayEndListener() {
			@Override
			public void onPlayEnd(PlayEndType arg0) {
				// do something by yourself
				LogUtil.i(TAG,arg0.toString());
				iKwSdk.onPlayEnd(arg0);
			}
		});

		//退出监听
		mKwapi.registerExitListener(new OnExitListener() {
			@Override
			public void onExit() {
				LogUtil.e(TAG, "应用退出了");
				iKwSdk.onExit();
			}
		});

		//播放操作监听
//		mKwapi.registerPlayerStatusListener(new OnPlayerStatusListener() {
//			@Override
//			public void onPlayerStatus(PlayerStatus arg0, Music music) {
//				LogUtil.e(TAG, "onPlayerStatus()"+"监听到酷我有动作"+arg0);
//				// TODO Auto-generated method stub
//				if(arg0 == PlayerStatus.INIT || arg0 == PlayerStatus.PLAYING){
//					curKwMusicStatus = 1;
//					curMusic = music;
//					LogUtil.e(TAG, "酷我初始化或者播放!");
//					//					MainService.isLauncherAcyivity = false;
//					Intent intent = new Intent();
//					intent.putExtra("count", "[MediaPlayActivity]" + "pause");
//					intent.setAction("com.szhklt.service.MainService");
//					context.sendBroadcast(intent);
//					//停止蓝牙推送
//					context.sendBroadcast(new Intent("android.bluetooth.action.FINISH"));
//				}else if(arg0 == PlayerStatus.PAUSE){
//					curKwMusicStatus = 0;
//					LogUtil.e(TAG,"暂停酷我音乐");
//
//				}else if(arg0 == PlayerStatus.STOP){
//					LogUtil.e(TAG,"退出酷我音乐");
//
//				}
//			}
//		});
	}
	
	/**
	 * 监听酷我音乐启动
	 * @param enterListener
	 */
	private void registerEnterListener(OnEnterListener enterListener){
		mKwapi.registerEnterListener(enterListener);
	}

	/**
	 * 获取当前播放器状态
	 * @return 当前播放器状态
	 */
	public PlayerStatus getCurPlayerStatus(){
		if(mKwapi == null){
			LogUtil.e("kw","mKwapi为空!!");
			return null;
		}
		return mKwapi.getPlayerStatus();
	}
	/**
	 * 设置酷我的状态值
	 * @param playState
	 */
public void setCurPlayerStatus(PlayState playState){
	if(mKwapi != null){
		mKwapi.setPlayState(playState);
	}
}
	/**
	 * 获取当前的音乐播放信息
	 * @return 返回当前的音乐对象
	 */
	public Music getNowPlaymusic(){
		if(mKwapi == null){
			LogUtil.e("kw","mKwapi为空!!");
			return null;
		}
		return mKwapi.getNowPlayingMusic();
	} 

	//按主题播放
	public void playClientMusicsByTheme(String Theme){
		if(mKwapi == null){
			return;
		}
		mKwapi.playClientMusicsByTheme(Theme);
	}

	//在应用内搜索歌曲并播放
	public void playClientMusics(String name,String singer,String album){
		mKwapi.playClientMusics(name, singer, album);
	}

	//暂停
	public void pause(){
		LogUtil.e("rc","pause()");
		if(mKwapi == null){
			return;
		}
		//	    if(!mKwapi.isKuwoRunning()){
		//	        return;        
		//	    }  
		this.mKwapi.setPlayState(PlayState.STATE_PAUSE);
		//	    if(mKwapi.getPlayerStatus() == PlayerStatus.PLAYING){
		//	        LogUtil.e("rc","setPlayState(PlayState.STATE_PAUSE");
		//	        this.mKwapi.setPlayState(PlayState.STATE_PAUSE);
		//	    }          
	}  

	//播放 
	public void play(){
		LogUtil.e("kw","play()");
		if(mKwapi == null){
			return;
		}
		//	    if(!mKwapi.isKuwoRunning()){
		//	        return;        
		//	    }       
		this.mKwapi.setPlayState(PlayState.STATE_PLAY);
		//	    if(mKwapi.getPlayerStatus() == PlayerStatus.PAUSE){
		//	        LogUtil.e("rc","setPlayState(PlayState.STATE_PLAY)");
		//	        this.mKwapi.setPlayState(PlayState.STATE_PLAY);
		//	    }          
	} 

	//播放下一首
	public void next(){
		LogUtil.e("kw","next()");
		if(mKwapi == null){
			return;
		}
		//	    if(!mKwapi.isKuwoRunning()){
		//	        return;        
		//	    }          
		mKwapi.setPlayState(PlayState.STATE_NEXT);          
	}

	//播放上一首
	public void pre(){
		LogUtil.e("kw","pre()");
		if(mKwapi == null){
			return;
		}
		//	    if(!mKwapi.isKuwoRunning()){
		//	        return;        
		//	    }          
		mKwapi.setPlayState(PlayState.STATE_PRE); 
	}

	//退出酷我
	public void exit(){
		if(mKwapi == null){
			return;
		}
		mKwapi.exitAPP();
	}

	//开启酷我
	public void start(Boolean autoPlay){
		if(mKwapi == null){
			return;
		}
		//		if(mKwapi.isKuwoRunning()){
		//			return;
		//		}

		mKwapi.startAPP(autoPlay);
	}

	public boolean isKuwoRunning() {
		if(mKwapi == null){
			return false;
		}
		return mKwapi.isKuwoRunning();
	}


	public void setPlayMode(PlayMode mode){
		if(mKwapi == null){
			return;
		}
		mKwapi.setPlayMode(mode);
	}

	//随机播放
	public void randomPlayMusic(){
		if(mKwapi == null){
			return;
		}
		mKwapi.randomPlayMusic();
	}

	//在线搜索
	public void searchOnlineMusic(String singer,String song,String album,OnSearchListener listener){
		LogUtil.e("kw","歌曲被搜索!!!");
		LogUtil.e("kw","searchOnlineMusic()");
		LogUtil.e("now","searchOnlineMusic()");
		if(mKwapi == null){
			return;
		}
		//注入数据
		this.singer = null;
		this.song = null;
		this.album = null;
		this.singer = singer;
		this.song = song;
		this.album = album;

		mKwapi.searchOnlineMusic(singer, song, album,listener);
	}

	//播放搜索的歌曲
	public void playMusic(List<Music> musics, int index, boolean isEntryApp, boolean visExit){
		LogUtil.e("kw","装入歌曲列表,并播放");
		if(mKwapi == null){
			return;
		}
		mKwapi.playMusic(musics, index, isEntryApp, visExit);
	}

	/**
	 * 播放本地歌曲文件
	 * @param path
	 */
	public void playLocalMusic(String path){
		if(mKwapi == null){
			return;
		}
		mKwapi.playLocalMusic(path);
	}

	/**
	 * 监听酷我退出
	 */
	private void registerExitListener(OnExitListener listener){
		if(mKwapi == null){
			return;
		}
		mKwapi.registerExitListener(listener);
	}

	/**
	 * 取消监听酷我退出
	 */
	private void unRegisterExitListener(){
		if(mKwapi == null){
			return;
		}
		mKwapi.registerExitListener(new OnExitListener() {

			@Override
			public void onExit() {
				// TODO Auto-generated method stub
				//do nothing
			}
		});
	}
	public int  getCurrentPos(){
		return mKwapi.getCurrentPos();
	}

	public Object getCurrentMusicDuration() {
		return mKwapi.getCurrentMusicDuration();
	}
	public void registerPlayerStatusListener(OnPlayerStatusListener onPlayerStatusListener){
		mKwapi.registerPlayerStatusListener(onPlayerStatusListener);
	}
	/**
	 * 进入客户端歌词搜索
	 */
	public void playClientMusicByLrc(String lrc){
		if(mKwapi == null){
			return;
		}
		mKwapi.playClientMusicsByLrc(lrc);
	}

	/**
	 * 不启动客户端歌词搜索
	 */
	public void searchOnlineMusicBylrc(String lrc,OnSearchListener searchListener){
		if(mKwapi == null){
			return;
		}
		mKwapi.searchOnlineMusicBylrc(lrc, searchListener);
	}
	/**
	 * 获取当前播放的歌曲信息
	 * 参数值为1：只获取歌曲名
	 * 参数值为2：获取歌手名和歌曲名
	 * 参数值为3：获取专辑名和歌曲名
	 * 参数值为4：获取歌手名和专辑名
	 * 参数值为5：获取歌手名和专辑名
	 * 参数值为6：获取歌手名+歌曲名+专辑名
	 * @param num
	 * @return
	 */
	public String getNowPlayingMusic(int num){
		Music music = mKwapi.getNowPlayingMusic();
		String des="";
		if(music!=null&&num==1){// 只获取歌曲名
			des=music.name;
			return des;
		}else if(music!=null&&num==2){//获取歌手名和歌曲名
			des=music.artist+"的"+music.name;
			return des;
		}else if(music!=null&&num==3){//获取专辑名和歌曲名
			des=music.album+" - "+music.name;
			return des;
		}else if(music!=null&&num==4){//获取歌手名和专辑名
			des=music.artist+" - "+music.album;
			return des;
		}else if(music!=null&&num==5){//获取歌手名和专辑名
			des=music.artist+" - "+music.album;
			return des;
		}else if(music!=null&&num==6){//获取歌手名+歌曲名+专辑名
			des=music.artist+" - "+music.name+" - "+music.album;
			return des;
		}
		return "当前没有播放歌曲";

	}
	public Music getNowPlayingMusic(){
		Music music = mKwapi.getNowPlayingMusic();
		return music;
	}
	public interface IKwSdk{
		void onEnter();
		void onPlayEnd(PlayEndType arg0);
		void onExit();
	}
}
