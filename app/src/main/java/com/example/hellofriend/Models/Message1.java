package com.example.hellofriend.Models;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "messages")
public class Message1 {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String userId;       // Sender ID
    private String recipientId;  // Receiver ID
    private String userName;     // Sender Name
    private String text;         // Message Content
    private long timestamp;      // Time of Message

    // Constructor
    public Message1(String text, String userId, String recipientId, String userName, long timestamp) {
        this.text = text;
        this.userId = userId;
        this.recipientId = recipientId;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public Message1() {

    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getRecipientId() { return recipientId; }
    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}
