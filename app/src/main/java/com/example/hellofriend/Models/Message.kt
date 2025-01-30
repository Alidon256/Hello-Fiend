package com.example.hellofriend.Models

class Message {
    private var text: String? = null
    private var userId: String? = null
    private var userName: String? = null
    private var timestamp: Long = 0
    private var recipientId: String? = null // Add this field

    constructor()

    constructor(
        text: String?,
        userId: String?,
        userName: String?,
        timestamp: Long,
        recipientId: String?
    ) {
        this.text = text
        this.userId = userId
        this.userName = userName
        this.timestamp = timestamp
        this.recipientId = recipientId // Initialize recipientId
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getTimestamp(): Long {
        return timestamp
    }

    fun setTimestamp(timestamp: Long) {
        this.timestamp = timestamp
    }

    // Getters and Setters
    fun getRecipientId(): String? {
        return recipientId
    }

    fun setRecipientId(recipientId: String?) {
        this.recipientId = recipientId
    } // Other getters and setters...
}
