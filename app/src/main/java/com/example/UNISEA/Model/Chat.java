package com.example.UNISEA.Model;

import androidx.dynamicanimation.animation.SpringAnimation;

public class Chat {
    private String UID;
    private String imageurl;
    private String username;
    private String latest_chat;
    private String time_stamp;
    private Integer unread_num;

    public Chat(String UID, String imageurl, String username, String latest_chat, String time_stamp, Integer unread_num) {
        this.UID = UID;
        this.imageurl = imageurl;
        this.username = username;
        this.latest_chat = latest_chat;
        this.time_stamp = time_stamp;
        this.unread_num = unread_num;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getLatest_chat() {
        return latest_chat;
    }

    public void setLatest_chat(String latest_chat) {
        this.latest_chat = latest_chat;
    }

    public String getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(String time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public Integer getUnread_num() {
        return unread_num;
    }

    public void setUnread_num(Integer unread_num) {
        this.unread_num = unread_num;
    }
}
