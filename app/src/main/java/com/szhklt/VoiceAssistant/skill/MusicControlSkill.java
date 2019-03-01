package com.szhklt.VoiceAssistant.skill;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.rich.czlylibary.bean.MusicInfo;
import com.rich.czlylibary.bean.SearchLyricNewResult;
import com.rich.czlylibary.bean.SingerNew;
import com.rich.czlylibary.bean.SongNew;
import com.rich.czlylibary.sdk.ResultCallback;
import com.szhklt.VoiceAssistant.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.RzMusicPkg.RzMusicLab;
import com.szhklt.VoiceAssistant.activity.RZMediaPlayActivity2;
import com.szhklt.VoiceAssistant.activity.SleepActivity;
import com.szhklt.VoiceAssistant.beam.intent;
import cn.kuwo.autosdk.api.PlayMode;
import cn.kuwo.base.bean.Music;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.db.MusicCollectionDBHelper;
import com.szhklt.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.MiGuSearcher;

public class MusicControlSkill extends Skill{
	private static final String TAG = "MusicControlSkill";
	public static String KFLAG = null;
	private String musicCtrIntent;
	private String lrc;
	private KwSdk mKwSdk = KwSdk.getInstance();
	public Context context;
//	private MediaControler mediaController;
	private static List<MusicInfo> musicInfoList = new ArrayList<>();
	private String musicName;
	private String singerName;

