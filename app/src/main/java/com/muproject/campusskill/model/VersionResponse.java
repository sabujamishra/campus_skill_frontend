package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Server se version check ka response handle karne ke liye class
public class VersionResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;
    
    @SerializedName("data")
    private VersionData data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public VersionData getData() { return data; }

    public static class VersionData {
        @SerializedName("version")
        private String version;

        public String getVersion() { return version; }
    }
}
