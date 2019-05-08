package com.szhklt.VoiceAssistant.beam;

public class News {
	private String url;
	private String name;
	private String content;
	private String imgUrl;
    private String publishDateTime;
    private String keywords;
	public String getImgUrl(){
		return imgUrl;
	}
	public void setImaUrl(String imgUrl){
		this.imgUrl=imgUrl;
	}
	public String  getkeywords(){
		return keywords;
	}
	public void setkeywords(String keywords){
		this.keywords=keywords;
	}
	public String getpublishDateTime(){
		return publishDateTime;
	}
	public void setpublishDateTime(String publishDateTime){
		this.publishDateTime=publishDateTime;
	}
	public String getUrl(){
		return url;
	}
	public void setUrl(String url){
    	this.url=url;   
    }
	
	public String getName(){
		return name;
	}
	public void setName(String name){
    	this.name=name;   
    }
	
	public String getContent(){
		return content;
	}
	public void setContent(String content){
    	this.content=content;   
    }
}
