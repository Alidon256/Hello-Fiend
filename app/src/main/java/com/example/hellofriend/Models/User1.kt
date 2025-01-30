package com.example.hellofriend.Models

class User1 {
    private var userId: String? = null
    private var name: String? = null
    private var email: String? = null
    private var contact: String? = null
    private var gender: String? = null
    private var address: String? = null
    private var dateOfBirth: String? = null
    private var userRole: String? = null
    private var profileImageUrl: String? = null

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

    // Getters and setters
    fun getUserId(): String? {
        return userId
    }

    fun setUserId(userId: String?) {
        this.userId = userId
    }

    fun getName(): String? {
        return name
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun getEmail(): String? {
        return email
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getProfileImageUrl(): String? {
        return profileImageUrl
    }

    fun setProfileImageUrl(profileImageUrl: String?) {
        this.profileImageUrl = profileImageUrl
    }

    fun getContact(): String? {
        return contact
    }

    fun setContact(contact: String?) {
        this.contact = contact
    }

    fun getGender(): String? {
        return gender
    }

    fun setGender(gender: String?) {
        this.gender = gender
    }

    fun getAddress(): String? {
        return address
    }

    fun setAddress(address: String?) {
        this.address = address
    }

    fun getDateOfBirth(): String? {
        return dateOfBirth
    }

    fun setDateOfBirth(dateOfBirth: String?) {
        this.dateOfBirth = dateOfBirth
    }

    fun getUserRole(): String? {
        return userRole
    }

    fun setUserRole(userRole: String?) {
        this.userRole = userRole
    }
}

