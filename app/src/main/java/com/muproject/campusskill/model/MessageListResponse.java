package com.muproject.campusskill.model;

import java.util.List;

public class MessageListResponse {
    private boolean success;
    private String message;
    private List<ChatMessage> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<ChatMessage> getData() { return data; }
}
