package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Login request bhejte waqt jo data chahiye (Hinglish: Login ke liye details)
public class LoginRequest {
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
