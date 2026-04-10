package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

public class ChatMessage {
    private int id;
    
    @SerializedName("order_id")
    private int orderId;
    
    @SerializedName("sender_id")
    private int senderId;
    
    private String message;
    
    @SerializedName("created_at")
    private String createdAt;

    public int getId() { return id; }
    public int getOrderId() { return orderId; }
    public int getSenderId() { return senderId; }
    public String getMessage() { return message; }
    public String getCreatedAt() { return createdAt; }

    // Helper for UI (Hinglish: Khud ka hai ya sahmne wale ka)
    public boolean isMe(int currentUserId) {
        return senderId == currentUserId;
    }
}
