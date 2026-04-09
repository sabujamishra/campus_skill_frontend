package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Service data model synced with API (Hinglish: API se aane waale services ka structure)
public class Service {
    private int id;
    private String title;
    private String description;
    private String price;
    
    @SerializedName("delivery_time")
    private int deliveryTime;
    
    private String category;
    
    @SerializedName("seller_name")
    private String sellerName;
    
    @SerializedName("seller_profile_image")
    private String sellerProfileImage;
    
    // Server thumbnail field variations (Hinglish: Agar name match na ho)
    @SerializedName(value = "thumbnail", alternate = {"image", "service_image", "photo"})
    private String thumbnail;
    
    @SerializedName("average_rating")
    private float averageRating;

    public int getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getPrice() { return price; }
    public int getDeliveryTime() { return deliveryTime; }
    public String getCategory() { return category; }
    public String getSellerName() { return sellerName; }
    public String getSellerProfileImage() { return sellerProfileImage; }
    public String getThumbnail() { return thumbnail; }
    public float getAverageRating() { return averageRating; }
}
