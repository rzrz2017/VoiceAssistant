package com.szhklt.VoiceAssistant.beam;

public class Tool {
    private String name;
    private int icon;

    public Tool(int icon,String name){
        this.icon = icon;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
