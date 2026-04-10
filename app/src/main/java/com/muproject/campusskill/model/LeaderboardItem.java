package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardItem {
    private int id;
    private String name;
    private String department;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    @SerializedName("total_completed_orders")
    private int totalCompletedOrders;
    
    @SerializedName("average_rating")
    private float averageRating;
    
    @SerializedName("leaderboard_score")
    private int leaderboardScore;
    
    @SerializedName("total_earnings")
    private String totalEarnings;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDepartment() { return department; }
    public String getProfileImage() { return profileImage; }
    public int getTotalCompletedOrders() { return totalCompletedOrders; }
    public float getAverageRating() { return averageRating; }
    public int getLeaderboardScore() { return leaderboardScore; }
    public String getTotalEarnings() { return totalEarnings; }
}
