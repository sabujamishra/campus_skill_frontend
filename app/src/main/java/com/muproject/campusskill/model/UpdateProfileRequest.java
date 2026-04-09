package com.muproject.campusskill.model;

// Profile update karne ki request (Hinglish: Data update karne waala model)
public class UpdateProfileRequest {
    private String name;
    private String department;

    public UpdateProfileRequest(String name, String department) {
        this.name = name;
        this.department = department;
    }
}
