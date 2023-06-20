package com.example.UNISEA.Model;

public class Step {
    private String UID;
    private String imageurl;
    private String username;
    private Integer rank;
    private Integer step_num;

    public Step(String UID, String imageurl, String username, Integer rank, Integer step_num) {
        this.UID = UID;
        this.imageurl = imageurl;
        this.username = username;
        this.rank = rank;
        this.step_num = step_num;
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

    public Integer getRank() {
        return rank;
    }

    public void setRank(Integer rank) {
        this.rank = rank;
    }

    public Integer getStep_num() {
        return step_num;
    }

    public void setStep_num(Integer step_num) {
        this.step_num = step_num;
    }
}
