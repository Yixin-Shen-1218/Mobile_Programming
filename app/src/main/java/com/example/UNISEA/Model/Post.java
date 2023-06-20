package com.example.UNISEA.Model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Post implements Serializable {
    private String content;
    private String time;
    private long order;
    private String uid;
    private String pid;
    private String image;

    private String username;
    private String avatar;

    private ActivityDetail details;

    private List<String> like = new ArrayList<>();
    private long commentNum;

    private boolean isActivity;
    private boolean visible;

    public Post(String content, String time, long order, String uid, String pid, String image,
                String username, String avatar, ActivityDetail details, boolean isActivity, boolean visible) {
        this.content = content;
        this.time = time;
        this.order = order;
        this.uid = uid;
        this.pid = pid;
        this.image = image;
        this.username = username;
        this.avatar = avatar;
        this.details = details;
        this.isActivity = isActivity;
        this.visible = visible;
    }

    public Post() {
    }

    public List<String> getLike() {
        return like;
    }

    public void setLike(HashMap<String,String> hashMap) {
        this.like=  new ArrayList<>(hashMap.keySet());
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public ActivityDetail getDetails() {
        return details;
    }

    public void setDetails(ActivityDetail details) { this.details = details; }

    public long getCommentNum() {
        return commentNum;
    }

    public void setCommentNum(long commentNum) {
        this.commentNum = commentNum;
    }

    public boolean isActivity() {
        return isActivity;
    }

    public void setActivity(boolean activity) {
        isActivity = activity;
    }

    public boolean isVisible() {
        return visible;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public long getOrder() {
        return order;
    }

    public void setOrder(long order) {
        this.order = order;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
