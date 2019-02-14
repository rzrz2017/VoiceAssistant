package com.szhklt.www.voiceassistant.beam;

public class Weather {
	private String city;//城市
	private String date;//日期
	private String tempRange;//温度
	private String weather;//天气
	private String wind;//风向	
	
	public String getCity(){
    	return city;
    }
    public void setCity(String city){
    	this.city=city;        	
    }
    
    public String getDate(){
    	return date; 
    }
    public void setDate(String date){
    	this.date=date;        	
    }
    
    public String getTempRange(){
    	return tempRange; 
    }
    public void setTempRange(String tempRange){
    	this.tempRange=tempRange;        	
    }
    
    public String getWeather(){
    	return weather; 
    }
    public void setWeather(String weather){
    	this.weather=weather;        	
    }
    
    public String getWind(){
    	return wind; 
    }
    public void setWind(String wind){
    	this.wind=wind;        	
    }
}
