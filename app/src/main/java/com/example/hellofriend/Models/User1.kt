package com.example.hellofriend.Models

import com.google.firebase.Timestamp

class User1 {
    var userId: String? = null
    var name: String? = null
    var email: String? = null
    var contact: String? = null
    var gender: String? = null
    var address: String? = null
    var dateOfBirth: String? = null
    var userRole: String? = null
    var profileImageUrl: String? = null

    constructor() // Empty constructor for Firestore


    constructor(
        userId: String?,
        name: String?,
        email: String?,
        contact: String?,
        gender: String?,
        address: String?,
        dateOfBirth: String?,
        userRole: String?,
        profileImageUrl: String?
    ) {
        this.userId = userId
        this.name = name
        this.email = email
        this.contact = contact
        this.gender = gender
        this.address = address
        this.dateOfBirth = dateOfBirth
        this.userRole = userRole
        this.profileImageUrl = profileImageUrl
    }

}

