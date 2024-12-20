package com.example.hellofriend.Models;

import com.google.firebase.Timestamp;

public class Message1 {

    private String userId;
    private String recipientId;
    private String userName;
    private String text;
    private Timestamp firestoreTimestamp;

    public Message1() {
    }

    public Message1(String text, String userId, String recipientId, String userName, Timestamp firestoreTimestamp) {
        this.text = text;
        this.userId = userId;
        this.recipientId = recipientId;
        this.userName = userName;
        this.firestoreTimestamp = firestoreTimestamp;
    }

    // Getters and Setters
    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Timestamp getFirestoreTimestamp() {
        return firestoreTimestamp;
    }

    public void setFirestoreTimestamp(Timestamp firestoreTimestamp) {
        this.firestoreTimestamp = firestoreTimestamp;
    }
}
