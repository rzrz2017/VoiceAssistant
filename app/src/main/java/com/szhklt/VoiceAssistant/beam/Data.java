package com.szhklt.VoiceAssistant.beam;

public class Data {

    private int id;
    private String audio_name;
    private String audio_url;
    private String audio_intro;
    private String announcer_nickname;
    private String audio_pic_url;
    private int audio_duration;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setAudio_name(String audio_name) {
        this.audio_name = audio_name;
    }
    public String getAudio_name() {
        return audio_name;
    }

    public void setAudio_url(String audio_url) {
        this.audio_url = audio_url;
    }
    public String getAudio_url() {
        return audio_url;
    }

    public void setAudio_intro(String audio_intro) {
        this.audio_intro = audio_intro;
    }
    public String getAudio_intro() {
        return audio_intro;
    }

    public void setAnnouncer_nickname(String announcer_nickname) {
        this.announcer_nickname = announcer_nickname;
    }
    public String getAnnouncer_nickname() {
        return announcer_nickname;
    }

    public void setAudio_pic_url(String audio_pic_url) {
        this.audio_pic_url = audio_pic_url;
    }
    public String getAudio_pic_url() {
        return audio_pic_url;
    }

    public void setAudio_duration(int audio_duration) {
        this.audio_duration = audio_duration;
    }
    public int getAudio_duration() {
        return audio_duration;
    }

}
