package com.szhklt.www.VoiceAssistant.beam;

import com.szhklt.www.VoiceAssistant.beam.intent.Answer;
import com.szhklt.www.VoiceAssistant.beam.intent.Data;

/**
 * 语义理解结果封装
 *
 */
public class SemanticUnderstandResultData {

	private String xfsmartnoom;
	private String xfsmartname;
	private String xfsmartaction;
	private String action;
	private String device;
	private String family;
	private int rc;
	private String text;
	private String vendor;
	private String service;
	private String dialog_stat;
	public Answer awr;// 回答
	public Data dat;// data段
	public String menu;// 做菜方法
	public String news;// 新闻播报
	public String fms;
	public String jokes;// 讲笑话
	public String poetrys;// 诗词对答
	
	private String songTheme;// 歌曲主题（根据字段tags和genre来判断）
	private String songName;// 歌曲名字
	private String songAlbum;// 专辑名
	private String singerName;// 歌手/乐队名字
	public String audiopath;
	public String picture;
	public Object MusicX;
	public String translation;
	// 闹钟

	public String intent;
	public String suggestDatetime;
	public String datetime;
	public String Cvalue;
	// 自定义
	private String vflag;
	private String mflag;
	private String VolumeText;
	private String volume_name;
	private String volume_act;
	private String volume;
	private String lighttext;
	private String lflag;
	private String kflag;
	public String type;
	public String min;
	public String sec;
	public String storyurl;
	public String musicintent;
	public String DramaUrl;
	public String CrossTalk;
	public String StoryTelling;
	public String Health;
	public String History;
	public String Websearch;
	public String scheduleXaction;

	public SemanticUnderstandResultData() {
		awr = new Answer();
		dat = new Data();
	}

	public String getscheduleXaction() {
		return scheduleXaction;
	}

	public void setscheduleXaction(String scheduleXaction) {
		this.scheduleXaction = scheduleXaction;
	}

	public String getWebsearch() {
		return Websearch;
	}

	public void setWebsearch(String Websearch) {
		this.Websearch = Websearch;
	}

	public String getHistory() {
		return History;
	}

	public void setHistory(String History) {
		this.History = History;
	}

	public String getHealth() {
		return Health;
	}

	public void setHealth(String Health) {
		this.Health = Health;
	}

	public String getStoryTelling() {
		return StoryTelling;
	}

	public void setStoryTelling(String StoryTelling) {
		this.StoryTelling = StoryTelling;
	}

	public String getCrossTalk() {
		return CrossTalk;
	}

	public void setCrossTalk(String CrossTalk) {
		this.CrossTalk = CrossTalk;
	}

	public String getDramaUrl() {
		return DramaUrl;
	}

	public void setDramaUrl(String DramaUrl) {
		this.DramaUrl = DramaUrl;
	}

	public String getFm() {
		return fms;
	}

	public void setFm(String fms) {
		this.fms = fms;
	}

	public String getmusicintent() {
		return musicintent;
	}

	public void setmusicintent(String musicintent) {
		this.musicintent = musicintent;
	}

	public String gettranslation() {
		return translation;
	}

	public void settranslation(String translation) {
		this.translation = translation;
	}

	public String getStoryUrl() {
		return storyurl;
	}

	public void setStoryUrl(String storyurl) {
		this.storyurl = storyurl;
	}

	public String getxfsmartnoom() {
		return xfsmartnoom;
	}

	public void setxfsmartnoom(String xfsmartnoom) {
		this.xfsmartnoom = xfsmartnoom;
	}

	public String getxfsmartname() {
		return xfsmartname;
	}

	public void setxfsmartname(String xfsmartname) {
		this.xfsmartname = xfsmartname;
	}

	public String getxfsmartaction() {
		return xfsmartaction;
	}

