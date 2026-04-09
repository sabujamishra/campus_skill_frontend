package com.muproject.campusskill.model;

import java.util.List;

// Services list response (Hinglish: Multiple services mangwane ka structure)
public class ServiceListResponse {
    private String status;
    private String message;
    private List<Service> data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Service> getData() { return data; }
}
