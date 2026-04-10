package com.muproject.campusskill.model;

import java.util.List;

public class LeaderboardResponse {
    private boolean success;
    private String message;
    private List<LeaderboardItem> data;

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public List<LeaderboardItem> getData() { return data; }
}
