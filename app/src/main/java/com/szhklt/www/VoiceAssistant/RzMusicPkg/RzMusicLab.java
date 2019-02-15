package com.szhklt.www.VoiceAssistant.RzMusicPkg;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import android.content.Context;
import com.szhklt.www.VoiceAssistant.beam.Result;

public class RzMusicLab {
	private static RzMusicLab sRzMusicLab;
	private List<Result> mRzMusics;
	private Result curData;
	private int index = -1;
	private String curName;
	private String curAuthor;
	public String getCurName() {
		return curName;
	}

	public void setCurName(String curName) {
		this.curName = curName;
	}

	public String getCurAuthor() {
		return curAuthor;
	}

	public void setCurAuthor(String curAuthor) {
		this.curAuthor = curAuthor;
	}



	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public Result getCurData() {
		return curData;
	}

	public void setCurData(Result curData) {
		this.curData = curData;
	}

	public static RzMusicLab get(Context context){
		if(sRzMusicLab == null){
			sRzMusicLab = new RzMusicLab(context);
		}
		return sRzMusicLab;
	}

	private RzMusicLab(Context context){
		mRzMusics = new ArrayList<>();

	}
	
	public void addRzMusic(Result result){
		mRzMusics.add(result);
	}

	public List<Result> getRzMusics(){
		return mRzMusics;
	}
	
	public void setRzMusics(List<Result> mRzMusics){
		this.mRzMusics = mRzMusics;
	}
	
	public void clear(){
		this.mRzMusics.clear();
		this.curData = null;
		this.index = -1;
		this.curName = null;
		this.curAuthor = null;
	}
	

	public Result getRzMusic(UUID id) {
		for (Result mRzMusic : mRzMusics) {
			if (mRzMusic.getId().equals(id)) {
				return mRzMusic;
			}
		}
		return null;
	}

	public boolean isCurData(Result tmp) {
		// TODO Auto-generated method stub
		if(tmp.getName().equals(curName) && tmp.getAuthor().equals(curAuthor)){
			return true;
		}else{
			return false;
		}
	}
}
