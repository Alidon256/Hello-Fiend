package com.example.hellofriend.Models

class Message {
    var text: String? = null
    var userId: String? = null
    var userName: String? = null
    var timestamp: Long = 0
    var recipientId: String? = null // Add this field

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

}
