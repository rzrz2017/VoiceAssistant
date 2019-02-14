package com.szhklt.www.voiceassistant.beam;

public class WeatherData {
	private String AnswerText;
	private String WriteInTime;
	private Integer airData;
	private String airQuality;
	private String city;
	private String data;
	private String humidity;
	private String lastUpdateTime;
	private String pm25;
	private Integer temp;
	private String tempRange;
	private String weather;
	private Integer weatherType;
	private String wind;
	private Integer windLevel;
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "AnswerText:"+AnswerText+"\n"
			+"WriteInTime:"+WriteInTime+"\n"
			+"airData:"+airData+"\n"
			+"airQuality:"+airQuality+"\n"
			+"city:"+city+"\n"
			+"data:"+data+"\n"
			+"humidity:"+humidity+"\n"
			+"lastUpdateTime:"+lastUpdateTime+"\n"
			+"pm25:"+pm25+"\n"
			+"temp:"+temp+"\n"
			+"tempRange"+tempRange+"\n"
			+"weather:"+weather+"\n"
			+"weatherType:"+weatherType+"\n"
			+"wind:"+wind+"/n"
			+"windLevel:"+windLevel+"\n";
	}
	
	//set
	public void setAnswerText(String AnswerText){
		this.AnswerText = AnswerText;
	}
	public void setWriteInTime(String WriteInTime){
		this.WriteInTime = WriteInTime;
	}
	public void setairData(Integer airData){
		this.airData = airData;
	}
	public void setairQuality(String airQuality){
		this.airQuality = airQuality;
	}
	public void setcity(String city){
		this.city = city;
	}
	public void setdata(String data){
		this.data = data;
	}
	public void sethumidity(String humidity){
		this.humidity = humidity;
	}
	public void setlastUpdateTime(String lastUpdateTime){
		this.lastUpdateTime = lastUpdateTime;
	}
	public void setpm25(String pm25){
		this.pm25 = pm25;
	}
	public void settemp(Integer temp){
		this.temp = temp;
	}
	public void settempRange(String tempRange){
		this.tempRange = tempRange;
	}
	public void setweather(String weather){
		this.weather = weather;
	}
	public void setweatherType(Integer weatherType){
		this.weatherType = weatherType;
	}
	public void setwind(String wind){
		this.wind = wind;
	}
	public void setwindLevel(Integer windLevel){
		this.windLevel = windLevel;
	}

	//get
	public String getAnswerText(){
		return AnswerText;
	}
	public String getWriteInTime(){
		return WriteInTime;
	}
	public Integer getairData(){
		return airData;
	}
	public String getairQuality(){
		return airQuality;
	}
	public String getcity(){
		return city;
	}
	public String getdata(){
		return data;
	}
	public String gethumidity(){
		return humidity;
	}
	public String getlastUpdateTime(){
		return lastUpdateTime;
	}
	public String getpm25(){
		return pm25;
	}
	public Integer gettemp(){
		return temp;
	}
	public String gettempRange(){
		return tempRange;
	}
	public String getweather(){
		return weather;
	}
	public Integer getweatherType(){
		return weatherType;
	}
	public String getwind(){
		return wind;
	}
	public Integer getwindLevel(){
		return windLevel;
	}
}
