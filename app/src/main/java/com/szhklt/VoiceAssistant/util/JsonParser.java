package com.szhklt.VoiceAssistant.util;

import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.szhklt.VoiceAssistant.beam.CrossTalk;
import com.szhklt.VoiceAssistant.beam.Drama;
import com.szhklt.VoiceAssistant.beam.FM;
import com.szhklt.VoiceAssistant.beam.Health;
import com.szhklt.VoiceAssistant.beam.History;
import com.szhklt.VoiceAssistant.beam.Joke;
import com.szhklt.VoiceAssistant.beam.Mediaplay;
import com.szhklt.VoiceAssistant.beam.MusicX;
import com.szhklt.VoiceAssistant.beam.News;
import com.szhklt.VoiceAssistant.beam.SemanticUnderstandResultData;
import com.szhklt.VoiceAssistant.beam.Story;
import com.szhklt.VoiceAssistant.beam.StoryTelling;
import com.szhklt.VoiceAssistant.beam.Weather;
import com.szhklt.VoiceAssistant.beam.Websearch;

/** 
 * 结果解析类 
 */ 
public class JsonParser { 

	public ArrayList<MusicX> MusicXUnderstander(String json) {// 所有的音乐都在这儿解析并存入list 
		JSONObject jsonData = null; 
		ArrayList<MusicX> musiclist = new ArrayList<MusicX>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonobject = new JSONObject(tokener); 
			if (jsonobject.optString("service").indexOf("musicX") != -1) {// 音乐1 
				jsonData = jsonobject.optJSONObject("data"); 
			} else if (jsonobject.getString("service").indexOf( 
					"musicPlayer_smartHome") != -1) {// 音乐2 
				JSONArray moreResultsArray = jsonobject.getJSONArray("moreResults"); 
				JSONObject dataObject1 = (JSONObject) moreResultsArray.get(0); 
				jsonData = dataObject1.optJSONObject("data"); 
			} 
			if (null != jsonData) { 
				JSONArray jsonArray1 = jsonData.getJSONArray("result"); 
				JSONObject jsonobject1 = jsonData.optJSONObject("sem_score"); 
				for (int i = 0; i < jsonArray1.length(); i++) { 
					JSONObject dataObject = (JSONObject) jsonArray1.get(i); 
					String audiopath = dataObject.optString("audiopath"); 
					String songname = dataObject.optString("songname"); 
					JSONArray singers = dataObject.getJSONArray("singernames"); 
					MusicX musicX = new MusicX();
					if (jsonobject1 != null) { 
						JSONObject jsonobject2 = jsonobject1.optJSONObject("song"); 
						if (jsonobject2 != null) { 
							String kwsongname = jsonobject2.optString("txt"); 
							musicX.setKwsongname(kwsongname); 
						} 
						JSONObject jsonobject3 = jsonobject1.optJSONObject("artist"); 
						if (jsonobject3 != null) { 
							String kwsinger = jsonobject3.optString("txt"); 
							musicX.setKwsinger(kwsinger); 
						} 
					} 
					String singername = singers.optString(0); 
					if ("".equals(singername) || " ".equals(singername) 
							|| "  ".equals(singername)) { 
						musicX.setSingername("未知艺术家"); 
					} else { 
						musicX.setSingername(singername); 
					} 
					JSONArray pictureArray = dataObject 
							.getJSONArray("pictures"); 
					if (pictureArray.length() > 0) {// 有图片对象 
						JSONObject picObject = (JSONObject) pictureArray 
								.get(pictureArray.length() - 1); 
						if (picObject != null) {// 有图片 
							String picture = picObject.optString("path"); 
							musicX.setPicture(picture); 
						} else {// 无图 
							musicX.setPicture(""); 
						} 
					} else {// 无图片对象 
						musicX.setPicture(""); 
					} 
					if ("".equals(songname) || " ".equals(songname) 
							|| "  ".equals(songname)) { 
						musicX.setSongname("流行歌曲"); 
					} else { 
						musicX.setSongname(songname); 
					} 
					musicX.setAudiopath(audiopath); 
					musiclist.add(musicX); 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return musiclist; 
	} 

	public Weather WeatherTextUnderstande(String json) {
		Weather weather = new Weather(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsondata = jsonObject.getJSONObject("data"); 
			JSONArray jsonresult = jsondata.getJSONArray("result"); 
			JSONObject jsontoday = jsonresult.getJSONObject(0); 
			String tempRange = jsontoday.getString("tempRange"); 
			String wind = jsontoday.getString("wind"); 
			weather.setTempRange(tempRange); 
			weather.setWind(wind); 
		} catch (Exception e) { 
		} 
		return weather; 
	} 

	/** 
	 * 笑话JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Joke> JokeXUnderstander(String json) {
		ArrayList<Joke> Jokelist = new ArrayList<Joke>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("story") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String titile = dataObject.optString("title"); 
						String content = dataObject.optString("content"); 
						Joke joke = new Joke(); 
						joke.setTitile(titile); 
						joke.setcontent(content); 
						Jokelist.add(joke); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Jokelist; 
	} 

	/** 
	 * 故事JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Story> storyUnderstander(String json) {
		ArrayList<Story> Storylist = new ArrayList<Story>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("story") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("category"); 
						String playUrl = dataObject.optString("playUrl"); 
						Story story = new Story(); 
						story.setName(name); 
						story.setAuthor(author); 
						story.setPlayUrl(playUrl); 
						Storylist.add(story); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Storylist; 
	} 

	/** 
	 * 戏曲JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Drama> xiquUnderstander(String json) {
		ArrayList<Drama> Dramalist = new ArrayList<Drama>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("drama") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String album = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						Drama mDrama = new Drama();
						mDrama.setname(name); 
						mDrama.setalbum(album); 
						mDrama.seturl(url); 
						Dramalist.add(mDrama); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Dramalist; 
	} 

	/** 
	 * 相声小品JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<CrossTalk> xiangshenUnderstander(String json) { 
		ArrayList<CrossTalk> CrossTalklist = new ArrayList<CrossTalk>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("crossTalk") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String album = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						CrossTalk mCrossTalk = new CrossTalk();
						mCrossTalk.setname(name); 
						mCrossTalk.setalbum(album); 
						mCrossTalk.seturl(url); 
						CrossTalklist.add(mCrossTalk); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return CrossTalklist; 
	} 

	/** 
	 * 评书JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<StoryTelling> pinshuUnderstander(String json) { 
		ArrayList<StoryTelling> StoryTellinglist = new ArrayList<StoryTelling>();
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("storyTelling") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String album = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						StoryTelling mStoryTelling = new StoryTelling();
						mStoryTelling.setname(name); 
						mStoryTelling.setalbum(album); 
						mStoryTelling.seturl(url); 
						StoryTellinglist.add(mStoryTelling); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return StoryTellinglist; 
	} 



	/** 
	 * 健康知识JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Health> healthUnderstander(String json) {
		ArrayList<Health> Healthlist = new ArrayList<Health>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("health") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String album = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						Health mHealth = new Health(); 
						mHealth.setname(name); 
						mHealth.setalbum(album); 
						mHealth.seturl(url); 
						Healthlist.add(mHealth); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Healthlist; 
	} 

	/** 
	 * 历史知识JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<History> historyUnderstander(String json) {
		ArrayList<History> Historylist = new ArrayList<History>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("history") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String album = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						History mHistory = new History();
						mHistory.setname(name); 
						mHistory.setalbum(album); 
						mHistory.seturl(url); 
						Historylist.add(mHistory); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Historylist; 
	} 

	/** 
	 * 网络搜索JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Websearch> webUnderstander(String json) { 
		ArrayList<Websearch> Websearchlist = new ArrayList<Websearch>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("websearch") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String img = dataObject.optString("img"); 
						String summary = dataObject.optString("summary"); 
						String url = dataObject.optString("url"); 
						Websearch mWebsearch = new Websearch();
						mWebsearch.setname(name); 
						mWebsearch.setimg(img); 
						mWebsearch.setsummaryl(summary); 
						mWebsearch.seturl(url); 
						Websearchlist.add(mWebsearch); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Websearchlist; 
	} 
	/** 
	 * 媒体资源的解析 
	 */ 
	public ArrayList<Mediaplay> MediaXUnderstander(String json) { 
		ArrayList<Mediaplay> Medialist = new ArrayList<Mediaplay>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("drama") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				} else if (jsonObject.getString("service").indexOf("crossTalk") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				} else if (jsonObject.getString("service").indexOf("storyTelling") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("health") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("history") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("album"); 
						String url = dataObject.optString("url"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("websearch") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("summary"); 
						String url = dataObject.optString("url"); 
						String image = dataObject.optString("img"); 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay();
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("radio") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author="蜻蜓FM"; 
						//                        String description = dataObject.optString("description"); 
						String url = dataObject.optString("url"); 
						String image = dataObject.optString("img"); 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("news") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String url = dataObject.optString("url"); 
						String author = dataObject.optString("category"); 
						String newsname = dataObject.optString("title"); 
						String image = dataObject.optString("imgUrl"); 
						String detail = dataObject.optString("publishDateTime"); 
						String name = newsname.replaceAll("\\[.*?\\]", ""); 
						Mediaplay mMediaplay = new Mediaplay(); 
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setdetail(detail); 
						Medialist.add(mMediaplay); 
					} 
				}else if (jsonObject.getString("service").indexOf("story") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String author = dataObject.optString("category"); 
						String url = dataObject.optString("playUrl"); 
						String image =""; 
						String detail = ""; 
						Mediaplay mMediaplay = new Mediaplay();
						mMediaplay.setname(name); 
						mMediaplay.setauther(author); 
						mMediaplay.seturl(url); 
						mMediaplay.setimage(image); 
						mMediaplay.setimage(detail); 
						Medialist.add(mMediaplay); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return Medialist; 
	} 
	/** 
	 * 广播直播 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<FM> FMUnderstander(String json) {
		ArrayList<FM> FMlist = new ArrayList<FM>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("radio") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String name = dataObject.optString("name"); 
						String description = dataObject 
								.optString("description"); 
						String img = dataObject.optString("img"); 
						String url = dataObject.optString("url"); 
						FM mFM = new FM();
						mFM.setName(name); 
						mFM.setDescription(description); 
						mFM.setImg(img); 
						mFM.setUrl(url); 
						FMlist.add(mFM); 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return FMlist; 
	} 

	/** 
	 * 新闻的JSON解析 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<News> NewsXUnderstander(String json) {
		ArrayList<News> newslist = new ArrayList<News>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("news") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						String newspath = dataObject.optString("url"); 
						String newsauther = dataObject.optString("category"); 
						String newskeywords = dataObject.optString("keyWords"); 
						String newsname = dataObject.optString("title"); 
						String newspictrue = dataObject.optString("imgUrl"); 
						String publishDateTime = dataObject 
								.optString("publishDateTime"); 
						String test = newsname.replaceAll("\\[.*?\\]", ""); 
						News news = new News();
						news.setUrl(newspath); 
						news.setContent(newsauther); 
						news.setName(test); 
						news.setImaUrl(newspictrue); 
						news.setkeywords(newskeywords); 
						news.setpublishDateTime(publishDateTime); 
						if (news != null) { 
							newslist.add(news); 
						} 
					} 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return newslist; 
	} 

	/** 
	 * 语音语义的天气信息 
	 * 
	 * @param json 
	 * @return 
	 */ 
	public ArrayList<Weather> WeatherXUnderstander(String json) {
		ArrayList<Weather> weatherlist = new ArrayList<Weather>(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			if (null != jsonData) { 
				JSONArray jsonArray = jsonData.getJSONArray("result"); 
				if (jsonObject.getString("service").indexOf("weather") != -1) { 
					for (int i = 0; i < jsonArray.length(); i++) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(i); 
						Weather weathers = new Weather();
						weathers.setCity(dataObject.optString("city")); 
						weathers.setDate(dataObject.optString("date")); 
						weathers.setTempRange(dataObject.optString("tempRange")); 
						weathers.setWeather(dataObject.optString("weather")); 
						weathers.setWind(dataObject.optString("wind")); 
						if (weathers != null) { 
							weatherlist.add(weathers); 
						} 
					} 
				} 
			} 
		} catch (Exception e) { 
		} 
		return weatherlist; 
	} 

	/** 
	 * 语义结果解析 
	 */ 
	public SemanticUnderstandResultData parseUnderstander(String json) {
		// TODO Auto-generated method stub 
		SemanticUnderstandResultData responseMsg = new SemanticUnderstandResultData(); 
		try { 
			JSONTokener tokener = new JSONTokener(json); 
			JSONObject jsonObject = new JSONObject(tokener); 
			JSONObject jsonAnswer = jsonObject.optJSONObject("answer"); 
			JSONObject jsonData = jsonObject.optJSONObject("data"); 
			int RC = jsonObject.getInt("rc"); 
			responseMsg.setRc(RC); 
			responseMsg.setText(jsonObject.getString("text"));// cmd 
			String serviceKind = jsonObject.optString("service"); 
			responseMsg.setService(serviceKind); 
			if (null != jsonAnswer) { 
				responseMsg.awr.setAnsw(jsonAnswer.getString("text"));// ans 
			} 
			if (null != jsonData) { 
				if (jsonObject.getInt("rc") == 3) {// 快进快退 
					if (serviceKind.equals("musicX")) { 
						JSONArray jsonArray = jsonObject 
								.getJSONArray("semantic"); 
						JSONObject dataObject = (JSONObject) jsonArray.get(0); 
						JSONArray jsonArray1 = dataObject.getJSONArray("slots"); 
						JSONObject dataObject1 = (JSONObject) jsonArray1.get(0); 
						String type = dataObject1.optString("value"); 
						responseMsg.setType(type); 
						if (jsonArray1.length() == 2) { 
							JSONObject dataObject2 = (JSONObject) jsonArray1 
									.get(1); 
							String name = dataObject2.optString("name"); 
							if (name.indexOf("second") != -1) { 
								String sec = dataObject2.optString("value"); 
								responseMsg.setSec(sec); 
								responseMsg.setMin(null); 
							} else if (name.indexOf("minute") != -1) { 
								String min = dataObject2.optString("value"); 
								responseMsg.setMin(min); 
								responseMsg.setSec(null); 
							} 
						} 
						if (jsonArray1.length() == 3) { 
							JSONObject dataObject2 = (JSONObject) jsonArray1 
									.get(1); 
							String min = dataObject2.optString("value"); 
							responseMsg.setMin(min); 
							JSONObject dataObject3 = (JSONObject) jsonArray1 
									.get(2); 
							String sec = dataObject3.optString("value"); 
							responseMsg.setSec(sec); 
						} 
					} 
				} else if (jsonObject.getInt("rc") == 0) { 
					JSONArray jsonArray = jsonData.getJSONArray("result"); 
					if (jsonArray != null) { 
						JSONObject dataObject = (JSONObject) jsonArray.get(0); 

						if (serviceKind.equals("musicX")) {// 音乐 
							String audiopath = dataObject 
									.optString("audiopath"); 
							if (audiopath != null) { 
								responseMsg.setAudiopath(audiopath); 
							} 
						} else if (serviceKind.equals("news")) {// 新闻 
							String news = dataObject.optString("url"); 
							if (news != null) { 
								responseMsg.setNews(news); 
							} 
						} else if (serviceKind.equals("radio")) {// 广播 
							String fm = dataObject.optString("url"); 
							if (fm != null) { 
								responseMsg.setFm(fm); 
							} 
						} else if (serviceKind.equals("joke")) {// 笑话 
							String jokes = dataObject.optString("mp3Url"); 
							if (jokes != null) { 
								responseMsg.setJokes(jokes); 
							} 
						} else if (serviceKind.equals("story")) {// 故事 
							String storyUrl = dataObject.optString("playUrl"); 
							if (storyUrl != null) { 
								responseMsg.setStoryUrl(storyUrl); 
							} 
						} else if (serviceKind.equals("drama")) {// 戏曲 
							String DramaUrl = dataObject.optString("url"); 
							if (DramaUrl != null) { 
								responseMsg.setDramaUrl(DramaUrl); 
							} 
						} else if (serviceKind.equals("crossTalk")) {// 相声小品 
							String crossTalk = dataObject.optString("url"); 
							if (crossTalk != null) { 
								responseMsg.setCrossTalk(crossTalk); 
							} 
						} else if (serviceKind.equals("storyTelling")) {// 评书 
							String storyTelling = dataObject.optString("url"); 
							if (storyTelling != null) { 
								responseMsg.setStoryTelling(storyTelling); 
							} 
						} else if (serviceKind.equals("health")) {// 健康 
							String health = dataObject.optString("url"); 
							if (health != null) { 
								responseMsg.setHealth(health); 
							} 
						} else if (serviceKind.equals("websearch")) {// 健康 
							String websearch = dataObject.optString("url"); 
							if (websearch != null) { 
								responseMsg.setWebsearch(websearch); 
							} 
						} else if (serviceKind.equals("history")) {// 历史 
							String history = dataObject.optString("url"); 
							if (history != null) { 
								responseMsg.setHistory(history); 
							} 
						} else if (serviceKind.equals("stock")) {// 股票 
							String currentPrice = dataObject 
									.optString("currentPrice"); 
							String name = dataObject.optString("name"); 
							String riseRate = dataObject.optString("riseRate"); 
							if (riseRate.indexOf("-") != -1) { 
								responseMsg.awr 
								.setAnsw(name + "现在" + currentPrice 
										+ "元," + "跌幅" + riseRate); 
							} else { 
								responseMsg.awr 
								.setAnsw(name + "现在" + currentPrice 
										+ "元," + "涨幅" + riseRate); 
							} 
						} else if (serviceKind.equals("cookbook")) {// 菜谱 
							String menu = dataObject.optString("steps"); 
							if (menu != null) { 
								responseMsg.setMenu(menu); 
							} 
						} else if (serviceKind.equals("poetry")) {// 诗词对答 
							String poetrys = dataObject.optString("content"); 
							if (poetrys != null) { 
								responseMsg.setPoetrys(poetrys); 
							} 
						} else if (serviceKind.equals("translation")) {// 英语翻译 
							String translation = dataObject 
									.optString("translated"); 
							if (translation != null) { 
								responseMsg.settranslation(translation); 
							} 
						} 
					} else { 
						responseMsg.awr.setAnsw("哈哈！这真是个有趣的问题！人家都不知道你在讲什么啦！"); 
					} 
				} 
			} else if (serviceKind != null) {// data为空的情况 
				if ("HKLT.light_up_down".equals(serviceKind)) {// 亮度调节 
					return Light_Json_Parse(jsonObject, responseMsg); 
				} else if ("HKLT.volume_up_down".equals(serviceKind)) { 
					return Volume_Json_Parse(jsonObject, responseMsg); 
				} else if ("HKLT.keyevent".equals(serviceKind)) { 
					return Keyevent_Json_Parse(jsonObject, responseMsg); 
				} else if ("HKLT.music_ctr".equals(serviceKind)) { // 播放器控制 
					return MusicCtr_Json_Parse(jsonObject, responseMsg); 
				} else if ("HKLT.smart_home".equals(serviceKind)) {// 智能家居控制 
					return SmartHome_Json_Parse(jsonObject, responseMsg); 
				} else if (serviceKind.equals("musicX")) { 
					JSONArray Arraymusc = jsonObject.getJSONArray("semantic"); 
					if (Arraymusc != null) { 
						JSONObject musicobj = Arraymusc.getJSONObject(0); 
						String intent = musicobj.optString("intent"); 
						if (intent != null) { 
							responseMsg.setmusicintent(intent); 
						} 
						JSONArray Arrayslots = musicobj.getJSONArray("slots"); 
						if (Arrayslots.length() == 1) { 
							JSONObject slots = Arrayslots.getJSONObject(0); 
							String name = slots.optString("name"); 
							if (name != null) { 
								String value = slots.optString("value"); 
								if (value != null) { 
									if (name.equals("artist")||name.equals("band")) { 
										responseMsg.setSingerName(value); 
									} else if (name.equals("song")) { 
										responseMsg.setSongName(value); 
									} else if(name.equals("tags")||name.equals("genre")||name.equals("lang")){ 
										//将tags,genre和lang设置为主题播放 
										responseMsg.setSongTheme(value); 
									} 

								} 

							} 

						} else if (Arrayslots.length() == 2) {//当slots数组长度为2 
							JSONObject slots1 = Arrayslots.getJSONObject(0); 
							JSONObject slots2 = Arrayslots.getJSONObject(1); 
							String name1 = slots1.optString("name"); 
							String name2 = slots2.optString("name"); 
							if (name1 != null&&name2!=null) { 
								String value1 = slots1.optString("value"); 
								String value2 = slots2.optString("value"); 
								if (value1!= null&&value2!=null) { 
									if (name1.equals("artist")) { 
										responseMsg.setSingerName(value1); 
										responseMsg.setSongName(value2); 
									} else if (name1.equals("song")) { 
										responseMsg.setSongName(value1); 

										if(name2.equals("source")){ 
											LogUtil.e("wuhao","到了这里 name2.equals(source)"); 
											responseMsg.setSongAlbum(value2); 
										}else{ 
											responseMsg.setSingerName(value2); 
										}     

									} 
								} 
							} 
						}else if(Arrayslots.length() > 2) {//当slots数组长度大于2 
							JSONObject slots1 = Arrayslots.getJSONObject(0); 
							JSONObject slots2 = Arrayslots.getJSONObject(1); 
							JSONObject slots3 = Arrayslots.getJSONObject(2); 
							String name1 = slots1.optString("name"); 
							String name2 = slots2.optString("name"); 
							String name3 = slots3.optString("name"); 
							if (name1 != null && name2 != null && name3 != null) { 
								String value1 = slots1.optString("value"); 
								String value2 = slots2.optString("value"); 
								String value3 = slots3.optString("value"); 
								if (value1 != null && value2 != null&&value3!=null) { 
									if (name1.equals("artist")) { 
										responseMsg.setSingerName(value1); 
										responseMsg.setSongName(value2); 
										responseMsg.setSongAlbum(value3); 
									} else if (name1.equals("song")) { 
										responseMsg.setSongName(value1); 
										responseMsg.setSingerName(value2); 
										responseMsg.setSongAlbum(value3); 
									} 
								} 
							} 
						} 
					} 
				} else if (serviceKind.equals("datetime")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("openQA")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("calc")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("radio")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("story")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("weixin")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} else if (serviceKind.equals("weather")) { 
					if (null != jsonAnswer) { 
						responseMsg.awr.setAnsw(jsonAnswer.getString("text")); 
					} 
				} 
				/*************************** SmartHome ****************************/ 
				else if (serviceKind.equals("airVent_smartHome") && RC == 0) {// 排风扇 
					Smarthome_Json_Parse(jsonObject, responseMsg, "排风扇"); 
				} else if (serviceKind.equals("rangeHood_smartHome") && RC == 0) {// 油烟机 
					Smarthome_Json_Parse(jsonObject, responseMsg, "油烟机"); 
				} else if (serviceKind.equals("oven_smartHome") && RC == 0) {// 烤箱 
					Smarthome_Json_Parse(jsonObject, responseMsg, "烤箱"); 
				} else if (serviceKind.equals("heater_smartHome") && RC == 0) {// 取暖器，有回答 
					Smarthome_Json_Parse(jsonObject, responseMsg, "取暖器"); 
				} else if (serviceKind.equals("microwaveOven_smartHome") 
						&& RC == 0) {// 微波炉 
					Smarthome_Json_Parse(jsonObject, responseMsg, "微波炉"); 
				} else if (serviceKind.equals("freezer_smartHome") && RC == 0) {// 冰箱，有回答 
					Smarthome_Json_Parse(jsonObject, responseMsg, "冰箱"); 
				} else if (serviceKind.equals("webcam_smartHome") && RC == 0) {// 摄像头 
					Smarthome_Json_Parse(jsonObject, responseMsg, "摄像头"); 
				} else if (serviceKind.equals("fan_smartHome") && RC == 0) {// 风扇，有回答 
					Smarthome_Json_Parse(jsonObject, responseMsg, "风扇"); 
				} else if (serviceKind.equals("washer_smartHome") && RC == 0) {// 洗衣机 
					Smarthome_Json_Parse(jsonObject, responseMsg, "洗衣机"); 
				} else if (serviceKind.equals("electricKettle_smartHome") 
						&& RC == 0) {// 电水壶 
					Smarthome_Json_Parse(jsonObject, responseMsg, "电水壶"); 
				} else if (serviceKind.equals("airControl_smartHome") 
						&& RC == 0) {// 空调 
					Smarthome_Json_Parse(jsonObject, responseMsg, "空调"); 
				} else if (serviceKind.equals("tv_smartHome") && RC == 0) {// 电视机 
					Smarthome_Json_Parse(jsonObject, responseMsg, "电视机"); 
				} else if (serviceKind.equals("humidifier_smartHome") 
						&& RC == 0) {// 加湿器 
					Smarthome_Json_Parse(jsonObject, responseMsg, "加湿器"); 
				} else if (serviceKind.equals("sterilizer_smartHome") 
						&& RC == 0) {// 消毒柜 
					Smarthome_Json_Parse(jsonObject, responseMsg, "消毒柜"); 
				} else if (serviceKind.equals("bathroomMaster_smartHome") 
						&& RC == 0) {// 浴霸 
					Smarthome_Json_Parse(jsonObject, responseMsg, "浴霸"); 
				} else if (serviceKind.equals("riceCooker_smartHome") 
						&& RC == 0) {// 电饭煲 
					Smarthome_Json_Parse(jsonObject, responseMsg, "电饭煲"); 
				} else if (serviceKind.equals("slot_smartHome") && RC == 0) {// 插座 
					Smarthome_Json_Parse(jsonObject, responseMsg, "插座"); 
				} else if (serviceKind.equals("window_smartHome") && RC == 0) {// 窗户 
					Smarthome_Json_Parse(jsonObject, responseMsg, "窗户"); 
				} else if (serviceKind.equals("inductionStove_smartHome") 
						&& RC == 0) {// 电磁炉 
					Smarthome_Json_Parse(jsonObject, responseMsg, "电磁炉"); 
				} else if (serviceKind.equals("airCleaner_smartHome") 
						&& RC == 0) {// 空气净化器 
					Smarthome_Json_Parse(jsonObject, responseMsg, "空气净化器"); 
				} else if (serviceKind.equals("waterHeater_smartHome") 
						&& RC == 0) {// 热水器 
					Smarthome_Json_Parse(jsonObject, responseMsg, "热水器"); 
				} else if (serviceKind.equals("curtain_smartHome") && RC == 0) {// 窗帘 
					Smarthome_Json_Parse(jsonObject, responseMsg, "窗帘"); 
				} else if (serviceKind.equals("underFloorHeating_smartHome") 
						&& RC == 0) {// 地暖 
					Smarthome_Json_Parse(jsonObject, responseMsg, "地暖"); 
				} else if (serviceKind.equals("dishWasher_smartHome") 
						&& RC == 0) {// 洗碗机 
					Smarthome_Json_Parse(jsonObject, responseMsg, "洗碗机"); 
				} else if (serviceKind.equals("light_smartHome") && RC == 0) {// 灯 
					Smarthome_Json_Parse(jsonObject, responseMsg, "灯"); 
				} else if (serviceKind.equals("toaster_smartHome") && RC == 0) {// 面包机 
					Smarthome_Json_Parse(jsonObject, responseMsg, "面包机"); 
				} else if (serviceKind.equals("cleaningRobot_smartHome") 
						&& RC == 0) {// 扫地机器人 
					Smarthome_Json_Parse(jsonObject, responseMsg, "扫地机器人"); 
				} else if (serviceKind.equals("musicPlayer_smartHome")) {// 放在这儿是因为data为空 
					responseMsg.awr.setAnsw("好的～，叮咚和您一起听"); 
					if (null != jsonObject) { 
						JSONArray moreResultsArray = jsonObject 
								.getJSONArray("moreResults"); 
						JSONObject dataObject = (JSONObject) moreResultsArray 
								.get(0); 
						JSONObject jsonData1 = dataObject.optJSONObject("data"); 
						if (null != jsonData1) { 
							JSONArray jsonArray = jsonData1 
									.getJSONArray("result"); 
							if (jsonArray != null) { 
								JSONObject dataObject2 = (JSONObject) jsonArray 
										.get(0); 
								String audiopath = dataObject2 
										.optString("audiopath"); 
								String songname = dataObject2 
										.optString("songname"); 
								String singername = dataObject2 
										.optString("singernames"); 
								if (audiopath != null) { 
									responseMsg.setAudiopath(audiopath); 
									responseMsg.setSongName(songname); 
									responseMsg.setSingerName(singername); 
								} 
							} 
						} 
					} 
				} else if (serviceKind.equals("scheduleX")) { 
					JSONArray jsonArray = jsonObject.getJSONArray("semantic"); 
					JSONObject dataObject = (JSONObject) jsonArray.get(0); 
					String intent = dataObject.optString("intent"); 
					responseMsg.setintent(intent); 
					if (intent.equals("CREATE")) {// 创建 
						JSONArray slots = dataObject.optJSONArray("slots"); 
						if (slots != null) { 
							if (slots.length() == 3) { 
								String action = slots.getJSONObject(0) 
										.optString("value"); 
								responseMsg.setscheduleXaction(action); 
								String normValue = slots.getJSONObject(1) 
										.getString("normValue"); 
								JSONTokener ctokener = new JSONTokener( 
										normValue); 
								JSONObject cnormValue = new JSONObject(ctokener); 
								String suggestDatetime = cnormValue 
										.getString("suggestDatetime"); 
								responseMsg.setsuggestDatetime(suggestDatetime); 

							} else if (slots.length() == 2) { 
								String action = "clock"; 
								responseMsg.setscheduleXaction(action); 
								String normValue = slots.getJSONObject(0) 
										.getString("normValue"); 
								JSONTokener ctokener = new JSONTokener( 
										normValue); 
								JSONObject cnormValue = new JSONObject(ctokener); 
								String suggestDatetime = cnormValue 
										.getString("suggestDatetime"); 
								responseMsg.setsuggestDatetime(suggestDatetime); 
							} 
						} 
					} else if (intent.equals("CANCEL")) {// 取消 
						String action = "cancel"; 
						responseMsg.setscheduleXaction(action); 
					} 
				} else if (serviceKind.indexOf("cmd") != -1) { 
					responseMsg.awr.setAnsw("哈哈！这真是个有趣的问题！人家都不知道你在讲什么啦！"); 
				} 
			} 
		} catch (JSONException e) { 
			e.printStackTrace(); 
		} 
		return responseMsg; 
	} 

	/** 
	 * 讯飞智能家居控制 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @param smartname 
	 */ 
	private void Smarthome_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg, String smartname) { 
		JSONObject semanticObject = jsonObject.optJSONObject("semantic"); 
		String serviceKind = jsonObject.optString("service"); 
		JSONObject slotsObject = semanticObject.optJSONObject("slots"); 
		String attrValue = slotsObject.optString("attrValue"); 
		if (attrValue.length() < 1) { 
			attrValue = "未知"; 
		} else if (attrValue.equals("开")) { 
			if (serviceKind.equals("curtain_smartHome")) { 
				attrValue = "拉开"; 
			} else { 
				attrValue = "打开"; 
			} 
		} else if (attrValue.equals("关")) { 
			if (serviceKind.equals("curtain_smartHome")) { 
				attrValue = "合上"; 
			} else { 
				attrValue = "关闭"; 
			} 
		} else if (attrValue.equals("红") || attrValue.equals("绿") 
				|| attrValue.equals("蓝") || attrValue.equals("黄") 
				|| attrValue.equals("粉红") || attrValue.equals("白")) { 
			attrValue = attrValue + "色"; 
		} 
		JSONObject location = slotsObject.optJSONObject("location"); 
		String xfname = smartname; 
		responseMsg.setxfsmartaction(attrValue); 
		responseMsg.setxfsmartname(xfname); 
		if (location != null) { 
			String room = location.optString("room"); 
			if (room.length() > 1) { 
				responseMsg.setxfsmartnoom(room); 
			} else { 
				responseMsg.setxfsmartnoom(null); 
			} 
		} 
	} 

