package com.example.teaapplication;

public class ChatsModal {
    private String message;
    private String sender;

    public ChatsModal(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}