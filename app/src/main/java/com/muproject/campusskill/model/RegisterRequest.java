package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Register request bhejte waqt jo data chahiye (Hinglish: Register ke liye jo details bhejni hain)
public class RegisterRequest {
    @SerializedName("name")
    private String fullName;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("password")
    private String password;

    public RegisterRequest(String fullName, String email, String department, String password) {
        this.fullName = fullName;
        this.email = email;
        this.department = department;
        this.password = password;
    }
}
