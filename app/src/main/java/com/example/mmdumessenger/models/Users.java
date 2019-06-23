package com.example.mmdumessenger.models;

public class Users {
    String name, email, uid, image, rollNo, userType;
    boolean online;
    Long last_seen;

    public Users() {
    }

    public Users(String name, String email, String uid, String image, String rollNo, String userType, boolean online, Long last_seen) {
        this.name = name;
        this.email = email;
        this.uid = uid;
        this.image = image;
        this.rollNo = rollNo;
        this.userType = userType;
        this.online = online;
        this.last_seen = last_seen;
    }

    public boolean getOnline() {
        return online;
    }

    public void setOnline(boolean online) {
        this.online = online;
    }

    public Long getLast_seen() {
        return last_seen;
    }

    public void setLast_seen(Long last_seen) {
        this.last_seen = last_seen;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getRollNo() {
        return rollNo;
    }

    public void setRollNo(String rollNo) {
        this.rollNo = rollNo;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }
}