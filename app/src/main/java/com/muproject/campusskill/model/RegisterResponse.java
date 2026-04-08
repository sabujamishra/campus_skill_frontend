package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Register API se jo reply aayega (Hinglish: Register hone ke baad ka response)
public class RegisterResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
}
