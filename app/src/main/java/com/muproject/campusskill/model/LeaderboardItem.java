package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

public class LeaderboardItem {
    private int id;
    private String name;
    
    @SerializedName("profile_image")
    private String profileImage;
    
    // Values can be earnings, rating, or activity score
    private String value;
    
    @SerializedName("extra_label")
    private String extraLabel;

    public int getId() { return id; }
    public String getName() { return name; }
    public String getProfileImage() { return profileImage; }
    public String getValue() { return value; }
    public String getExtraLabel() { return extraLabel; }
}
