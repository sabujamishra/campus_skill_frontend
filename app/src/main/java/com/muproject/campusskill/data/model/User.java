package com.muproject.campusskill.data.model;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    
    @SerializedName("name")
    private String name;
    
    @SerializedName("email")
    private String email;
    
    @SerializedName("password")
    private String password;
    
    @SerializedName("department")
    private String department;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    @SerializedName("total_earnings")
    private double totalEarnings;
    
    @SerializedName("total_completed_orders")
    private int totalCompletedOrders;
    
    @SerializedName("average_rating")
    private double averageRating;
    
    @SerializedName("repeat_clients")
    private int repeatClients;
    
    @SerializedName("response_rate")
    private int responseRate;
    
    @SerializedName("leaderboard_score")
    private int leaderboardScore;
    
    @SerializedName("status")
    private String status;
    
    @SerializedName("created_at")
    private String createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
    public String getProfileImage() { return profileImage; }
    public void setProfileImage(String profileImage) { this.profileImage = profileImage; }
    public double getTotalEarnings() { return totalEarnings; }
    public void setTotalEarnings(double totalEarnings) { this.totalEarnings = totalEarnings; }
    public int getTotalCompletedOrders() { return totalCompletedOrders; }
    public void setTotalCompletedOrders(int totalCompletedOrders) { this.totalCompletedOrders = totalCompletedOrders; }
    public double getAverageRating() { return averageRating; }
    public void setAverageRating(double averageRating) { this.averageRating = averageRating; }
    public int getRepeatClients() { return repeatClients; }
    public void setRepeatClients(int repeatClients) { this.repeatClients = repeatClients; }
    public int getResponseRate() { return responseRate; }
    public void setResponseRate(int responseRate) { this.responseRate = responseRate; }
    public int getLeaderboardScore() { return leaderboardScore; }
    public void setLeaderboardScore(int leaderboardScore) { this.leaderboardScore = leaderboardScore; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
