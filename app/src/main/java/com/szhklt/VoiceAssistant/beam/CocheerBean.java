package com.szhklt.VoiceAssistant.beam;

import java.util.List;

public class CocheerBean {

    private int status;
    private String cause;
    private String message;
    private List<Data> data;
    public void setStatus(int status) {
        this.status = status;
    }
    public int getStatus() {
        return status;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
    public String getCause() {
        return cause;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    public String getMessage() {
        return message;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }
    public List<Data> getData() {
        return data;
    }

}