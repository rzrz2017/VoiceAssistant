package com.szhklt.www.VoiceAssistant.beam;

public class Msg {  
    public static final int MSG_RECEIVE = 0;  
    public static final int MSG_SEND = 1;  
      
    private int type;  
    private String content;  
      
    public Msg(String content, int type) {  
        this.content = content;  
        this.type = type;  
    }  
      
    public String getMessage() {  
        return content;  
    }  
    public int getType() {  
        return type;  
    }  
}  