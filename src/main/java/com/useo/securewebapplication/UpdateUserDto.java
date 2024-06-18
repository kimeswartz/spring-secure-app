package com.useo.securewebapplication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class UpdateUserDto {

    @NotEmpty(message = "Username cannot be empty")
    @Size(min = 1, message = "Username must be at least one character")
    private String newUsername;

    @NotEmpty(message = "Password cannot be empty")
    @Size(min = 3, message = "Password must be at least 3 characters long")
    private String newPassword;

    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email format")
    private String email;

    // Getters and Setters
    public String getNewUsername() {
        return newUsername;
    }

    public void setNewUsername(String newUsername) {
        this.newUsername = newUsername;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
