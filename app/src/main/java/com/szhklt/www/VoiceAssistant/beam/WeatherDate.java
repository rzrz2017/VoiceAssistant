package com.szhklt.www.VoiceAssistant.beam;

public class WeatherDate {
	
    private static WeatherDate instance;  
	public String wind;
	public String tempRange;
	
    public static WeatherDate getInstance() {  
    if (instance == null) {  
        instance = new WeatherDate();  
    }  
    return instance;  
    } 

	public String getwind(){
		return wind;
	}
	
	public String getTempRange(){
		return tempRange;
	}
	
	public void setwind(String cwind){
		this.wind = cwind;
	}
	
	public void settempRange(String ctempRange){
		this.tempRange = ctempRange;
	}
}
