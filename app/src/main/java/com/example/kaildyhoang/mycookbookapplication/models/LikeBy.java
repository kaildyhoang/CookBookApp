package com.example.kaildyhoang.mycookbookapplication.models;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class LikeBy {
    private String likeById;
    private String likeByName;

    public LikeBy(String likeById, String likeByName) {
        this.likeById = likeById;
        this.likeByName = likeByName;
    }
    public LikeBy() {

    }

    @Exclude
    public String getLikeById() {
        return likeById;
    }

    public void setLikeById(String likeById) {
        this.likeById = likeById;
    }

    public String getLikeByName() {
        return likeByName;
    }

    public void setLikeByName(String likeByName) {
        this.likeByName = likeByName;
    }
}
