package com.example.kaildyhoang.mycookbookapplication.models;

public class Chat {
    private String messenger;
    private String avatar;
    private String sender;
    private String receiver;

    public Chat() {

    }
    public Chat(String messenger, String avatar, String sender, String receiver) {
        this.messenger = messenger;
        this.avatar = avatar;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String getMessenger() {
        return messenger;
    }

    public void setMessenger(String messenger) {
        this.messenger = messenger;
    }
    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getReceiver() {
        return receiver;
    }

    public void setReceiver(String receiver) {
        this.receiver = receiver;
    }
}
