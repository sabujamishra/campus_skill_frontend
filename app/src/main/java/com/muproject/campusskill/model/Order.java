package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Order data model synced with updated API (Hinglish: Order ka full structure with IDs and names)
public class Order {
    private int id;

    @SerializedName(value = "amount", alternate = {"service_price", "price"})
    private String amount;

    private String status;

    @SerializedName("service_id")
    private int serviceId;

    @SerializedName("service_title")
    private String serviceTitle;

    @SerializedName("buyer_id")
    private int buyerId;

    @SerializedName("buyer_name")
    private String buyerName;

    @SerializedName("seller_id")
    private int sellerId;

    @SerializedName("seller_name")
    private String sellerName;

    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public String getAmount() { return amount; }
    public String getStatus() { return status; }
    public int getServiceId() { return serviceId; }
    public String getServiceTitle() { return serviceTitle; }
    public int getBuyerId() { return buyerId; }
    public String getBuyerName() { return buyerName; }
    public int getSellerId() { return sellerId; }
    public String getSellerName() { return sellerName; }
    public String getCreatedAt() { return createdAt; }
}
