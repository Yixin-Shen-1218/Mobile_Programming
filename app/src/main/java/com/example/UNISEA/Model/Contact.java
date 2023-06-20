package com.example.UNISEA.Model;

public class Contact {
    private String UID;
    private String imageurl;
    private String username;

    public Contact(String UID, String imageurl, String username) {
        this.UID = UID;
        this.imageurl = imageurl;
        this.username = username;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
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
}
