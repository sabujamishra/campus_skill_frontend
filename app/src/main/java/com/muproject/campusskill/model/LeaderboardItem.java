package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardItem {
    private int id;
    
    @SerializedName(value = "name", alternate = {"username", "full_name", "user_name"})
    private String name;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    // Values can be earnings, rating, or activity score (Hinglish: API ke alag-alag keys handle karne ke liye alternates)
    @SerializedName(value = "value", alternate = {"total_earnings", "average_rating", "leaderboard_score", "score", "earnings", "rating"})
    private String value;
    
    @SerializedName("extra_label")
    private String extraLabel;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getProfileImage() { return profileImage; }
    public String getValue() { return value; }
    public String getExtraLabel() { return extraLabel; }
}
