package com.example.manageit.models;

import com.google.gson.annotations.SerializedName;

/**
 * Authenticated app user.
 */
public class User {
    @SerializedName("user_id")
    private String id;
    @SerializedName("first_name")
    private String firstName;
    @SerializedName("last_name")
    private String lastName;
    @SerializedName("dob")
    private String dateOfBirth;
    private String email;
    @SerializedName("created_at")
    private String createdAt;

    public User() {
    }

    public User(String id, String firstName, String lastName, String dateOfBirth, String email, String createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dateOfBirth = dateOfBirth;
        this.email = email;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + (lastName == null || lastName.isEmpty() ? "" : " " + lastName);
    }

    /**
     * Backward-compatible alias while the app is being refactored.
     */
    public String getName() {
        return getFullName().trim();
    }
}
