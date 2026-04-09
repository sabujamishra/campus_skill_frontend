package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Consolidated User model (Hinglish: User ki saari details ek hi jagah)
public class User {
    private int id;
    private String name;
    private String email;
    private String department;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    @SerializedName("total_earnings")
    private String totalEarnings; // String as per server response "0.00"
    
    @SerializedName("total_completed_orders")
    private int totalCompletedOrders;
    
    @SerializedName("average_rating")
    private float averageRating;
    
    @SerializedName("repeat_clients")
    private int repeatClients;
    
    @SerializedName("response_rate")
    private int responseRate;
    
    @SerializedName("leaderboard_score")
    private int leaderboardScore;
    
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getDepartment() { return department; }
    public String getProfileImage() { return profileImage; }
    public String getTotalEarnings() { return totalEarnings; }
    public int getTotalCompletedOrders() { return totalCompletedOrders; }
    public float getAverageRating() { return averageRating; }
    public int getRepeatClients() { return repeatClients; }
    public int getResponseRate() { return responseRate; }
    public int getLeaderboardScore() { return leaderboardScore; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
}
