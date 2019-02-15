package com.szhklt.www.VoiceAssistant.RzMusicPkg;

import java.io.IOException;
import com.szhklt.www.VoiceAssistant.beam.Result;
import com.szhklt.www.VoiceAssistant.util.LogUtil;
import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;

public class MediaPlayerWrapper {
	private static final String TAG = "MediaPlayerWrapper";
	private MediaPlayer mediaPlayer;
	public static boolean getPreStatus() {
		return preStatus;
	}

	public static void setPreStatus(boolean preStatus) {
		MediaPlayerWrapper.preStatus = preStatus;
	}

	public static boolean getStatus() {
		return status;
	}

	private static boolean status;//当前播放状态
	private static boolean preStatus;
	
	private Result tmp;
	
	public Result getTmp() {
		return tmp;
	}

	public void setTmp(Result tmp) {
		this.tmp = tmp;
	}
	
	public MediaPlayerWrapper(){
		mediaPlayer = new MediaPlayer();
	}
	
	public int getDuration(){
		if(mediaPlayer != null){
			return mediaPlayer.getDuration();
		}else{
			return 0;
		}
	}
	
	public int getCurrentPosition() throws IllegalStateException{
		if(mediaPlayer != null){
			return mediaPlayer.getCurrentPosition();
		}else{
			return 0;
		}
	}
	
	
	public void setSeekBarPos(int msec){
		if(mediaPlayer != null){
			mediaPlayer.seekTo(msec);
		}
	}
	
	public boolean isPlaying(){
		try {
			if(mediaPlayer != null){
				return mediaPlayer.isPlaying();
			}else{
				return false;
			}
		} catch (IllegalStateException e) {
			// TODO: handle exception
			return false;
		}
	}
	
	/**
	 * 设置播放资源(有prepare)
	 * @param context
	 * @param result
	 */
	public void setDataSource(Context context,Result result){
		if(result == null){
			return;
		}
		if(mediaPlayer == null){
			return;
		}
		tmp = result;
		mediaPlayer.reset();
		Uri uri = Uri.parse(result.getUrl());
		try {
			mediaPlayer.setDataSource(context, uri);
			mediaPlayer.prepareAsync();
		} catch (IllegalArgumentException | SecurityException
				| IllegalStateException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void start() {
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器开始播放了---");
			status = true;
			mediaPlayer.start();
		}
	}

	public void setOnCompletionListener(OnCompletionListener completionListener) {
		if(mediaPlayer != null){
			mediaPlayer.setOnCompletionListener(completionListener);
		}
	}

	public void setOnBufferingUpdateListener(
			OnBufferingUpdateListener bufferingUpdateListener) {
		if(mediaPlayer != null){
			mediaPlayer.setOnBufferingUpdateListener(bufferingUpdateListener);
		}
	}

	public void setOnInfoListener(OnInfoListener infoListener) {
		if(mediaPlayer != null){
			mediaPlayer.setOnInfoListener(infoListener);
		}
	}

	public void setOnPreparedListener(OnPreparedListener preparedListener) {
		if(mediaPlayer != null){
			mediaPlayer.setOnPreparedListener(preparedListener);
		}
	}

	public void setOnErrorListener(OnErrorListener errorListener) {
		if(mediaPlayer != null){
			mediaPlayer.setOnErrorListener(errorListener);
		}		
	}

	public void reset() {
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器暂停了---");
			status = false;
			try {
				if(mediaPlayer.isPlaying()){
					mediaPlayer.reset();
				}
			} catch (IllegalStateException e) {
				// TODO: handle exception
				mediaPlayer = null;
				mediaPlayer = new MediaPlayer();
			}
		}
	}

	public void stop() {
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器暂停了---");
			status = false;
			try {
				mediaPlayer.stop();
			} catch (IllegalStateException e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
	}
	
	public void release(){
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器暂停了---");
			status = false;
			mediaPlayer.release();
		}
	}

	public void pause() {
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器暂停了---");
			status = false;
			mediaPlayer.pause();
		}
	}

	public void play() {
		if(mediaPlayer != null){
			LogUtil.e(TAG,"---播放器开始播放了---");
			status = true;
			mediaPlayer.start();
		}
	}
}
