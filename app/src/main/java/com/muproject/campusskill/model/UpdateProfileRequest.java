package com.muproject.campusskill.model;

// Profile update karne ki request (Hinglish: Name, Email, Department update karne waala model)
public class UpdateProfileRequest {
    private String name;
    private String email;
    private String department;

    public UpdateProfileRequest(String name, String email, String department) {
        this.name = name;
        this.email = email;
        this.department = department;
    }
}
