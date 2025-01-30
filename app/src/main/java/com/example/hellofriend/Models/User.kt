package com.example.hellofriend.Models

import java.util.Objects

class User {
    private var id: String? = null
    private var name: String? = null
    private var email: String? = null

    constructor()

    constructor(id: String, name: String?, email: String?) {
        this.id = id
        this.name = name
        this.email = email
    }

    fun setId(id: String) {
        this.id = id
    }

    fun setName(name: String?) {
        this.name = name
    }

    fun setEmail(email: String?) {
        this.email = email
    }

    fun getId(): String {
        return id!!
    }

    fun getName(): String? {
        return name
    }

    fun getEmail(): String? {
        return email
    }

    override fun equals(o: Any?): Boolean {
        if (this === o) return true
        if (o !is User) return false
        val user = o
        return id == user.id // Assuming 'id' is the unique identifier
    }

    override fun hashCode(): Int {
        return Objects.hash(id) // Assuming 'id' is the unique identifier
    }
}
