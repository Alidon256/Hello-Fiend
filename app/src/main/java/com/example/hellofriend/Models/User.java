package com.example.hellofriend.Models;

import java.util.Objects;

public class User {
    private String id;
    private String name;
    private String email;
    private String role;

    public User() {}

    public User(String id, String name, String email, String role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        User user = (User) o;
        return id.equals(user.id); // Assuming 'id' is the unique identifier
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Assuming 'id' is the unique identifier
    }




}
