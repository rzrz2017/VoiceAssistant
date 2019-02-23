package com.szhklt.VoiceAssistant.beam;

import java.io.Serializable;

public class CocheerAlbumData {
    private int id;
    private String album_name;
    private String album_intro;
    private String age;
    private String icon;
    private String  updatetime;
    private String tag;
    private int album_count;
    private int isFree;
    private String purchase_notice;
    private int price;
    private String album_type;
    private String announcer_nickname;
    private int click_count;
    public void setId(int id) {
        this.id = id;
    }
    public int getId() {
        return id;
    }

    public void setAlbum_name(String album_name) {
        this.album_name = album_name;
    }
    public String getAlbum_name() {
        return album_name;
    }

    public void setAlbum_intro(String album_intro) {
        this.album_intro = album_intro;
    }
    public String getAlbum_intro() {
        return album_intro;
    }

    public void setAge(String age) {
        this.age = age;
    }
    public String getAge() {
        return age;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }
    public String getIcon() {
        return icon;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
    public String getUpdatetime() {
        return updatetime;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }
    public String getTag() {
        return tag;
    }

    public void setAlbum_count(int album_count) {
        this.album_count = album_count;
    }
    public int getAlbum_count() {
        return album_count;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }
    public int getIsFree() {
        return isFree;
    }

    public void setPurchase_notice(String purchase_notice) {
        this.purchase_notice = purchase_notice;
    }
    public String getPurchase_notice() {
        return purchase_notice;
    }

    public void setPrice(int price) {
        this.price = price;
    }
    public int getPrice() {
        return price;
    }

    public void setAlbum_type(String album_type) {
        this.album_type = album_type;
    }
    public String getAlbum_type() {
        return album_type;
    }

    public void setAnnouncer_nickname(String announcer_nickname) {
        this.announcer_nickname = announcer_nickname;
    }
    public String getAnnouncer_nickname() {
        return announcer_nickname;
    }

    public void setClick_count(int click_count) {
        this.click_count = click_count;
    }
    public int getClick_count() {
        return click_count;
    }

}
