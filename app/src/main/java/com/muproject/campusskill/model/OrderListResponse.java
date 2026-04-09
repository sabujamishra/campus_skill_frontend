package com.muproject.campusskill.model;

import java.util.List;

// Order list response wrapper (Hinglish: Orders ki list ka container)
public class OrderListResponse {
    private String status;
    private String message;
    private List<Order> data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Order> getData() { return data; }
}
