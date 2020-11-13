package com.example.post;

import com.google.firebase.database.ServerValue;

public class post {
    private String postKey;
    private String title;
    private  String description;
    private String picture;
    private Object timeStamp;

    public post(String title, String description, String picture) {
        this.title = title;
        this.description = description;
        this.picture = picture;
        this.timeStamp = ServerValue.TIMESTAMP;
    }


    public String getPostKey() {
        return postKey;
    }

    public void setPostKey(String postKey) {
        this.postKey = postKey;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getPicture() {
        return picture;
    }

    public Object getTimeStamp() {
        return timeStamp;
    }

    public post() {

    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setTimeStamp(Object timeStamp) {
        this.timeStamp = timeStamp;
    }
}
