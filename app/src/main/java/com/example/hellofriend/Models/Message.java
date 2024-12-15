package com.example.hellofriend.Models;
public class Message {

    private String text;
    private String userId;
    private String userName;
    private long timestamp;
    private String recipientId;  // Add this field

    public Message() {
        // Default constructor required for calls to DataSnapshot.getValue(Message.class)
    }

    public Message(String text, String userId, String userName, long timestamp, String recipientId) {
        this.text = text;
        this.userId = userId;
        this.userName = userName;
        this.timestamp = timestamp;
        this.recipientId = recipientId;  // Initialize recipientId
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    // Getters and Setters
    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    // Other getters and setters...

}
