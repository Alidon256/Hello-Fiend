package com.example.hellofriend.Models

import com.google.firebase.Timestamp

class Message1 {
    var userId: String? = null
    var recipientId: String? = null
    var userName: String? = null
    var text: String? = null
    var firestoreTimestamp: Timestamp? = null

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
}
