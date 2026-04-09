package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Login ka response (Hinglish: Login hone par jo data aayega)
public class LoginResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private Data data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Data getData() { return data; }

    public static class Data {
        @SerializedName("user")
        private User user; // Use the consolidated User class
        
        @SerializedName("token")
        private String token;

        public User getUser() { return user; }
        public String getToken() { return token; }
    }
}
