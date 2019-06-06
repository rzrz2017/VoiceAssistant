package com.szhklt.VoiceAssistant.MusicRes;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.szhklt.VoiceAssistant.Cocheer;
import com.szhklt.VoiceAssistant.MainApplication;
import com.szhklt.VoiceAssistant.activity.RZMediaPlayActivity2;
import com.szhklt.VoiceAssistant.beam.CocheerAlbum;
import com.szhklt.VoiceAssistant.beam.CocheerBean;
import com.szhklt.VoiceAssistant.beam.Result;
import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.beam.intent.Slot;
import com.szhklt.VoiceAssistant.component.MyAIUI;
import com.szhklt.VoiceAssistant.impl.CocheerCallBack;
import com.szhklt.VoiceAssistant.impl.DoSomethingAfterTts;
import com.szhklt.VoiceAssistant.skill.Skill;
import com.szhklt.VoiceAssistant.util.LogUtil;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CocheerSkill extends Skill {
    private static final String TAG = "CocheerSkill";
    private Cocheer cocheer;

    private String theme = null;
    private String song = null;
    private String singer = null;
    private String album = null;

    public static final List<String> list=new ArrayList<>();//存储通过歌词搜索到的歌曲信息

    public CocheerSkill(intent intent) {
        super(intent);
        mintent = intent;
        cocheer = new Cocheer();
    }

    @Override
    protected void extractVaildInformation() {
        super.extractVaildInformation();
        final ArrayList<Slot> slots = mintent.getSemantic().get(0).getSlots();
        for(Slot slot:slots){
            if("tags".equals(slot.getName())){
                theme = slot.getValue();
                LogUtil.e(TAG,"theme:"+theme);
            }

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

            if("song".equals(slot.getName())){
                song = slot.getValue();
                LogUtil.e(TAG,"song:"+song);
            }

            if("source".equals(slot.getName())){
                album = slot.getValue();
                LogUtil.e(TAG,"album:"+album);
            }
        }
    }

    @Override
    public void execute() {
        LogUtil.e(TAG,"execute()"+LogUtil.getLineInfo());
        extractVaildInformation();
        //主题播放
        if(theme != null){
            String answer="好的!为您播放"+theme+"主题的歌";
            if(theme.indexOf("儿歌")!=-1){
                answer="好的!为您播放"+theme;
            }
            cocheer.sreachSongs(theme, new CocheerCallBack() {
                @Override
                public void searchSuccess(String msg) {
                    LogUtil.e(TAG,"msg:"+msg+LogUtil.getLineInfo());
                    //解析
                    Bundle bundle = buildData(msg);
                    if(bundle == null){
                        mTts.speechSynthesis("抱歉,没有主题信息!",question);
                        return;
                    }
                    mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
                        @Override
                        public void doSomethingsAfterTts() {
                            actionStart(bundle);
                            removeOnUI();
                            MyAIUI.WRITEAUDIOEABLE = false;
                            LogUtil.e("now","----------------"+LogUtil.getLineInfo());
                        }
                    },"请欣赏,"+theme+"音乐",question);
                }

                @Override
                public void searchFailed(String msg) {
                    mTts.speechSynthesis("抱歉暂时没有"+theme+"主题的版权",question);
                }
            });
            return;
        }

        if(song != null){
            cocheer.sreachSongs(song, new CocheerCallBack() {
                @Override
                public void searchSuccess(String msg) {
                    LogUtil.e(TAG,"msg:"+msg+LogUtil.getLineInfo());
                    Bundle bundle = buildData(msg);
                    if(bundle == null){
                        mTts.speechSynthesis("抱歉,没有歌曲信息!",question);
                        return;
                    }

                    mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
                        @Override
                        public void doSomethingsAfterTts() {
                            actionStart(bundle);
                            removeOnUI();
                            MyAIUI.WRITEAUDIOEABLE = false;
                            LogUtil.e("now","----------------"+LogUtil.getLineInfo());
                        }
                    },singer == null?"请欣赏,"+song:"请欣赏,"+singer+"的"+song,question);
                }

                @Override
                public void searchFailed(String msg) {
                    mTts.speechSynthesis("抱歉暂时没有"+song+"的版权",question);
                }
            });
            return;
        }

        if(album != null){
            cocheer.searchAlbum(album, new CocheerCallBack() {
                @Override
                public void searchSuccess(String msg) {
                    LogUtil.e(TAG,"msg:"+msg+LogUtil.getLineInfo());
                    int albumId = parseAlbum(msg);
                    if(albumId == -1){
                        mTts.speechSynthesis("没有找到"+album+"专辑",question);
                        return;
                    }

                    cocheer.getFollowingContentFromAlbum(String.valueOf(albumId), new CocheerCallBack() {
                        @Override
                        public void searchSuccess(String msg) {
                            LogUtil.e(TAG,"msg:"+msg+LogUtil.getLineInfo());
                            Bundle bundle = buildData(msg);
                            if(bundle == null){
                                mTts.speechSynthesis("抱歉,没有歌曲信息!",question);
                                return;
                            }
                            mTts.doSomethingAfterTts(new DoSomethingAfterTts() {
                                @Override
                                public void doSomethingsAfterTts() {
                                    actionStart(bundle);
                                    removeOnUI();
                                    MyAIUI.WRITEAUDIOEABLE = false;
                                    LogUtil.e("now","----------------"+LogUtil.getLineInfo());
                                }
                            },"请欣赏,"+album+"专辑",question);
                        }

                        @Override
                        public void searchFailed(String msg) {
                            mTts.speechSynthesis("抱歉暂时没有"+album+"的版权",question);
                        }
                    });
                }

                @Override
                public void searchFailed(String msg) {
                    mTts.speechSynthesis("抱歉暂时没有"+album+"的版权",question);
                }
            });
            return;
        }

    }

    private int parseAlbum(String msg){
        Gson mgson = new GsonBuilder().serializeNulls().create();
        CocheerAlbum ca = mgson.fromJson(msg, CocheerAlbum.class);
        if(!ca.getData().isEmpty()){
            return ca.getData().get(0).getId();
        }
        return -1;
    }

    /**
     * 删除全部UI
     */
    private void removeOnUI(){
        mFWM.removeQuestionWindow(MainApplication.getContext());
        mFWM.removeAnswerWindow(MainApplication.getContext());
//        mFWM.removeBigWindow(MainApplication.getContext());
        mFWM.bigWindow.setVisibility(View.GONE);
    }

    /**
     * 构建数据
     * @param msg
     * @return
     */
    private Bundle buildData(String msg){
        //解析
        Gson mgson = new GsonBuilder().serializeNulls().create();
        CocheerBean cb = mgson.fromJson(msg, CocheerBean.class);
        if(cb.getData().isEmpty()){
            return null;
        }

        List<Result> list = new ArrayList<>();
        for(int i = 0; i < cb.getData().size(); i++){
            String gjson = "{\"name\":"+"\""+cb.getData().get(i).getAudio_name()+"\""+"," +
                    "\"author\":"+"\""+cb.getData().get(i).getAnnouncer_nickname()+"\""+"," +
                    "\"url\":"+"\""+cb.getData().get(i).getAudio_url()+"\""+"}";

            LogUtil.e("gjson","gjson:"+gjson);
            try {
                Result obj = mgson.fromJson(gjson, Result.class);
                list.add(obj);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        Bundle bundle = new Bundle();
        if(singer != null){
            bundle.putString("singer",singer);
        }
        bundle.putSerializable("cocheer",(Serializable) list);
        return bundle;
    }

    /**
     * 携带数据启动播放器
     * @param data
     */
    private void actionStart(Bundle data){
        Intent sintent = new Intent(MainApplication.getContext(), RZMediaPlayActivity2.class);
        sintent.putExtras(data);
        sintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        MainApplication.getContext().startActivity(sintent);
    }
}
