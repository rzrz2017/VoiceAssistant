package com.szhklt.VoiceAssistant.beam.mqtt;

public class Phone {
    private String name;
    private String id;
    private String topic;
    private Boolean status;


    public Phone(String name,String id,String topic,Boolean status){
        this.name = name;
        this.id = id;
        this.topic = topic;
        this.status = status;

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return topic.split("/")[1];
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "\n"+"名字(name):"+name+
                "\n"+"手机id(id):"+id+
                "\n"+"主题(topic):"+topic+
                "\n"+"当前状态(status):"+status;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Phone == false){
            return false;
        }
        Phone tmp = (Phone)obj;
        if(!name.equals(tmp.getName())){
            return false;
        }
        if(!id.equals(tmp.getId())){
            return false;
        }
        if(!topic.equals(tmp.getTopic())){
            return false;
        }
        if(!status == tmp.getStatus()){
            return false;
        }
        return true;
    }
}
