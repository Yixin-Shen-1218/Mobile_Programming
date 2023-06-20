package com.example.UNISEA.Model;

public class Preference {
    private int age1;
    private int age2;
    private int height1;
    private int height2;
    private boolean male;
    private boolean female;
    private boolean nonBinary;
    private int distance;

    public Preference() {
    }

    public Preference(int age1, int age2, int height1, int height2, boolean male, boolean female, boolean nonBinary, int distance) {
        this.age1 = age1;
        this.age2 = age2;
        this.height1 = height1;
        this.height2 = height2;
        this.male = male;
        this.female = female;
        this.nonBinary = nonBinary;
        this.distance = distance;
    }

    public int getAge1() {
        return age1;
    }

    public void setAge1(int age1) {
        this.age1 = age1;
    }

    public int getAge2() {
        return age2;
    }

    public void setAge2(int age2) {
        this.age2 = age2;
    }

    public int getHeight1() {
        return height1;
    }

    public void setHeight1(int height1) {
        this.height1 = height1;
    }

    public int getHeight2() {
        return height2;
    }

    public void setHeight2(int height2) {
        this.height2 = height2;
    }

    public boolean isMale() {
        return male;
    }

    public void setMale(boolean male) {
        this.male = male;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public boolean isNonBinary() {
        return nonBinary;
    }

    public void setNonBinary(boolean nonBinary) {
        this.nonBinary = nonBinary;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }
}
