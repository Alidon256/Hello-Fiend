package com.example.hellofriend.Models

import com.google.firebase.Timestamp

class Message1 {
    private var userId: String? = null
    private var recipientId: String? = null
    private var userName: String? = null
    private var text: String? = null
    private var firestoreTimestamp: Timestamp? = null

    constructor()

    constructor(
        text: String?,
        userId: String?,
        recipientId: String?,
        userName: String?,
        firestoreTimestamp: Timestamp?
    ) {
        this.text = text
        this.userId = userId
        this.recipientId = recipientId
        this.userName = userName
        this.firestoreTimestamp = firestoreTimestamp
    }

    // Getters and Setters
    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getRecipientId(): String? {
        return recipientId
    }

    fun setRecipientId(recipientId: String?) {
        this.recipientId = recipientId
    }

    fun getUserName(): String? {
        return userName
    }

    fun setUserName(userName: String?) {
        this.userName = userName
    }

    fun getText(): String? {
        return text
    }

    fun setText(text: String?) {
        this.text = text
    }

    fun getFirestoreTimestamp(): Timestamp? {
        return firestoreTimestamp
    }

    fun setFirestoreTimestamp(firestoreTimestamp: Timestamp?) {
        this.firestoreTimestamp = firestoreTimestamp
    }
}
