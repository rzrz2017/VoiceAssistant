package com.szhklt.www.VoiceAssistant.skill;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.szhklt.www.VoiceAssistant.SmartMsgPostMan;
import com.szhklt.www.VoiceAssistant.beam.intent;
import com.szhklt.www.VoiceAssistant.util.LogUtil;

public class OpenQASkill extends Skill{
    public static String TAG = "OpenQASkill";

    public OpenQASkill(intent intent){
        mintent = intent;
    }

    @Override
    protected void extractVaildInformation() {
        // TODO Auto-generated method stub
        super.extractVaildInformation();
    }

    public void execute() {
        extractVaildInformation();

        Gson gson = new GsonBuilder().create();
        if(answerText.startsWith("from:hklt")){
            String[] array = answerText.split(";");

            if(array != null && array.length == 2)
            {
                LogUtil.e(TAG,"array[0]="+array[0]+"array[1]="+array[1]+LogUtil.getLineInfo());
                String resp = array[1];
                String[] myAnswer = resp.split(":");

                if(myAnswer != null && myAnswer.length == 2){
                    LogUtil.e(TAG,"myAnswer[0]="+myAnswer[0]+"myAnswer[1]="+myAnswer[1]+LogUtil.getLineInfo());
                    String ans = myAnswer[1];
                    mintent.getAnswer().setText(ans);
                    SmartMsgPostMan.getInstance().sendMsg2Client(gson.toJson(mintent));LogUtil.e("postman","sendMsg2Client()"+LogUtil.getLineInfo());
                }
            }
        }else{
            LogUtil.e("openQA", "不需要转发的开放问答:"+answerText+LogUtil.getLineInfo());
            mTts.doSomethingAfterTts(mTts.new DoSomethingAfterTts(){
                @Override
                public void doSomethingsAfterTts() {
                    // TODO Auto-generated method stub
                    recoveryPlayerState();
                }
            },answerText,  question);
        }
    };

}

