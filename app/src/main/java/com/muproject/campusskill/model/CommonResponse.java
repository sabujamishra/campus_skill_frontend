package com.muproject.campusskill.model;

// Generic response wrapper (Hinglish: Common success/error response)
public class CommonResponse {
    private String status;
    private String message;
    private Object data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
}
