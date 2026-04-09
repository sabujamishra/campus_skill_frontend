package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Order data model synced with actual API response (Hinglish: Order ka real structure)
public class Order {
    private int id;

    @SerializedName(value = "amount", alternate = {"service_price", "price"})
    private String amount;

    private String status; // pending, in_progress, completed, cancelled

    @SerializedName("service_title")
    private String serviceTitle;

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("buyer_name")
    private String buyerName;

    @SerializedName("seller_name")
    private String sellerName;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getServiceTitle() { return serviceTitle; }
    public int getServiceId() { return serviceId; }
    public String getBuyerName() { return buyerName; }
    public String getSellerName() { return sellerName; }
    public String getCreatedAt() { return createdAt; }
}