	public MusicControlSkill(intent intent) {
		super(intent);
		mintent = intent;
		context = MainApplication.getContext();
	}
	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		List<Slot> slots = new ArrayList<>();
		slots = mintent.getSemantic().get(0).getSlots();
		musicCtrIntent = mintent.getSemantic().get(0).getIntent();
		LogUtil.e("musicstate", "musicCtrIntent:"+musicCtrIntent+ LogUtil.getLineInfo());
		if("music_player_control".equals(musicCtrIntent)){//播放控制意图
			for(Slot slot:slots){
				if("musicValue".equals(slot.getName())){
					KFLAG = slot.getNormValue();
					continue;
				}

				if("musicAct2".equals(slot.getName())){
					KFLAG = slot.getNormValue();
					continue;
				}
			}
		}else if("music_collection".equals(musicCtrIntent)){//歌曲收藏意图
			for(Slot slot:slots){
				if("music_act1".equals(slot.getName())){
					KFLAG=slot.getNormValue();
					break;
				}
				if("music_coll_act".equals(slot.getName())){
					KFLAG=slot.getNormValue();
					continue;
				}
			}
		}else if("music_searchbylrc".equals(musicCtrIntent)){//歌词搜索意图
			for(Slot slot:slots){
				if("lrc".equals(slot.getName())){
					KFLAG=slot.getName();
					lrc=slot.getNormValue();
					continue;
				}
			}
		}else if("music_searchbylrc_act".equals(musicCtrIntent)){
			for(Slot slot:slots){
				if("playbylrc_sure".equals(slot.getName())){
					KFLAG=slot.getNormValue();
					continue;
				}
				if("playbylrc_no".equals(slot.getName())){
					KFLAG=slot.getNormValue();
					continue;
				}
			}
		}else{
			LogUtil.e(TAG, "musicCtrIntent:"+musicCtrIntent+LogUtil.getLineInfo());
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		LogUtil.e("wakeuptime","唤醒到MusicControlSkill技能时间:"+System.currentTimeMillis());
		if ("gosleep".equals(KFLAG)) {//休眠
			MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
			mKwSdk.exit();//关闭酷我音乐
			Intent intent = new Intent(MainApplication.getContext(), SleepActivity.class);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			MainApplication.getContext().startActivity(intent);
			mTts.doSomethingAfterTts(null,"好",question);
			return;
		}else if("reboot".equals(KFLAG)){//重启
			Intent intent = new Intent("android.action.GLOBAL_REBOOT_SYSTEM");  
			MainApplication.getContext().sendBroadcast(intent); 
			mTts.doSomethingAfterTts(null,"好",question);
			return;
		}else if("shutdown".equals(KFLAG)){//关机
			Intent intent = new Intent("android.action.HKLT_SHUTDOWN");  
			MainApplication.getContext().sendBroadcast(intent); 
			mTts.doSomethingAfterTts(null,"好",question);
			return;
		}
		
		//播放器控制
		if("music_player_control".equals(musicCtrIntent)){
			LogUtil.e(TAG,"播放器控制"+LogUtil.getLineInfo()+", KFLAG:"+KFLAG);
			if ("next".equals(KFLAG)) {//播放下一首
				next();
			}else if ("pre".equals(KFLAG)) {//播放上一首
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","prev"));
				mKwSdk.pre();
				send("[MediaPlayActivity]" + "pre");
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						mKwSdk.play();
					}
				},"好的",question);
			}else if ("pause".equals(KFLAG)) {//暂停播放
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","pause"));
				mKwSdk.pause();
				send("[MediaPlayActivity]" + "pause");
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						mKwSdk.pause();
						send("[MediaPlayActivity]" + "pause");
					}
				}, "好的", question);
			}else if("stop".equals(KFLAG)){//退出播放
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
				LogUtil.e("fuuck","蓝牙被关闭"+LogUtil.getLineInfo());
				send("[MediaPlayActivity]" + "pause");
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						mKwSdk.exit();
					}
				}, "好的", question);
			}else if ("start".equals(KFLAG)) {//继续播放
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","play"));
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						mKwSdk.play();
						send("[MediaPlayActivity]" + "play");
					}
				}, "好的", question);
				//只有酷我才有
			}else if("loop".equals(KFLAG)){//循环播放			
				mKwSdk.setPlayMode(PlayMode.MODE_ALL_CIRCLE);
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						mKwSdk.play();
					}
				},"好的",question);
			}else if("single".equals(KFLAG)){//单曲循环		
				mKwSdk.setPlayMode(PlayMode.MODE_SINGLE_CIRCLE);
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						mKwSdk.play();
					}
				},"好的",question);
			}else if("shuffle".equals(KFLAG)){//随机播放		
				mKwSdk.setPlayMode(PlayMode.MODE_ALL_RANDOM);
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						mKwSdk.play();
					}
				},"好的",question);
			}else if ("back".equals(KFLAG)) {//退出播放
				LogUtil.e("now","退出播放"+LogUtil.getLineInfo());
				send("[MediaPlayActivity]" + "pause");
				mKwSdk.pause();
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						// TODO Auto-generated method stub
						mKwSdk.exit();
					}
				},"好的",question);
			}else if ("gosleep".equals(KFLAG)) {//休眠
				mKwSdk.exit();//关闭酷我音乐
				Intent intent = new Intent(MainApplication.getContext(), SleepActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				MainApplication.getContext().startActivity(intent);
			}else if("reboot".equals(KFLAG)){//重启
				Intent intent = new Intent("android.action.GLOBAL_REBOOT_SYSTEM");  
				MainApplication.getContext().sendBroadcast(intent); 
				mTts.doSomethingAfterTts(null,"好",question);
			}else if("shutdown".equals(KFLAG)){//关机
				Intent intent = new Intent("android.action.HKLT_SHUTDOWN");  
				MainApplication.getContext().sendBroadcast(intent); 
				mTts.doSomethingAfterTts(null,"好",question);
			}else if("exit".equals(KFLAG)){//退出聊天
				context.sendBroadcast(new Intent("android.rzmediaplayact.action.FINISH"));
				send("[MediaPlayActivity]" + "stop");
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						FloatWindowManager.getInstance().removeAll();
//						SpeekTimeout.getInstance().stop();
						KwSdk.getInstance().exit();
					}
				}, "好的", null);
			}
		}
		
		//询问当前播放的歌曲//现在放的是什么
		if("music_search".equals(musicCtrIntent)){
//			if(mKwSdk.isKuwoRunning() == false){
//				mTts.speechSynthesis("酷我没有运行",question);
//				return;
//			}
//			LogUtil.e("musicstate", "************查看歌曲信息************"+LogUtil.getLineInfo());
//			String answer = mKwSdk.getNowPlayingMusic(2);
//			boolean isKuwoRunning = mKwSdk.isKuwoRunning();
//			if(isKuwoRunning){
//				mTts.doSomethingAfterTts(null,"正在为您播放的是："+answer,question);
//			}else{
//				mTts.doSomethingAfterTts(null,answer,question);
//			}
			String cn = RzMusicLab.get().getCurName();
			String ca = RzMusicLab.get().getCurAuthor();
			StringBuilder sb = new StringBuilder();
			sb.append("当前播放的是");
			if(cn != null){
				sb.append(ca);
			}
			sb.append("的");
			if(ca != null){
				sb.append(cn);
			}
			if(cn != null && ca != null){
				mTts.speechSynthesis(sb.toString(),question);
			}else{
				mTts.speechSynthesis("当前没有播放音乐",question);
			}
		}

		//歌曲收藏
		if("music_collection".equals(musicCtrIntent)){
			if(mKwSdk.isKuwoRunning() == false){
				mTts.speechSynthesis("酷我没有运行，无法收藏",question);
				return;
			}
			final MusicCollectionDBHelper musicCollectionDBHelper = MusicCollectionDBHelper.getInstance();
			if("collection".equals(KFLAG)){
				Music music = mKwSdk.getNowPlayingMusic();
				if(music==null){
					mTts.doSomethingAfterTts(null,"没有正在播放的歌曲",question);
					return;
				}
				int id = musicCollectionDBHelper.queryIdBySong(music.artist,music.name,music.album);
				if(id==-1){//id==-1说明不存在于收藏列表，这时加进收藏列表
					musicCollectionDBHelper.insert(music.artist, music.name, music.album);
				}
				mTts.doSomethingAfterTts(null,"已收藏，想听的时候请对我说：播放收藏的歌曲",question);
			}else if("delete".equals(KFLAG)){
				musicCollectionDBHelper.deleteAll();
				mTts.doSomethingAfterTts(null,"收藏列表已清空",question);
			}else if("play".equals(KFLAG)){
				Map<Integer, Map<String, Object>> map = musicCollectionDBHelper.queryAll();
				if(map.size()==0){
					mTts.doSomethingAfterTts(null, "您还没有收藏的歌曲，请对我说：收藏这首歌，下次就可以播放了哦", question);
					return;
				}
				Integer[] keys = map.keySet().toArray(new Integer[0]);
				Random random = new Random();
				Integer id = keys[random.nextInt(keys.length)];

				final Map<String, Object> map1 = map.get(id);
				mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
					@Override
					public void doSomethingsAfterTts() {
						mKwSdk.playClientMusics((String)map1.get("name"), (String)map1.get("singer"), (String)map1.get("album"));
						musicCollectionDBHelper.insert((String)map1.get("singer"), (String)map1.get("name"), (String)map1.get("album"));
					}
				}, "好的", question);
			}
		}

		//根据歌词搜索
		if("music_searchbylrc".equals(musicCtrIntent)){
			LogUtil.e(TAG,"lrc:"+lrc);
			//咪咕
			MiGuSearcher.searchSongByLyric(lrc, 1, 15, new ResultCallback<SearchLyricNewResult>() {
				@Override
				public void onStart() {

				}

				@Override
				public void onSuccess(SearchLyricNewResult searchLyricNewResult) {
					searchSongPlay(searchLyricNewResult.getSearchLyric().getData().getResult());
				}

				@Override
				public void onFailed(String s, String s1) {

				}

				@Override
				public void onFinish() {

				}
			});

			//酷我
//			if(mKwSdk.isKuwoRunning() == false){
//				mTts.speechSynthesis("酷我没有运行，无法搜索歌词",question);
//				return;
//			}
//			if("lrc".equals(KFLAG)){
//				KwMusicSkill kwMusicSkill = new KwMusicSkill(mintent);
//				kwMusicSkill.playOnlineMusicBylrc(lrc);
//			}
		}

		if("music_searchbylrc_act".equals(musicCtrIntent)){
			if("playbylrc_sure".equals(KFLAG)){//确定歌词播放
				confirmAndPlay();
			}else if("playbylrc_no".equals(KFLAG)){//取消歌词播放
				mTts.doSomethingAfterTts(null,"好的",question);
			}else{
				mTts.doSomethingAfterTts(null,"我没有听懂",question);
			}
		}
	}
	
	/********************************控制函数（封装）*******************************/
	/**
	 * 播放下一首
	 */
	private void next(){
		context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","next"));
		mKwSdk.next();
		send("[MediaPlayActivity]" + "next");
		mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
				mKwSdk.play();
			}
		},"好的",question);
	}
	
	/**
	 * 发送广播
	 */
	private void send(String text) {
		Intent intent = new Intent();
		intent.putExtra("count", text);
		intent.setAction("com.szhklt.service.MainService");
		MainApplication.getContext().sendBroadcast(intent);
	}

	/**
	 * 解析歌曲
	 * @param songNews
	 */
	private void searchSongPlay(SongNew[] songNews) {
		try {
			disposeSongData(songNews);
			mTts.speechSynthesis(buildAns(musicName,singerName),lrc);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 构建歌词查询回复
	 */
	private String buildAns(String musicName,String singerName){
		StringBuilder ans = new StringBuilder();
		if(singerName != null && musicName!= null){
			ans.append("您是想听");
			ans.append(singerName);
			ans.append("演唱的");
			ans.append(clearBracket(musicName));
			ans.append("吗？您可以说:\"是的\"或者\"取消\"!");
		}
		return ans.toString();
	}

	/**
	 * 如果有括号，去掉括号及括号里的字符串
	 * @param company
	 * @return
	 */
	public String clearBracket(String company){
		if(company.contains("(")&&company.contains(")")){
			String bracket = company.substring(company.indexOf("("),company.indexOf(")")+1);
			company = company.replace(bracket, "");
			return company;
		}
		return company;
	}

	/**
	 * 确认并播放
	 */
	private void confirmAndPlay(){
		LogUtil.e(TAG,"musicInfoList:"+musicInfoList.size());
		if (musicInfoList.isEmpty()) {
//                showToast("查询歌曲成功，但是歌曲没有版权ID，不能播放");
			mTts.speechSynthesis("抱歉,暂时没有版权",question);
			LogUtil.e(TAG,"查询歌曲成功，但是歌曲没有版权ID，不能播放");
		} else {
			//进入播放器播放
			Bundle bundle = new Bundle();
			bundle.putSerializable("migu",(Serializable)musicInfoList);
			bundle.putString("index","0");
			actionStart(bundle);
			musicInfoList.clear();
		}
	}

	/**
	 * 处理数据
	 */
	private void disposeSongData(SongNew[] songNews){
		for (int i=0;i < songNews.length;i++) {
			MusicInfo musicInfo = new MusicInfo();
			if (songNews[i].getFullSongs() != null && songNews[i].getFullSongs().length > 0) {
				musicInfo.setMusicId(songNews[i].getFullSongs()[0].getCopyrightId());
				LogUtil.e(TAG,"MusicId:"+songNews[i].getFullSongs()[0].getCopyrightId());
			} else {
				continue;
			}
			musicInfo.setMusicName(songNews[i].getName());
			LogUtil.e(TAG,"MusicName:"+songNews[i].getName());
			if(i == 0){
				musicName = songNews[i].getName();
			}
			musicInfo.setSingerName(songNews[i].getSingers()[0].getName());
			LogUtil.e(TAG,"SingerName:"+songNews[i].getSingerName());
			SingerNew[] ss = songNews[i].getSingers();
			LogUtil.e(TAG,"SingerName:"+"ss.length:"+ss.length+ss[0].toString());
			if(i == 0){
				singerName = ss[0].getName();
			}
			if (songNews[i].getMvPic() != null && songNews[i].getMvPic().length != 0) {
				musicInfo.setPicUrl(songNews[i].getMvPic()[0].getPicPath());
				LogUtil.e(TAG,"SingerName:"+songNews[i].getMvPic()[0].getPicPath());
			}
			musicInfo.setLrcUrl(songNews[i].getLyricUrl());
			LogUtil.e(TAG,"LrcUrl:"+songNews[i].getLyricUrl());
			musicInfoList.add(musicInfo);
			LogUtil.e(TAG,"=================================");
		}
		LogUtil.e(TAG,"musicInfoList:"+musicInfoList.size());
	}

	/**
	 * 携带数据启动播放器
	 * @param data
	 */
	private void actionStart(Bundle data){
		Intent sintent = new Intent(MainApplication.getContext(), RZMediaPlayActivity2.class);
		sintent.putExtras(data);
		sintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		MainApplication.getContext().startActivity(sintent);
	}

}
