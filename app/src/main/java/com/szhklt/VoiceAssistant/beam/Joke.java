package com.szhklt.VoiceAssistant.beam;

public class Joke {
	private String titile;
	private String tag;
	private String mp3Url;
	private String content;
	public String getTitile(){
    	return titile;
    }
    public void setTitile(String titile){
    	this.titile=titile;   
    }
    public String getTag(){
    	return tag;
    }
    public void setTag(String tag){
    	this.tag=tag;   
    }
    public void setcontent(String content){
    	this.content=content;
    }
    public String getcontent(){
    	return content;
    }
    public String getMp3Url(){
    	return mp3Url;
    }
    public void setMp3Url(String mp3Url){
    	this.mp3Url=mp3Url;   
    }

}
