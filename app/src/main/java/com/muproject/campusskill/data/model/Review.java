package com.muproject.campusskill.data.model;

import com.google.gson.annotations.SerializedName;

public class Review {
    @SerializedName("id")
    private int id;
    
    @SerializedName("order_id")
    private int orderId;
    
    @SerializedName("reviewer_id")
    private int reviewerId;
    
    @SerializedName("reviewed_user_id")
    private int reviewedUserId;
    
    @SerializedName("rating")
    private int rating;
    
    @SerializedName("comment")
    private String comment;
    
    @SerializedName("created_at")
    private String createdAt;

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public int getReviewerId() { return reviewerId; }
    public void setReviewerId(int reviewerId) { this.reviewerId = reviewerId; }
    public int getReviewedUserId() { return reviewedUserId; }
    public void setReviewedUserId(int reviewedUserId) { this.reviewedUserId = reviewedUserId; }
    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}
