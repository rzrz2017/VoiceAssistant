package com.szhklt.www.VoiceAssistant.skill;

import java.util.ArrayList;
import java.util.List;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.szhklt.www.VoiceAssistant.KwSdk;
import com.szhklt.www.VoiceAssistant.MainApplication;
import com.szhklt.www.VoiceAssistant.R;
import com.szhklt.www.VoiceAssistant.component.MyAIUI;
import com.szhklt.www.VoiceAssistant.db.MusicCollectionDBHelper;
import com.szhklt.www.VoiceAssistant.floatWindow.FloatWindowManager;
import com.szhklt.www.VoiceAssistant.util.LogUtil;
import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.SearchStatus;
import cn.kuwo.base.bean.Music;
import com.szhklt.www.VoiceAssistant.beam.intent.Slot;
import com.szhklt.www.VoiceAssistant.beam.intent;


public class KwMusicSkill extends Skill{
	private static final String TAG = "KwMusicSkill";
	private String service;
	private int rc;
	private String intent;

	private String theme = null;
	private String song = null;
	private String singer = null;
	private String album = null;

	private KwSdk mKwSdk = KwSdk.getInstance();
	public static final List<String> list=new ArrayList<>();//存储通过歌词搜索到的歌曲信息
	private FloatWindowManager mFWM=FloatWindowManager.getInstance();//悬浮窗UI管理
	private Context context= MainApplication.getContext();
	public KwMusicSkill(intent intent) {
		mintent = intent;
	}
	public KwMusicSkill(){
	}
	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		
		LogUtil.e(TAG,"extractVaildInformation()");
		
		service = mintent.getService();

		if ("musicX".equals(service) == false){
			return;
		}

		intent = mintent.getSemantic().get(0).getIntent();
		
		rc = mintent.getRc();

		final ArrayList<Slot> slots = mintent.getSemantic().get(0).getSlots();

