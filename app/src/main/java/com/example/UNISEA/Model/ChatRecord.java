package com.example.UNISEA.Model;

public class ChatRecord {
    private String UID;
    private String imageurl;
    private Integer Type;
    private String Records;
//    private Boolean isLocal;

    public ChatRecord(String UID, String imageurl, Integer type, String records) {
        this.UID = UID;
        this.imageurl = imageurl;
        this.Type = type;
        this.Records = records;
//        this.isLocal = isLocal;
    }
//    public Boolean getIsLocal(){return this.isLocal;}

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

    public Integer getType() {
        return Type;
    }

    public void setType(Integer type) {
        this.Type = type;
    }

    public String getRecords() {
        return Records;
    }

    public void setRecords(String records) {
        this.Records = records;
    }
}
