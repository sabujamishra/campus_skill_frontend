package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Service data model synced with API (Hinglish: API se aane waale services ka structure)
public class Service implements java.io.Serializable {
    private int id;
    private String title;
    private String description;
    private String price;
    
    @SerializedName("delivery_time")
    private int deliveryTime;
    
    private String category;
    
    @SerializedName("seller_name")
    private String sellerName;

    @SerializedName(value = "seller_id", alternate = {"user_id", "owner_id", "created_by", "sellerID", "userID"})
    private int sellerId;
    
    @SerializedName(value = "seller_profile_image", alternate = {"profile_image", "seller_photo", "avatar"})
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
    public int getSellerId() { return sellerId; }
    public String getSellerProfileImage() { return sellerProfileImage; }
    public String getThumbnail() { return thumbnail; }
    public float getAverageRating() { return averageRating; }
}