	public void setxfsmartaction(String xfsmartaction) {
		this.xfsmartaction = xfsmartaction;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getDevice() {
		return device;
	}

	public void setDevice(String device) {
		this.device = device;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getMin() {
		return min;
	}

	public void setMin(String min) {
		this.min = min;
	}

	public String getSec() {
		return sec;
	}

	public void setSec(String sec) {
		this.sec = sec;
	}

	public String getSongName() {
		return songName;
	}

	public void setSongName(String songName) {
		this.songName = songName;
	}

	public String getAudiopath() {
		return audiopath;
	}

	public void setAudiopath(String audiopath) {
		this.audiopath = audiopath;
	}

	public String getSingerName() {
		return singerName;
	}

	public void setSingerName(String singerName) {
		this.singerName = singerName;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	// 做菜方法
	public String getMenu() {
		return menu;
	}

	public void setMenu(String menu) {
		this.menu = menu;
	}

	// 新闻播报
	public String getNews() {
		return news;
	}

	public void setNews(String news) {
		this.news = news;
	}

	// 讲笑话
	public String getJokes() {
		return jokes;
	}

	public void setJokes(String jokes) {
		this.jokes = jokes;
	}

	// 诗词对答
	public String getPoetrys() {
		return poetrys;
	}

	public void setPoetrys(String poetrys) {
		this.poetrys = poetrys;
	}

	public int getRc() {
		return rc;
	}

	public void setRc(int rc) {
		this.rc = rc;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getVendor() {
		return vendor;
	}

	public void setVendor(String vendor) {
		this.vendor = vendor;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getDialog_stat() {
		return dialog_stat;
	}

	public void setDialog_stat(String dialog_stat) {
		this.dialog_stat = dialog_stat;
	}

	// 自定义
	public void setVflag(String vflag) {
		// TODO Auto-generated method stub
		this.vflag = vflag;

	}

	public String getvflag() {
		// TODO Auto-generated method stub
		return vflag;
	}

	public void setLflag(String lflag) {
		// TODO Auto-generated method stub
		this.lflag = lflag;
	}

	public void setLightText(String lighttext) {
		// TODO Auto-generated method stub
		this.lighttext = lighttext;
	}

	public String getLflag() {
		// TODO Auto-generated method stub
		return lflag;
	}

	public String getLightText() {
		// TODO Auto-generated method stub
		return lighttext;
	}

	// 自定义音乐
	public void setMflag(String mflag) {
		// TODO Auto-generated method stub
		this.mflag = mflag;
	}

	public void setMusicText(String mquery) {
	}

	public String getMflag() {
		// TODO Auto-generated method stub
		return mflag;
	}

	// 自定义音量
	public void setVolumeText(String volumetext) {
		// TODO Auto-generated method stub
		this.VolumeText = volumetext;
	}

	public String getVolumeText() {
		return VolumeText;
	}

	public void setvloume_name(String volume_name) {
		// TODO Auto-generated method stub
		this.volume_name = volume_name;
	}

	public void setvolume_act(String volume_act) {
		// TODO Auto-generated method stub
		this.volume_act = volume_act;
	}

	public void setvolume(String volume) {
		// TODO Auto-generated method stub
		this.volume = volume;

	}

	public String getvolume() {
		return volume;
	}

	public String getvolume_act() {
		return volume_act;
	}

	public String getvolume_name() {
		return volume_name;
	}

	public void setKflag(String kflag) {
		// TODO Auto-generated method stub
		this.kflag = kflag;
	}

	public void setKeyText(String keytext) {
	}

	public String getKflag() {
		// TODO Auto-generated method stub
		return kflag;
	}

	// 闹钟
	public String getintent() {
		return intent;
	}

	public void setintent(String intent) {
		this.intent = intent;
	}

	public String getdatetime() {
		return datetime;
	}

	public void setdatetime(String datetime) {
		this.datetime = datetime;
	}

	public String getsuggestDatetime() {
		return suggestDatetime;
	}

	public void setsuggestDatetime(String suggestDatetime) {
		this.suggestDatetime = suggestDatetime;
	}

	public void setCvalue(String value) {
		// TODO Auto-generated method stub
		this.Cvalue = value;
	}

	public String getCvalue() {
		return Cvalue;
	}

	public String getSongTheme() {
		return songTheme;
	}

	public void setSongTheme(String songTheme) {
		this.songTheme = songTheme;
	}

	public String getSongAlbum() {
		return songAlbum;
	}

	public void setSongAlbum(String songAlbum) {
		this.songAlbum = songAlbum;
	}
}