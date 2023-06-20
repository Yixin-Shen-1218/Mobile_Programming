package com.example.UNISEA.Model;

import java.io.Serializable;

public class ActivityDetail implements Serializable {
    private String date;
    private String startTime;
    private String endTime;
    private String location;

    public ActivityDetail() {
    }

    public ActivityDetail(String date, String startTime, String endTime, String location) {
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
    }

    public void emptyDetail() {
        this.date = "";
        this.startTime = "";
        this.endTime = "";
        this.location = "";
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String toString() {
        return "ActivityDetail{" +
                "date='" + date + '\'' +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", location='" + location + '\'' +
                '}';
    }
}
