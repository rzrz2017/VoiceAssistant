package com.szhklt.VoiceAssistant.beam.mqtt;

public class Phone {
    private String name;
    private String id;
    private String topic;


    public Phone(String name,String id,String topic){
        this.name = name;
        this.id = id;
        this.topic = topic;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    @Override
    public String toString() {
        return "\n"+"名字(name):"+name+"\n"+"手机id(id):"+id+"\n"+"主题(topic):"+topic;
    }
}
