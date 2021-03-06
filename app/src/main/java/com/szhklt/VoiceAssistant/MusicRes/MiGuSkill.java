package com.szhklt.VoiceAssistant.MusicRes;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.gson.Gson;
import com.rich.czlylibary.bean.MusicInfo;
import com.rich.czlylibary.bean.RecommendMusic;
import com.rich.czlylibary.bean.SearchSongNewResult;
import com.rich.czlylibary.bean.SearchTagSongNewResult;
import com.rich.czlylibary.bean.SingerNew;
import com.rich.czlylibary.bean.SongNew;
import com.rich.czlylibary.sdk.ResultCallback;
import com.szhklt.VoiceAssistant.impl.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.R;
import com.szhklt.VoiceAssistant.activity.RZMediaPlayActivity2;
import com.szhklt.VoiceAssistant.beam.MiGuMusicInfo;
import com.szhklt.VoiceAssistant.beam.Result;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.skill.Skill;
import com.szhklt.VoiceAssistant.util.LogUtil;
import com.szhklt.VoiceAssistant.util.MiGuSearcher;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MiGuSkill extends Skill {
    private static final String TAG = "MiGuSkill";
    private String intent;

    private String theme = null;
    private String song = null;
    private String singer = null;
    private String album = null;

    private List<Result> list = new ArrayList<>();
    private ArrayList<com.rich.czlylibary.bean.MusicInfo> mLocalMusics = new ArrayList<>();

    public MiGuSkill(intent intent) {
        super(intent);
        Log.e("測試","MiGuSkill()'constrctor");
        LogUtil.e(TAG,"MiGuSkill()"+LogUtil.getLineInfo());
        mintent = intent;
    }

    @Override
    protected void extractVaildInformation() {
        Log.e("測試","MiGuSkill()'extractVaildInformation");
        super.extractVaildInformation();
        final ArrayList<intent.Slot> slots = mintent.getSemantic().get(0).getSlots();
        intent = mintent.getSemantic().get(0).getIntent();
        LogUtil.e(TAG,"intent:"+intent+LogUtil.getLineInfo());
        for(intent.Slot slot:slots){
            //解析主题
            if("tags".equals(slot.getName())){
                theme = slot.getValue();
                LogUtil.e(TAG,"theme:"+theme);
            }

            if(theme == null){
                theme = slot.getValue();
                LogUtil.e(TAG,"theme:"+theme);
            }

            //解析歌手
            if("artist".equals(slot.getName())){
                singer = slot.getValue();
                LogUtil.e(TAG,"singer:"+singer);
            }

            if(singer == null){
                if("band".equals(slot.getName())){
                    singer = slot.getValue();
                    LogUtil.e(TAG,"band:"+singer);
                }
            }

            //解析歌曲
            if("song".equals(slot.getName())){
                song = slot.getValue();
                LogUtil.e(TAG,"song:"+song);
            }

            //解析专辑
            if("source".equals(slot.getName())){
                album = slot.getValue();
                LogUtil.e(TAG,"album:"+album);
            }
        }
    }

    @Override
    public void execute() {
        Log.e("測試","MiGuSkill()'execute");
        extractVaildInformation();

        //随机播放
        if (intent.equals("RANDOM_SEARCH")) {
            MiGuSearcher.findRecommendSong(0, 10, 10, new ResultCallback<RecommendMusic>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(RecommendMusic recommendMusic) {
                    //进入播放器播放
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("migu",(Serializable)recommendMusic.getMusicInfos());
                    bundle.putString("index",String.valueOf(new Random().nextInt(recommendMusic.getMusicInfos().size())));
                    actionStart(bundle);
                }

                @Override
                public void onFailed(String s, String s1) {

                }

                @Override
                public void onFinish() {

                }
            });
            return;
        }

        if(theme != null && song == null && singer == null && album == null){
            MiGuSearcher.searchTagMusicByKey(theme, 1, 15, 1, 1, new ResultCallback<SearchTagSongNewResult>() {
                @Override
                public void onStart() {
                    LogUtil.e(TAG,"onStart");
                }

                @Override
                public void onSuccess(SearchTagSongNewResult o) {
                    LogUtil.e(TAG,"onSuccess");
                    searchSongPlay(o.getSearchTag().getData().getResult(),"random");
                }

                @Override
                public void onFailed(String code, String failedInfo) {
                    LogUtil.e(TAG,"onFailed"+code + failedInfo);
                }

                @Override
                public void onFinish() {
                    LogUtil.e(TAG,"onFinish");
                }
            });
            return;
        }

        StringBuilder stringBuilder = new StringBuilder();
        if(song != null){
            stringBuilder.append(song);
        }
        if(singer != null){
            stringBuilder.append(singer);
        }
        if(album != null){
            stringBuilder.append(album);
        }
        LogUtil.e(TAG,"===================="+stringBuilder.toString());
        MiGuSearcher.searchMusicByKey(stringBuilder.toString(), 1, 15, 1, 1, 1,
            new ResultCallback<SearchSongNewResult>() {
                @Override
                public void onStart() {

                }

                @Override
                public void onSuccess(SearchSongNewResult o) {
                    if(song != null){
                        searchSongPlay(o.getSearchSong().getData().getResult(),"0");
                    }else{
                        searchSongPlay(o.getSearchSong().getData().getResult(),"random");
                    }
                }

                @Override
                public void onFailed(String code, String failedInfo) {
                    mTts.speechSynthesis("抱歉,没有找到该歌曲",question);
                    Log.e(TAG,"查询歌曲失败");
                }

                @Override
                public void onFinish() {

                }
            });
    }

    /**
     * 推荐歌曲
     * @param exc
     */
    private void RecommendedSong(boolean exc){
        Log.e("測試","MiGuSkill()'RecommendedSong");
        String[] kwanswer = MainApplication.getContext().getResources().getStringArray(R.array.kwanswer);
        mTts.doSomethingAfterTts(new DoSomethingAfterTts(){
            @Override
            public void doSomethingsAfterTts() {

                mFWM.removeAll();
                MyAIUI.WRITEAUDIOEABLE = false;LogUtil.e("now","----------------"+LogUtil.getLineInfo());
            }

        },exc?"抱歉没有该歌曲,"+kwanswer[4]:""+kwanswer[3],question);
        return;
    }

    /**
     * 查询歌曲
     * @param songNews
     */
    private void searchSongPlay(SongNew[] songNews,String index) {
        Log.e("測試","MiGuSkill()'searchSongPlay");
        try {
            List<MusicInfo> musicInfoList = new ArrayList<>();
            disposeSongData(musicInfoList,songNews);
            if (musicInfoList.isEmpty()) {
//                showToast("查询歌曲成功，但是歌曲没有版权ID，不能播放");
                mTts.speechSynthesis("抱歉,暂时没有版权",question);
                LogUtil.e(TAG,"查询歌曲成功，但是歌曲没有版权ID，不能播放");
            } else {
                //进入播放器播放
                Bundle bundle = new Bundle();
                bundle.putSerializable("migu",(Serializable)musicInfoList);
                bundle.putString("index",index);
                actionStart(bundle);
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
    }

    private static List<MiGuMusicInfo> toListMusicInfo(List<com.rich.czlylibary.bean.MusicInfo> list) {
        Log.e("測試","MiGuSkill()'toListMusicInfo");
        if (list == null || list.isEmpty()) {
            return null;
        }
        List<MiGuMusicInfo> musicInfos = new ArrayList<>();
        for (com.rich.czlylibary.bean.MusicInfo info : list) {
            musicInfos.add(toMusicInfo(info));
        }
        return musicInfos;
    }

    private static MiGuMusicInfo toMusicInfo(com.rich.czlylibary.bean.MusicInfo info) {
        Log.e("測試","MiGuSkill()'toMusicInfo");
        MiGuMusicInfo musicInfo = new MiGuMusicInfo();
        musicInfo.setMusicId(info.getMusicId());
        musicInfo.setMusicName(info.getMusicName());
        musicInfo.setSingerName(info.getSingerName());
        musicInfo.setPicUrl(info.getPicUrl());
        musicInfo.setLrcUrl(info.getLrcUrl());
        musicInfo.setBmp(info.getBmp());
        musicInfo.setListenUrl(info.getListenUrl());
        musicInfo.setIsCollection(info.getIsCollection());
        musicInfo.setIsCpAuth(info.getIsCpAuth());
        return musicInfo;
    }

    /**
     * 构建数据
     * @param data
     * @return
     */
    private void addData(MusicInfo data){
        Log.e("測試","MiGuSkill()'addData");
        String gjson = "{\"name\":"+"\""+data.getMusicName()+"\""+"," +
                "\"author\":"+"\""+data.getSingerName()+"\""+"," +
                "\"id\":"+"\""+data.getMusicId()+"\""+"," +
                "\"url\":"+"\""+data.getListenUrl()+"\""+"}";

        LogUtil.e("gjson","gjson:"+gjson);
        try {
            Result obj = new Gson().fromJson(gjson, Result.class);
            list.add(obj);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 携带数据启动播放器
     * @param data
     */
    private void actionStart(Bundle data){
        Log.e("測試","MiGuSkill()'actionStart");
        Intent sintent = new Intent(MainApplication.getContext(), RZMediaPlayActivity2.class);
        sintent.putExtras(data);
        sintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApplication.getContext().startActivity(sintent);
    }

    /**
     * 处理数据
     */
    private void disposeSongData(List<MusicInfo> musicInfoList,SongNew[] songNews){
        Log.e("測試","MiGuSkill()'disposeSongData");
        for (SongNew songNew : songNews) {
            MusicInfo musicInfo = new MusicInfo();
            if (songNew.getFullSongs() != null && songNew.getFullSongs().length > 0) {
                musicInfo.setMusicId(songNew.getFullSongs()[0].getCopyrightId());
                LogUtil.e(TAG,"MusicId:"+songNew.getFullSongs()[0].getCopyrightId());
            } else {
                continue;
            }
            musicInfo.setMusicName(songNew.getName());
            LogUtil.e(TAG,"MusicName:"+songNew.getName());
            musicInfo.setSingerName(songNew.getSingers()[0].getName());
            LogUtil.e(TAG,"SingerName:"+songNew.getSingerName());
            SingerNew[] ss = songNew.getSingers();
            LogUtil.e(TAG,"SingerName:"+"ss.length:"+ss.length+ss[0].toString());
            if (songNew.getMvPic() != null && songNew.getMvPic().length != 0) {
                musicInfo.setPicUrl(songNew.getMvPic()[0].getPicPath());
                LogUtil.e(TAG,"PicUrl:"+songNew.getMvPic()[0].getPicPath());
            }
            LogUtil.e(TAG,"LrcUrl:"+songNew.getLyricUrl());
            musicInfoList.add(musicInfo);
            LogUtil.e(TAG,"=================================");
        }
    }
}
