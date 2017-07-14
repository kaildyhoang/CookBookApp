package com.example.kaildyhoang.mycookbookapplication.models;

public class User {
    private String idUser;
    private String name;
    private String email;
    private String password;
    private String address;
    private String description;
    private String avatar;
    private String gender;
    private String createDay;

    private String username;
    private boolean isOnline;
    private long birthday;
    public User() {
//        Default
    }


    public User(String name, String email, String password, String address, String description,
                String avatar, String gender, String username,
                boolean isOnline, long birthday, String createDay) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.address = address;
        this.description = description;
        this.avatar = avatar;
        this.gender = gender;
        this.username = username;
        this.isOnline = isOnline;
        this.birthday = birthday;
        this.createDay = createDay;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getIdUser() {
        return idUser;
    }

    public void setIdUser(String idUser) {
        this.idUser = idUser;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isOnline() {
        return isOnline;
    }

    public void setOnline(boolean online) {
        isOnline = online;
    }

    public long getBirthday() {
        return birthday;
    }

    public void setBirthday(long birthday) {
        this.birthday = birthday;
    }

    public String getCreateDay() {
        return createDay;
    }

    public void setCreateDay(String createDay) {
        this.createDay = createDay;
    }
}
