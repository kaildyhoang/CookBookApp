package com.example.kaildyhoang.mycookbookapplication.models;

public class Follow {
    private String idFriend;
    private String avatar;
    private String name;

    private String email;

    public Follow() {

    }

    public Follow(String idFriend, String avatar, String name, String email) {
        this.idFriend = idFriend;
        this.avatar = avatar;
        this.name = name;
        this.email = email;
    }

    public String getIdFriend() {
        return idFriend;
    }

    public void setIdFriend(String idFriend) {
        this.idFriend = idFriend;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
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
}
