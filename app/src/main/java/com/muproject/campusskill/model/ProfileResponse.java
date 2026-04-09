package com.muproject.campusskill.model;

// Profile API ka response wrapper (Hinglish: Profile data ka container)
public class ProfileResponse {
    private String status;
    private String message;
    private User data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public User getData() { return data; }
}
