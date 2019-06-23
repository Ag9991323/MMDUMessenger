package com.example.mmdumessenger.models;

public class MessageModel {
    String timeStamp, sender, reciever, message;
    public Boolean isSeen;

    public MessageModel() {
    }

    public MessageModel(String timeStamp, String sender, String reciever, String message, Boolean isSeen) {
        this.timeStamp = timeStamp;
        this.sender = sender;
        this.reciever = reciever;
        this.message = message;
        this.isSeen = isSeen;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReciever() {
        return reciever;
    }

    public void setReciever(String reciever) {
        this.reciever = reciever;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Boolean getSeen() {
        return isSeen;
    }

    public void setSeen(Boolean seen) {
        isSeen = seen;
    }
}