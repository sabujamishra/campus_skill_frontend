package com.muproject.campusskill.model;

import java.util.List;

// Categories response from server (Hinglish: Categories ki list mangwane ka model)
public class CategoryResponse {
    private String status;
    private String message;
    private List<Category> data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public List<Category> getData() { return data; }
}
