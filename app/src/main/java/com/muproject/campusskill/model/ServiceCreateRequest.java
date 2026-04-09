package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Service banane ki request (Hinglish: Naya service create karne ka data)
public class ServiceCreateRequest {
    private String title;
    private String description;
    
    @SerializedName("category_id")
    private int categoryId;
    
    private double price;
    
    @SerializedName("delivery_time")
    private int deliveryTime; // Days

    public ServiceCreateRequest(String title, String description, int categoryId, double price, int deliveryTime) {
        this.title = title;
        this.description = description;
        this.categoryId = categoryId;
        this.price = price;
        this.deliveryTime = deliveryTime;
    }
}
