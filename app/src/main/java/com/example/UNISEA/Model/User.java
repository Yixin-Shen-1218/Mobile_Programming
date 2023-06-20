package com.example.UNISEA.Model;

import java.io.Serializable;

public class User implements Serializable {
    private String uid;
    private String username;
    private String url;
    private int post_count;

    public User() {
    }

    public User(String uid, String username, String url, int post_count) {
        this.uid = uid;
        this.username = username;
        this.url = url;
        this.post_count = post_count;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getPost_count() {
        return post_count;
    }

    public void setPost_count(int post_count) {
        this.post_count = post_count;
    }

    @Override
    public String toString() {
        return "User{" +
                "uid='" + uid + '\'' +
                ", username='" + username + '\'' +
                ", url='" + url + '\'' +
                ", post_count=" + post_count +
                '}';
    }
}
