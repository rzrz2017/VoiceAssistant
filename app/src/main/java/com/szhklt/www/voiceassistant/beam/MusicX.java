package com.szhklt.www.voiceassistant.beam;

public class MusicX {
	private String songname;
	private String audiopath;
	private String singername;
	private String picture;
	private String initdata;
	private String kwsongname;
	private String kwsinger;
	private String text;
	
	public String getSongname(){
    	return songname;
    }
    public void setSongname(String songname){
    	this.songname=songname;        	
    }	
	public String getAudiopath(){
    	return audiopath;
    }
    public void setAudiopath(String audiopath){
    	this.audiopath=audiopath;        	
    }	
    public String getSingername(){
    	return singername;
    }
    public void setSingername(String singername){
    	this.singername=singername;        	
    }
    
    public String getPicture(){
    	return picture;
    }
    public void setPicture(String picture){
    	this.picture = picture;
    }
   
    public String getInitdata(){
    	return initdata;
    }
    public void setInitdata(String initdata){
    	this.initdata=initdata;        	
    }
    public String getKwsongname(){
    	return kwsongname;
    }
    public void setKwsongname(String kwsongname){
    	this.kwsongname=kwsongname;
    }
    public String getKwsinger(){
    	return kwsinger;
    }
    public void setKwsinger(String kwsinger){
    	this.kwsinger=kwsinger;
    }
    public String getText(){
    	return text;
    }
    public void setText(String text){
    	this.text=text;
    }
  
}
