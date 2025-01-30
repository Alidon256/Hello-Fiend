package com.example.hellofriend.Models;

public class User1 {
        private String userId;
        private String name;
        private String email;
        private String contact;
        private String gender;
        private String address;
        private String dateOfBirth;
        private String userRole;
        private String profileImageUrl;

        public User1() {} // Empty constructor for Firestore


    public User1(String userId, String name, String email, String contact, String gender, String address, String dateOfBirth, String userRole, String profileImageUrl) {
        this.userId = userId;
        this.name = name;
        this.email = email;
        this.contact = contact;
        this.gender = gender;
        this.address = address;
        this.dateOfBirth = dateOfBirth;
        this.userRole = userRole;
        this.profileImageUrl = profileImageUrl;
    }

    // Getters and setters
        public String getUserId() { return userId; }
        public void setUserId(String userId) { this.userId = userId; }

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }

        public String getProfileImageUrl() { return profileImageUrl; }
        public void setProfileImageUrl(String profileImageUrl) { this.profileImageUrl = profileImageUrl; }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getUserRole() {
        return userRole;
    }

    public void setUserRole(String userRole) {
        this.userRole = userRole;
    }
}

