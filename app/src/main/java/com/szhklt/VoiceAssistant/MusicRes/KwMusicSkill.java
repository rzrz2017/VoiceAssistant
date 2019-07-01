package com.szhklt.VoiceAssistant.MusicRes;

import android.content.Intent;
import android.util.Log;

import com.szhklt.VoiceAssistant.KwSdk;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.impl.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.skill.Skill;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import cn.kuwo.autosdk.api.OnSearchListener;
import cn.kuwo.autosdk.api.SearchStatus;
import cn.kuwo.base.bean.Music;

public class KwMusicSkill extends Skill {
	private static final String TAG = "KwMusicSkill";
	private String theme = null;
	private String song = null;
	private String singer = null;
	private String album = null;

	private KwSdk mKwSdk = KwSdk.getInstance();
	public static final List<String> list=new ArrayList<>();//存储通过歌词搜索到的歌曲信息
	private String intent;

	public KwMusicSkill(intent intent) {
		super(intent);
		mintent = intent;
	}

	@Override
	protected void extractVaildInformation() {
		// TODO Auto-generated method stub
		super.extractVaildInformation();
		LogUtil.e(TAG,"extractVaildInformation()");
		if ("musicX".equals(service) == false){
			return;
		}
		intent = mintent.getSemantic().get(0).getIntent();
		LogUtil.e(TAG,"intent:"+intent+LogUtil.getLineInfo());
		final ArrayList<Slot> slots = mintent.getSemantic().get(0).getSlots();
		for(Slot slot:slots){
			if("tags".equals(slot.getName())){
				theme = slot.getValue();
				Log.e("kw","theme:"+theme);
			}

			if("artist".equals(slot.getName())){
				singer = slot.getValue();
				Log.e("kw","singer:"+singer);
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
			}

			if("source".equals(slot.getName())){
				album = slot.getValue();
				Log.e("kw","album:"+album);
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
			disposeRC3();
		}

		//随机播放
		if (intent.equals("RANDOM_SEARCH")) {
			RecommendedSong(false);
		}

		//主题播放
		if(theme != null){
			String answer="好的!为您播放"+theme+"主题的歌";
			if(theme.indexOf("儿歌")!=-1){
				answer="好的!为您播放"+theme;
			}
			mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
				@Override
				public void doSomethingsAfterTts() {
					mKwSdk.playClientMusicsByTheme(theme);
					mFWM.removeAll();
					MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
				}
			},answer,question);
			return;
		}

		LogUtil.e(TAG, "singer："+singer);
		LogUtil.e(TAG, "song："+song);
		LogUtil.e(TAG, "album："+album);

		//播放搜索到的歌曲
		mKwSdk.searchOnlineMusic(singer,song,album,new OnSearchListener() {

			@Override
			public void searchFinshed(SearchStatus status, boolean first,final List<Music> musics, boolean overTime) {
				//搜索成功
				if(status == SearchStatus.SUCCESS){

					int random = new Random().nextInt(musics.size());
					LogUtil.e(TAG,"random:"+random);
					String anwser = "请欣赏";
					if(singer != null && song != null && album != null){
						anwser = "请欣赏,"+singer+album+"专辑里的"+song;
					}else if(singer != null && song != null){
						anwser = "请欣赏,"+singer+"的"+song;
					}else if(singer != null && album != null){
						song = musics.get(random).name;
						anwser = "请欣赏,"+singer+"的"+musics.get(random).name;
					}else if(album != null && song != null){
						anwser = "请欣赏,"+album+"里的"+song;
					}else if(song != null){
						anwser = "请欣赏,"+musics.get(0).artist+"的"+song;
					}else if(album != null){
						anwser = "请欣赏,"+musics.get(0).artist+album+"专辑里的"+musics.get(0).name;
					}else if(singer != null){
						anwser = "请欣赏,"+musics.get(0).artist+"的"+musics.get(0).name;
					}

					mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
						@Override
						public void doSomethingsAfterTts() {
							mKwSdk.playClientMusics(song,singer,album);
							mFWM.removeAll();
							MyAIUI.WRITEAUDIOEABLE = false;
							LogUtil.e("now","----------------"+LogUtil.getLineInfo());
						}
					},anwser,question);
				}else{
					LogUtil.e(TAG,"搜索失败");
					afterProcessing();
				}
			}
		});
	}


	/**
	 * 关键词搜索失败后,从关键词里面看看有没有
	 * 可用的信息,并引导
	 */
	private void afterProcessing(){
		if(song != null && singer != null){
			mKwSdk.searchOnlineMusic(null, song, null, new OnSearchListener() {
				@Override
				public void searchFinshed(SearchStatus searchStatus, boolean b, List<Music> list, boolean b1) {
					if(searchStatus == SearchStatus.SUCCESS){
						mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
							@Override
							public void doSomethingsAfterTts() {
								mKwSdk.playClientMusics(list.get(0).name,list.get(0).artist,list.get(0).album);
								mFWM.removeAll();
								MyAIUI.WRITEAUDIOEABLE = false;
								LogUtil.e("now","----------------"+LogUtil.getLineInfo());
							}
						}, "抱歉没找到该歌曲,请欣赏" + list.get(0).artist + "的" + song, question);

					}else{
						mKwSdk.searchOnlineMusic(singer, null, null, new OnSearchListener() {
							@Override
							public void searchFinshed(SearchStatus searchStatus, boolean b, List<Music> list, boolean b1) {
								if(searchStatus == SearchStatus.SUCCESS){
									mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
										@Override
										public void doSomethingsAfterTts() {
											mKwSdk.playClientMusics(list.get(0).name,list.get(0).artist,list.get(0).album);
											mFWM.removeAll();
											MyAIUI.WRITEAUDIOEABLE = false;
											LogUtil.e("now","----------------"+LogUtil.getLineInfo());
										}
									}, "抱歉没找到该歌曲,请欣赏" + singer + "的" + list.get(0).name, question);

								}else{
									RecommendedSong(true);
								}
							}
						});
					}
				}
			});
		}else{
			//实在什么都没有
			RecommendedSong(false);
		}
	}

	/**
	 * 推荐歌曲
	 * @param exc
	 */
	private void RecommendedSong(boolean exc){
		String[] kwanswer = MainApplication.getContext().getResources().getStringArray(R.array.kwanswer);
		mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
			@Override
			public void doSomethingsAfterTts() {
				mKwSdk.randomPlayMusic();
				mFWM.removeAll();
				MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
			}

		},exc?"抱歉没有该歌曲,"+kwanswer[4]:""+kwanswer[3],question);
		return;
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
	 * rc 等于 3的情况下，也有可能出现上下首控制
	 */
	private void disposeRC3(){
		//rc 等于 3的情况下，也有可能出现上下首控制
		if("INSTRUCTION".equals(intent)){
			ArrayList<Slot> slots = mintent.getSemantic().get(0).getSlots();
			for(Slot s:slots){
				if("insType".equals(s.getName())){
					//回到上一曲是（past），很奇怪，讯飞就是这样*-*
					if("past".equals(s.getValue())){
						LogUtil.e("dispose","RC == 3s时，出现的下一首情况"+LogUtil.getLineInfo());
						MainApplication.getContext().sendBroadcast(new Intent("android.rzmediaplayact.action.OTHER_ACTION").putExtra("playeraction","prev"));
						mKwSdk.pre();
						send("[MediaPlayActivity]" + "pre");
						mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
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
