package com.szhklt.VoiceAssistant.beam;

public class Topic {

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    private Integer id;
    private String suTopic;
    private Integer state;

    public Topic(){

    }

    public Topic( String suTopic, Integer state) {

        this.suTopic = suTopic;
        this.state = state;
    }



    public String getSuTopic() {
        return suTopic;
    }

    public void setSuTopic(String suTopic) {
        this.suTopic = suTopic;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return "Topic{" +
                "id=" + id +
                ", suTopic='" + suTopic + '\'' +
                ", state=" + state +
                '}';
    }
}