		for(Slot slot:slots){
			if("tags".equals(slot.getName())){
				theme = slot.getValue();
				Log.e("kw","theme:"+theme);
				continue;
			}

			if("artist".equals(slot.getName())){
				singer = slot.getValue();
				Log.e("kw","singer:"+singer);
				continue;
			}
			
			if(singer == null){
				if("band".equals(slot.getName())){
					singer = slot.getValue();
					Log.e("kw","band:"+singer);
				}
			}

			if("song".equals(slot.getName())){
				song = slot.getValue();
				Log.e("kw","song:"+song);
				continue;
			}

			if("source".equals(slot.getName())){
				album = slot.getValue();
				Log.e("kw","album:"+album);
				continue;
			}
		}
	}

	@Override
	public void execute() {
		// TODO Auto-generated method stub
		extractVaildInformation();
		//关闭其它音乐
		send("[KWplaying]");
		
		//由于服务是 musicX 的原因，上下首也要在
		if(rc == 3){
			//rc 等于 3的情况下，也有可能出现上下首控制
			if("INSTRUCTION".equals(intent)){
				ArrayList<Slot> slots = mintent.getSemantic().get(0).getSlots();
				for(Slot s:slots){
					if("insType".equals(s.getName())){
						//回到上一曲是（past），很奇怪，讯飞就是这样*-*
						if("past".equals(s.getValue())){
							LogUtil.e("dispose","RC == 3s时，出现的下一首情况"+LogUtil.getLineInfo());
//							mediaControler.next();
							context.sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","prev"));
							mKwSdk.pre();
							send("[MediaPlayActivity]" + "pre");
							mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
								@Override
								public void doSomethingsAfterTts() {
									// TODO Auto-generated method stub
								}
							},"好的",question);
						}else if("sleep".equals(s.getValue())){
//							MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
//							MySynthesizer.recoveyMusicStatusBeforeWakeUp();
							mFWM.removeAll();
						}
					}
				}
				return;
			}
		}
		
		//随机播放
		if (intent.equals("RANDOM_SEARCH")) {
			String[] kwanswer = MainApplication.getContext().getResources().getStringArray(R.array.kwanswer);
			String answer=kwanswer[(int)(Math.random()*(kwanswer.length))];
			mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(song, singer, album, theme){
				@Override
				public void playMusicAfterTts(){
					mKwSdk.randomPlayMusic();
					//在酷我音乐播放时，关闭海豚,并停止录音。
					mFWM.removeAll();
					MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
				}
			},answer,question);
			return;
		}
		
		//主题播放
		if(theme != null){
			String answer="好的!为您播放"+theme+"主题的歌";
			 if(theme.indexOf("儿歌")!=-1){
				answer="好的!为您播放"+theme;
			}
			mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(song, singer, album, theme){
				@Override
				public void playMusicAfterTts(){
					mKwSdk.playClientMusicsByTheme(theme);
					//在酷我音乐播放时，关闭海豚
					mFWM.removeAll();
					MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
				}
			},answer,question);
			return;
		}

		/*********************************************************************************************/
		LogUtil.e(TAG, "singer："+singer);
		LogUtil.e(TAG, "song："+song);
		LogUtil.e(TAG, "album："+album);
		//播放搜索到的歌曲
		mKwSdk.searchOnlineMusic(singer,song,album,new OnSearchListener() {
			
			@Override
			public void searchFinshed(SearchStatus status, boolean first,final List<Music> musics, boolean overTime) {
				if(status != SearchStatus.SUCCESS){
					LogUtil.e("kw","搜索出错!推荐同歌手其他音乐或者不作处理"+LogUtil.getLineInfo());
					//有歌手
					if(mKwSdk.singer != null){
						//搜索同一个歌手的其它歌曲
						mKwSdk.searchOnlineMusic(mKwSdk.singer, null, null, new OnSearchListener() {
							@Override
							public void searchFinshed(SearchStatus status, boolean first,final List<Music> musics, boolean overTime) {
								if(status == SearchStatus.SUCCESS){//成功
									int random = (int)(Math.random()*musics.size());
									LogUtil.e("kw","随机数为:"+random);
									final Music temp = musics.get(random);

									mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(null,null,null,null){
										@Override
										public void playMusicAfterTts(){
											mKwSdk.playClientMusics(temp.name, temp.artist, temp.album);
											//在酷我音乐播放时，关闭海豚
											mFWM.removeAll();
											MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
											try {
												Music music = mKwSdk.getNowPlayingMusic();
												int id = MusicCollectionDBHelper.getInstance().queryIdBySong(music.artist,music.name,music.album);
												if(id!=-1){//id!=-1说明已存在于歌曲列表中，这时增加播放次数
													MusicCollectionDBHelper.getInstance().insert(music.artist,music.name,music.album);
												}
											} catch (Exception e) {
												LogUtil.e("music", "收藏歌曲错误"+LogUtil.getLineInfo());
											}
										}
									},"该歌手没有此歌曲，请欣赏"+mKwSdk.singer+"演唱的"+temp.name,question);

								}else{//失败
									mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(null,null,null,null){
										@Override
										public void playMusicAfterTts(){
											mKwSdk.randomPlayMusic();
											//在酷我音乐播放时，关闭海豚
											mFWM.removeAll();
										}
									},"抱歉,没找到该歌手,正在推荐歌曲",question);
								}
								return;	
							}
						});
						return;
					}

					//有专辑
					if(mKwSdk.album != null){
						//播放同一张专辑的其它歌曲
						mKwSdk.searchOnlineMusic(null, null, mKwSdk.album, new OnSearchListener() {
							@Override
							public void searchFinshed(SearchStatus status, boolean first,final List<Music> musics, boolean overTime) {
								if(status == SearchStatus.SUCCESS){//成功
									int random = (int)(Math.random()*musics.size());
									LogUtil.e("kw","随机数为:"+random);
									final Music temp = musics.get(random);

									mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(null,null,null,null){
										@Override
										public void playMusicAfterTts(){
											mKwSdk.playClientMusics(temp.name, temp.artist, temp.album);
											//在酷我音乐播放时，关闭海豚
											mFWM.removeAll();
											MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
											try {
												Music music = mKwSdk.getNowPlayingMusic();
												int id = MusicCollectionDBHelper.getInstance().queryIdBySong(music.artist,music.name,music.album);
												if(id!=-1){//id!=-1说明已存在于歌曲列表中，这时增加播放次数
													MusicCollectionDBHelper.getInstance().insert(music.artist,music.name,music.album);
												}
											} catch (Exception e) {
												LogUtil.e("music", "收藏歌曲错误"+LogUtil.getLineInfo());
											}
										}
									},"该专辑里没有这首歌,请欣赏"+mKwSdk.album+"里的"+temp.name,question);

								}else{//失败
									mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(null,null,null,null){
										@Override
										public void playMusicAfterTts() {
											mKwSdk.randomPlayMusic();
											//在酷我音乐播放时，关闭海豚
											mFWM.removeAll();
										}
									}, "抱歉,没找到该专辑,正在为你推荐歌曲", question);
								}
								return;	
							}
						});
						return;
					}

					mTts.doSomethingAfterTts(null, "抱歉,没有找到这首歌", question);
					return;
				}else{
//					LogUtil.e(TAG,"**************搜索到底歌曲列表****************");
//					for(int i=0;i < musics.size();i++){
//						LogUtil.e(TAG,"name"+musics.get(i).name);
//					}
					mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
						@Override
						public void playMusicAfterTts() {
//							mKwSdk.mKwapi.playMusic(musics, 0, true, false);
							//播放歌曲会显示歌曲列表
							mKwSdk.playClientMusics(mKwSdk.song,mKwSdk.singer,mKwSdk.album);
							//在酷我音乐播放时，关闭海豚
							mFWM.removeAll();
							MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
							try {
								Music music = mKwSdk.getNowPlayingMusic();
								int id = MusicCollectionDBHelper.getInstance().queryIdBySong(music.artist,music.name,music.album);
								if(id!=-1){//id!=-1说明已存在于歌曲列表中，这时增加播放次数
									MusicCollectionDBHelper.getInstance().insert(music.artist,music.name,music.album);
								}
							} catch (Exception e) {
								LogUtil.e("music", "收藏歌曲错误"+LogUtil.getLineInfo());
							}
						}
					}, "好的"+",请欣赏，"+musics.get(0).artist+"的"+musics.get(0).name, question);
				}
			}
		});
	}
	
	/**
	 * 通过歌词搜索歌曲
	 *（歌词来源：客户处理过后的数据且result.equals("-1")||result.equals("0")）
	 * @param lrc
	 */
	public void playOnlineMusicBylrc(final String lrc){
		LogUtil.e("musicstate","lrc:"+lrc+LogUtil.getLineInfo());
		list.clear();
		mKwSdk.searchOnlineMusicBylrc(lrc, new OnSearchListener() {
			@Override
			public void searchFinshed(SearchStatus status, boolean first,
					List<Music> musics, boolean overTime) {
				if(status==SearchStatus.SUCCESS&&musics.size()>0){
					Music music = musics.get(0);
					String name = music.name;
					name=name.replace(",", "");
					String artist=music.artist;
					list.add(name);
					list.add(artist);
					LogUtil.e("musicstate", "name:"+name+"  artist:"+artist+LogUtil.getLineInfo());
					mTts.doSomethingAfterTts(null,"您是想听"+artist+"演唱的"+clearBracket(name)+"吗？您可以说:\"是的\"或者\"取消\"!",lrc);
					//					sendMainServiceBoardcast("[manyspeech]" + "" + "@@" +  "您是想听"+artist+"演唱的"+clearBracket(name)+"吗？");//在tempansweractivity（圆球回答界面）中显示的字符串
				}else{
//					mTts.doSomethingAfterTts(null,"抱歉,我没有听懂,或许换个说法我就听明白了", question);
				}
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
		});
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
}
