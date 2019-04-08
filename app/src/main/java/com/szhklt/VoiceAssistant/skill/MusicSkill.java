package com.szhklt.VoiceAssistant.skill;


import com.szhklt.VoiceAssistant.beam.intent;
import com.szhklt.VoiceAssistant.util.LogUtil;
import java.util.ArrayList;

public class MusicSkill extends Skill{
    private static final String TAG = "MusicSkill";
    private String intent;

    private String theme = null;
    private String song = null;
    private String singer = null;
    private String album = null;
    public MusicSkill(intent intent) {
        super(intent);
    }

    @Override
    protected void extractVaildInformation() {
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
        extractVaildInformation();
        //随机播放(推荐播放)
        if (intent.equals("RANDOM_SEARCH")) {

            return;
        }

        //主题播放
        if(theme != null && song == null && singer == null && album == null){

            return;
        }

        //

    }
}
