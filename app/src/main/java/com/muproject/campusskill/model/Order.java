package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Order data model (Hinglish: Order ki saari details ka structure)
public class Order {
    private int id;

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("service_title")
    private String serviceTitle;

    @SerializedName("service_price")
    private String servicePrice;

    @SerializedName("buyer_id")
    private int buyerId;

    @SerializedName("buyer_name")
    private String buyerName;

    @SerializedName("seller_id")
    private int sellerId;

    @SerializedName("seller_name")
    private String sellerName;

    private String status; // pending, in_progress, completed, cancelled

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("updated_at")
    private String updatedAt;

    public int getId() { return id; }
    public int getServiceId() { return serviceId; }
    public String getServiceTitle() { return serviceTitle; }
    public String getServicePrice() { return servicePrice; }
    public int getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public int getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getStatus() { return status; }
    public String getCreatedAt() { return createdAt; }
    public String getUpdatedAt() { return updatedAt; }
}
