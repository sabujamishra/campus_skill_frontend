package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// User ka detailed profile model (Hinglish: User ki saari details)
public class User {
    private int id;
    private String name;
    private String email;
    private String department;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    @SerializedName("total_earnings")
    private double totalEarnings;
    
    @SerializedName("leaderboard_score")
    private int leaderboardScore;
    
    @SerializedName("average_rating")
    private float averageRating;
    
    @SerializedName("response_rate")
    private String responseRate;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getProfileImage() { return profileImage; }
    public double getTotalEarnings() { return totalEarnings; }
    public int getLeaderboardScore() { return leaderboardScore; }
    public float getAverageRating() { return averageRating; }
    public String getResponseRate() { return responseRate; }
}
