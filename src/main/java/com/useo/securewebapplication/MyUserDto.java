package com.useo.securewebapplication;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

public class MyUserDto {

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Password is required")
    @Size(min = 3, message = "Password must be at least 6 characters")
    private String password;

    @NotEmpty(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    public MyUserDto() {
        // default constructor
    }

    // Getters and setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
