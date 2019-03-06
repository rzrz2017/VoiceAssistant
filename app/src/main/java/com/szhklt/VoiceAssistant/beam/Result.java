package com.szhklt.VoiceAssistant.beam;

import java.io.Serializable;

public class Result implements Serializable{
	public static String TAG = "Result";
	public Result() {}	
	
	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getFuzzyState() {
		return fuzzyState;
	}

	public void setFuzzyState(String fuzzyState) {
		this.fuzzyState = fuzzyState;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getImgUrl() {
		return imgUrl;
	}

	public void setImgUrl(String imgUrl) {
		this.imgUrl = imgUrl;
	}

	public String getKeyWords() {
		return keyWords;
	}

	public void setKeyWords(String keyWords) {
		this.keyWords = keyWords;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPublishDateTime() {
		return publishDateTime;
	}

	public void setPublishDateTime(String publishDateTime) {
		this.publishDateTime = publishDateTime;
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	//新闻
	public String category;
	public String content;
	public String description;
	public String fuzzyState;
	public String id;
	public String imgUrl;
	public String keyWords;
	public String name;
	public String publishDateTime;
	public String source;
	public String time;
	public String title;
	public String type;
	public String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	//故事
	private String playUrl;
	public String author;
	
	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPlayUrl() {
		return playUrl;
	}

	public void setPlayUrl(String playUrl) {
		this.playUrl = playUrl;
	}
	
	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public String getAlbumUrl() {
		return albumUrl;
	}

	public void setAlbumUrl(String albumUrl) {
		this.albumUrl = albumUrl;
	}

	public String getMp3Url() {
		return mp3Url;
	}

	public void setMp3Url(String mp3Url) {
		this.mp3Url = mp3Url;
	}

	public String getMp4Url() {
		return mp4Url;
	}

	public void setMp4Url(String mp4Url) {
		this.mp4Url = mp4Url;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}
	//笑话
	private String album;
	private String albumUrl;
	private String mp3Url;
	private String mp4Url;
	private String tag;
	
	
	private String actor;
	public String getActor() {
		return actor;
	}

	public void setActor(String actor) {
		this.actor = actor;
	}

	public String getAliasName() {
		return aliasName;
	}

	public void setAliasName(String aliasName) {
		this.aliasName = aliasName;
	}

	public int getChapter() {
		return chapter;
	}

	public void setChapter(int chapter) {
		this.chapter = chapter;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}
	
	//戏曲//小品//相声
	private String aliasName;
	private int chapter;
	private String duration;
	private String webUrl;
	
	//电台
	private String city;
	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getFm() {
		return fm;
	}

	public void setFm(String fm) {
		this.fm = fm;
	}

	public String getImg() {
		return img;
	}

	public void setImg(String img) {
		this.img = img;
	}
	private String fm;
	private String img;

	private String lrcUrl;
	public void setLrcUrl(String lrcUrl){
		this.lrcUrl = lrcUrl;
	}

	public String getLrcUrl(){
		return lrcUrl;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
//		LogUtil.e(TAG,"aliasName:"+aliasName+"/n"+"name:"+name+"/n"+"title:"+title);
		return "aliasName:"+aliasName+"\n"+"name:"+name+"\n"+"title:"+title+"\n"+"playUrl:"+playUrl;
	}
}
