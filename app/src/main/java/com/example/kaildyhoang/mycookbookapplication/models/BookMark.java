package com.example.kaildyhoang.mycookbookapplication.models;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class BookMark {

    private String bookMarkId;
    private String bookMarkTitle;
    private String postById;
    private String postByName;
    private String postByAvatar;
    public BookMark(){}
    public BookMark(String bookMarkId, String bookMarkTitle, String postById, String postByName, String postByAvatar) {
        this.bookMarkId = bookMarkId;
        this.bookMarkTitle = bookMarkTitle;
        this.postById = postById;
        this.postByName = postByName;
        this.postByAvatar = postByAvatar;
    }

    public String getBookMarkId() {
        return bookMarkId;
    }

    public void setBookMarkId(String bookMarkId) {
        this.bookMarkId = bookMarkId;
    }

    public String getBookMarkTitle() {
        return bookMarkTitle;
    }

    public void setBookMarkTitle(String bookMarkTitle) {
        this.bookMarkTitle = bookMarkTitle;
    }

    public String getPostById() {
        return postById;
    }

    public void setPostById(String postById) {
        this.postById = postById;
    }

    public String getPostByName() {
        return postByName;
    }

    public void setPostByName(String postByName) {
        this.postByName = postByName;
    }

    public String getPostByAvatar() {
        return postByAvatar;
    }

    public void setPostByAvatar(String postByAvatar) {
        this.postByAvatar = postByAvatar;
    }
}
