package com.example.hellofriend.Models

import java.util.Objects

class User {
    var id: String? = null
   var name: String? = null
    var email: String? = null

    constructor()

    constructor(id: String, name: String?, email: String?) {
        this.id = id
        this.name = name
        this.email = email
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