	/** 
	 * 音乐播放器控制 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @return 
	 * @throws JSONException 
	 */ 
	private SemanticUnderstandResultData MusicCtr_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg) throws JSONException { 
		JSONArray jsonsemantic = jsonObject.getJSONArray("semantic"); 
		JSONArray jsonslots = jsonsemantic.getJSONObject(0).getJSONArray("slots"); 
		int slotssize = jsonslots.length(); 
		String name = jsonslots.getJSONObject(0).getString("name"); 
		if (1 == slotssize) { 
			if ("musicValue".equals(name)) {// 下一首 
				String normvalue = jsonslots.getJSONObject(0).getString( 
						"normValue"); 
				if ("pre".equals(normvalue)) { 
					responseMsg.setKflag("pre"); 
				} else if ("next".equals(normvalue)) { 
					responseMsg.setKflag("next"); 
				} 
			} else if ("musicAct2".equals(name)) {// 播放 
				String normvalue = jsonslots.getJSONObject(0).getString("normValue"); 
				if ("pause".equals(normvalue)) { 
					responseMsg.setKflag("pause"); 
				} else if ("stop".equals(normvalue)) { 
					if("退出播放".equals(jsonslots.getJSONObject(0).getString("value"))){//由于讯飞的“退出”和“退出播放”不是一个意思，所以在这里进行特殊处理
						responseMsg.setKflag("stop");
					}else{
						responseMsg.setKflag("pause"); 
					}
				} else if ("start".equals(normvalue)) { 
					responseMsg.setKflag("start"); 
				} else if ("loop".equals(normvalue)) {// 顺序 
					responseMsg.setKflag("loop"); 
				} else if ("single".equals(normvalue)) {// 单曲 
					responseMsg.setKflag("single"); 
				} else if ("shuffle".equals(normvalue)) {// 随机 
					responseMsg.setKflag("shuffle"); 
				} else if ("back".equals(normvalue)) {// 退出 
					responseMsg.setKflag("back"); 
				} else if ("gosleep".equals(normvalue)) { 
					responseMsg.setKflag("sleep"); 
				} else if("reboot".equals(normvalue)){ 
					responseMsg.setKflag("reboot"); 
				}else if("shutdown".equals(normvalue)){ 
					responseMsg.setKflag("shutdown"); 
				} 
			} 
		} else if (2 == slotssize) { 
			if ("musicAct1".equals(name)) { 
				String normvalue = jsonslots.getJSONObject(1).getString( 
						"normValue"); 
				if ("next".equals(normvalue)) { 
					responseMsg.setKflag("next"); 
				} else if ("pre".equals(normvalue)) { 
					responseMsg.setKflag("pre"); 
				} 
			} 
		} 
		return responseMsg; 
	} 

	/** 
	 * 解析休眠返回 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @return 
	 * @throws JSONException 
	 */ 
	private SemanticUnderstandResultData Keyevent_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg) throws JSONException { 
		JSONArray jsonsemantic = jsonObject.getJSONArray("semantic"); 
		JSONArray jsonslots = jsonsemantic.getJSONObject(0).getJSONArray( 
				"slots");// 获取slots数组 
		int slotssize = jsonslots.length(); 
		if (2 == slotssize) { 
			String normvalue2 = jsonslots.getJSONObject(1).getString( 
					"normValue"); 
			String value2 = jsonslots.getJSONObject(1).getString("value"); 
			if ("home".equals(normvalue2)) { 
				if ("休眠".equals(value2)) { 
					responseMsg.setKflag("sleep"); 
				} else { 
					responseMsg.setKflag("return2man"); 
				} 
			} 
		} 
		return responseMsg; 
	} 

	/** 
	 * 智捷通展会的 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @return 
	 * @throws JSONException 
	 */ 
	private SemanticUnderstandResultData SmartHome_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg) throws JSONException { 
		JSONArray jsonsemantic = jsonObject.getJSONArray("semantic"); 
		JSONArray jsonslots = jsonsemantic.getJSONObject(0).getJSONArray( 
				"slots");// 获取slots数组 
		if (jsonslots.getJSONObject(0).getString("name").equals("action")) { 
			String action = jsonslots.getJSONObject(0).getString("normValue"); 
			String device = jsonslots.getJSONObject(1).getString("value"); 
			String family = jsonslots.getJSONObject(1).getString("normValue"); 
			responseMsg.setAction(action); 
			responseMsg.setDevice(device); 
			responseMsg.setFamily(family); 
		} else if (jsonslots.getJSONObject(0).getString("name") 
				.equals("device")) { 
			String action = jsonslots.getJSONObject(1).getString("normValue"); 
			String family = jsonslots.getJSONObject(0).getString("normValue"); 
			String device = jsonslots.getJSONObject(0).getString("value"); 
			responseMsg.setAction(action); 
			responseMsg.setDevice(device); 
			responseMsg.setFamily(family); 
		} 
		return responseMsg; 
	} 

	/** 
	 * 解析亮度控制 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @return 
	 * @throws JSONException 
	 */ 
	private SemanticUnderstandResultData Light_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg) throws JSONException { 
		JSONArray jsonsemantic = jsonObject.getJSONArray("semantic"); 
		JSONArray jsonslots = jsonsemantic.getJSONObject(0).getJSONArray( 
				"slots");// 获取slots数组 
		int slotsSize = jsonslots.length();// 获取slotsSize 
		/* 判断数组长度 */ 
		if (slotsSize == 2) {// {light}{light_name} || {light_name}{light}|| 
			// {light_name}{light2}||{light2}{light_name} 
			String name1 = jsonslots.getJSONObject(0).getString("name"); 
			String name2 = jsonslots.getJSONObject(1).getString("name"); 
			if ("lightName".equals(name1)) {// 亮度10，亮度小一点 
				String light = jsonslots.getJSONObject(1).getString("name"); 
				if ("light".equals(light)) {// 亮度10 
					String normvalue2 = jsonslots.getJSONObject(1).getString( 
							"normValue"); 
					normvalue2 = normvalue2.replaceAll("\\%", ""); 
					if (IsNumeric(normvalue2)) { 
						responseMsg.setLflag(normvalue2); 
					} 
				} else if ("light2".equals(light)) {// 亮度减小一点 
					String normvalue2 = jsonslots.getJSONObject(1).getString( 
							"normValue"); 
					normvalue2 = normvalue2.replaceAll("\\%", ""); 
					if (normvalue2.indexOf("L") != -1) { 
						responseMsg.setLflag("-"); 
					} else if (normvalue2.indexOf("H") != -1) { 
						responseMsg.setLflag("+"); 
					} else if (IsNumeric(normvalue2)) { 
						responseMsg.setLflag(normvalue2); 
					} 
				} 
			} else if ("lightName".equals(name2)) {// 10音量，小一点音量 
				String light = jsonslots.getJSONObject(0).getString("name"); 
				if ("light".equals(light)) { 
					String normValue1 = jsonslots.getJSONObject(0).getString( 
							"normValue"); 
					normValue1 = normValue1.replaceAll("\\%", ""); 
					if (IsNumeric(normValue1)) { 
						responseMsg.setLflag(normValue1); 
					} 
				} else if ("light2".equals(light)) { 
					String normValue1 = jsonslots.getJSONObject(0).getString( 
							"normValue"); 
					normValue1 = normValue1.replaceAll("\\%", ""); 
					if (normValue1.indexOf("H") != -1) { 
						responseMsg.setLflag("+"); 
					} else if (normValue1.indexOf("L") != -1) { 
						responseMsg.setLflag("-"); 
					} 
				} 
			} 
		} else if (slotsSize == 3) { 
			String lnormvlaue2 = jsonslots.getJSONObject(1).getString( 
					"normValue"); 
			lnormvlaue2 = lnormvlaue2.replaceAll("\\%", ""); 
			if ("T".equals(lnormvlaue2)) { 
				String normValue3 = jsonslots.getJSONObject(2).getString( 
						"normValue"); 
				normValue3 = normValue3.replaceAll("\\%", ""); 
				if (IsNumeric(normValue3) == true) { 
					responseMsg.setLflag(normValue3); 
				} 
			} else if ("H".equals(lnormvlaue2)) { 
				String normValue3 = jsonslots.getJSONObject(2).getString( 
						"normValue"); 
				normValue3 = normValue3.replaceAll("\\%", ""); 
				if (IsNumeric(normValue3)) { 
					responseMsg.setLflag("+" + normValue3); 
				} 
			} else if ("L".equals(lnormvlaue2)) { 
				String normValue3 = jsonslots.getJSONObject(2).getString( 
						"normValue"); 
				normValue3 = normValue3.replaceAll("\\%", ""); 
				if (IsNumeric(normValue3)) { 
					responseMsg.setLflag("-" + normValue3); 
				} 
			} 
		} 
		return responseMsg; 
	} 

	/** 
	 * 音量控制解析 
	 * 
	 * @param jsonObject 
	 * @param responseMsg 
	 * @return 
	 * @throws JSONException 
	 */ 
	private SemanticUnderstandResultData Volume_Json_Parse(JSONObject jsonObject, 
			SemanticUnderstandResultData responseMsg) throws JSONException { 
		JSONArray jsonsemantic = jsonObject.getJSONArray("semantic"); 
		JSONArray jsonslots = jsonsemantic.getJSONObject(0).getJSONArray( 
				"slots");// 获取slots数组 
		int slotsSize = jsonslots.length();// 获取slotsSize 
		String name1 = jsonslots.getJSONObject(0).getString("name"); 
		String name2 = jsonslots.getJSONObject(1).getString("name"); 
		if (2 == slotsSize) {// 音量30,30音量,yl30%,声音30 
			if (name1.equals("volumeName")) { 
				String volume2 = jsonslots.getJSONObject(1).getString("name"); 
				if ("volume2".equals(volume2)) {// 音量增加一点 
					String normValue2 = jsonslots.getJSONObject(1).getString( 
							"normValue"); 
					normValue2 = normValue2.replaceAll("\\%", ""); 
					if (normValue2.indexOf("H") != -1) { 
						responseMsg.setVflag("+"); 
					} else if (normValue2.indexOf("L") != -1) { 
						responseMsg.setVflag("-"); 
					} else if (IsNumeric(normValue2)) {// 音量最大 
						responseMsg.setVflag(normValue2); 
					} 
				} else if ("volume".equals(volume2)) {// 音量10 
					String normValue2 = jsonslots.getJSONObject(1).getString( 
							"normValue"); 
					normValue2 = normValue2.replaceAll("\\%", ""); 
					if (IsNumeric(normValue2)) { 
						responseMsg.setVflag(normValue2); 
					} 
				} 
			} else if (name2.equals("volumeName")) {// 倒过来 
				String volume2 = jsonslots.getJSONObject(0).getString("name"); 
				if ("volume2".equals(volume2)) {// 增加一一点音量 
					String normValue2 = jsonslots.getJSONObject(0).getString( 
							"normValue"); 
					normValue2 = normValue2.replaceAll("\\%", ""); 
					if (normValue2.indexOf("H") != -1) { 
						responseMsg.setVflag("+"); 
					} else if (normValue2.indexOf("L") != -1) { 
						responseMsg.setVflag("-"); 
					} else if (IsNumeric(normValue2)) {// 音量100 | 0 
						responseMsg.setVflag(normValue2); 
					} 
				} else if ("volume".equals(volume2)) {// 10音量 
					String normValue2 = jsonslots.getJSONObject(0).getString( 
							"normValue"); 
					normValue2 = normValue2.replaceAll("\\%", ""); 
					if (IsNumeric(normValue2)) { 
						responseMsg.setVflag(normValue2); 
					} 
				} 
			} 
		} else if (3 == slotsSize) { 
			String volumename = jsonslots.getJSONObject(0).getString("name"); 
			if ("volumeName".equals(volumename)) { 
				String normValue2 = jsonslots.getJSONObject(1).getString( 
						"normValue"); 
				normValue2 = normValue2.replaceAll("\\%", ""); 
				if (normValue2.indexOf("L") != -1) { 
					String normValue3 = jsonslots.getJSONObject(2).getString( 
							"normValue"); 
					normValue3 = normValue3.replaceAll("\\%", ""); 
					if (IsNumeric(normValue3)) { 
						responseMsg.setVflag("-" + normValue3); 
					} 
				} else if (normValue2.indexOf("H") != -1) { 
					String normValue3 = jsonslots.getJSONObject(2).getString( 
							"normValue"); 
					normValue3 = normValue3.replaceAll("\\%", ""); 
					if (IsNumeric(normValue3)) { 
						responseMsg.setVflag("+" + normValue3); 
					} 
				} else if (normValue2.indexOf("T") != -1) { 
					String normValue3 = jsonslots.getJSONObject(2).getString( 
							"normValue"); 
					normValue3 = normValue3.replaceAll("\\%", ""); 
					if (IsNumeric(normValue3)) { 
						responseMsg.setVflag(normValue3); 
					} 
				} 
			} 
		} else { 
			LogUtil.e("音量控制", "json结构不对"); 
			responseMsg.setVflag("struct_error"); 
		} 
		return responseMsg; 
	} 

	private boolean IsNumeric(String str) { 
		for (int i = 0; i < str.length(); i++) { 
			System.out.println(str.charAt(i)); 
			if (!Character.isDigit(str.charAt(i))) { 
				return false; 
			} 
		} 
		return true; 
	} 
}