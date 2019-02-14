package com.szhklt.www.voiceassistant.RzMusicPkg;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.text.TextUtils;

import com.szhklt.www.voiceassistant.beam.Song;
import com.szhklt.www.voiceassistant.util.HttpCallBack;
import com.szhklt.www.voiceassistant.util.HttpUtil;
import com.szhklt.www.voiceassistant.util.HttpUtilParamTool;
import com.szhklt.www.voiceassistant.util.LogUtil;

public class KGHandler {
	public static String TAG = "KGHandler";	
	public HttpUtil httpUtil;
	
	public KGHandler(){
		httpUtil = new HttpUtil();
	}

	/**
	 * 搜索歌曲
	 * @param songname
	 * @param songsinger
	 */
	public void search(final String songname,final String songsinger,final SongInfoCallBack callback2){
		new Thread() {
			@Override
			public void run() {
				final HttpUtilParamTool mHttpUtilParamTool = HttpUtilParamTool.createMusic(30,1,songname,1);
				httpUtil.post("http://mobilecdn.kugou.com/api/v3/search/song?", mHttpUtilParamTool.getParams(), new HttpCallBack() {
					@Override
					public void onFailed(int error, String errorMsg) {
						LogUtil.d(TAG,"返回结果="+errorMsg);
						callback2.onFailed(error, errorMsg);
					}
					@Override
					public void onSuccess(String res) {
						Song mSong = null;
						LogUtil.d(TAG,"酷狗查找成功");
						try {
							JSONObject obj = new JSONObject(res);
							JSONObject data = obj.getJSONObject("data");
							JSONArray info = data.getJSONArray("info");
							for (int i = 0; i < info.length(); i++) {
								JSONObject s = info.getJSONObject(i);
								Song song = Song.parse(s);
								if (song.filename.contains(songname) && song.filename.contains(songsinger)) {
									mSong = song;
									break;
								}
							}
							if(mSong == null){
								for (int i = 0; i < info.length(); i++) {
									JSONObject s = info.getJSONObject(i);
									Song song = Song.parse(s);
									if (song.filename.contains(songname) ) {
										mSong = song;
										break;
									}
								}
							}
							if(mSong == null){
								if(info.length() > 0){
									JSONObject s = info.getJSONObject(0);
									mSong = Song.parse(s);
								}
							}
						}catch (Exception e){
							e.printStackTrace();
						}
						if(mSong!=null){
							getSongInfo(mSong, callback2);
						}
					}
				});
			}
		}.start();
	}
	
	public void search2(String songname, String songsinger,HttpResultCallBack callback) throws UnsupportedEncodingException{
		List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
		paramsList.add(new BasicNameValuePair("pagesize","30"));
		paramsList.add(new BasicNameValuePair("page","1"));
		paramsList.add(new BasicNameValuePair("iscorrent","1"));
		paramsList.add(new BasicNameValuePair("keyword",songname));
		
		//组装参数
		StringBuilder paramStr = new StringBuilder();
		for(NameValuePair pair : paramsList){
			if(!TextUtils.isEmpty(paramStr)){
				paramStr.append("&");
			}
			paramStr.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			paramStr.append("=");
			paramStr.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		
		//将参数写入到输出流
		LogUtil.e(TAG,"paramStr.toString():"+paramStr.toString()+LogUtil.getLineInfo());
		try {
			sendRequest("http://mobilecdn.kugou.com/api/v3/search/song?"+paramStr.toString(), paramsList,callback);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void sendRequest(String url, List<NameValuePair> paramsList,HttpResultCallBack callback) throws IOException{
		InputStream is = null;
		try{
			URL newUrl = new URL(url);
			HttpURLConnection conn = (HttpURLConnection)newUrl.openConnection();
			conn.setReadTimeout(3000);
			conn.setConnectTimeout(3000);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Connection", "Keep-Alive");
			
//			List<NameValuePair> paramsList = new ArrayList<NameValuePair>();
//			paramsList.add(new BasicNameValuePair("pagesize","30"));
//			paramsList.add(new BasicNameValuePair("page","1"));
//			paramsList.add(new BasicNameValuePair("keyword","吻别"));
//			paramsList.add(new BasicNameValuePair("iscorrent","1"));
//			writeParams(conn.getOutputStream(), paramsList);
			
			//发起请求
			conn.connect();
			is = conn.getInputStream();
			//获取结果
			String result = convertStreamToString(is);
//			LogUtil.e(TAG,"### 请求结果 ：" + result);
			callback.onComplete(result);
		}finally{
			if(is != null){
				is.close();
			}
		}
	}
	
	private String writeParams(OutputStream output, List<NameValuePair> paramsList) throws IOException{
		StringBuilder paramStr = new StringBuilder();
		for(NameValuePair pair : paramsList){
			if(!TextUtils.isEmpty(paramStr)){
				paramStr.append("&");
			}
			paramStr.append(URLEncoder.encode(pair.getName(), "UTF-8"));
			paramStr.append("=");
			paramStr.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
		}
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output, "UTF-8"));
		//将参数写入到输出流
		LogUtil.e(TAG,"paramStr.toString():"+paramStr.toString()+LogUtil.getLineInfo());
		writer.write(paramStr.toString());
		writer.flush();
		writer.close();
		return paramStr.toString();
	}
	
	private String convertStreamToString(InputStream is) throws IOException{
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();
		String line = null;
		try {
			while((line = reader.readLine()) != null){
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return sb.toString();
	}
	
	/**
	 * 获取歌曲信息
	 * @param song
	 */
    public void getSongInfo(Song song,final SongInfoCallBack callback){
    	LogUtil.e("dsplay","切换歌曲!!");
    	final Song tempSong =song;
        new Thread() {
            @Override
            public void run() {
                final HttpUtilParamTool mHttpUtilParamTool = HttpUtilParamTool.createSong( "playInfo",tempSong.hash);
                httpUtil.post("http://m.kugou.com/app/i/getSongInfo.php?", mHttpUtilParamTool.getParams(), new HttpCallBack() {
                    @Override
                    public void onFailed(int error, String errorMsg) {
                        LogUtil.d(TAG,errorMsg+"====errorMsg");
                    }
                    @Override
                    public void onSuccess(String res) {
                        LogUtil.d(TAG,"搜索到的歌曲:"+res);
                        LogUtil.d("dsplay","搜索到的歌曲:"+res);
						JSONObject mJSONObject;
						String URL="";
						try {
							mJSONObject = new JSONObject(res);
							URL=mJSONObject.optString("url");
							tempSong.url = URL;
							LogUtil.e("dsplay","line:"+LogUtil.getLineInfo());
							String image=mJSONObject.optString("imgUrl");
							if(image.indexOf("{size}")!=-1){
								image=image.replace("{size}", "200");
							}
							tempSong.imgUrl = image;
							callback.onComplete(tempSong);
						} catch (JSONException e) {
							e.printStackTrace();
						}
                    }
                });
            }
        }.start();
    }
    
    public interface HttpResultCallBack{
    	void onComplete(String result);
    }
    
    public interface SongInfoCallBack{
    	void onComplete(Song result);
    	void onFailed(int error, String errorMsg);
    }
}
