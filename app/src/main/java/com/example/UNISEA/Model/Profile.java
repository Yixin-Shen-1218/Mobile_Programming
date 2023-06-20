package com.example.UNISEA.Model;

public class Profile {
    private String birth;
    private String gender;
    private String university;
    private int height;
    private String about;
    private String tag1;
    private String tag2;
    private String tag3;
    private String tag4;

    public Profile() {
        this.birth = "";
        this.gender = "";
        this.university = "";
        this.height = 0;
        this.about = "";
        this.tag1 = "";
        this.tag2 = "";
        this.tag3 = "";
        this.tag4 = "";
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        if (!birth.equals("")) {
            this.birth = birth;
        }
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        if (!gender.equals("")) {
            this.gender = gender;
        }
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        if (!university.equals("")) {
            this.university = university;
        }
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        if (height != 0) {
            this.height = height;
        }
    }

    public String getAbout() {
        return about;
    }

    public void setAbout(String about) {
        if (!about.equals("")) {
            this.about = about;
        }
    }

    public String getTag1() {
        return tag1;
    }

    public void setTag1(String tag1) {
        if (!tag1.equals("")) {
            this.tag1 = tag1;
        }
    }

    public String getTag2() {
        return tag2;
    }

    public void setTag2(String tag2) {
        if (!tag2.equals("")) {
            this.tag2 = tag2;
        }
    }

    public String getTag3() {
        return tag3;
    }

    public void setTag3(String tag3) {
        if (!tag3.equals("")) {
            this.tag3 = tag3;
        }
    }

    public String getTag4() {
        return tag4;
    }

    public void setTag4(String tag4) {
        if (!tag4.equals("")) {
            this.tag4 = tag4;
        }
    }
}
