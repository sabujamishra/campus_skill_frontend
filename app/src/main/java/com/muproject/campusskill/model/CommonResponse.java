package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Generic response wrapper (Hinglish: Common success/error response)
public class CommonResponse {
    private String status;
    private String message;
    private Object data;
    
    // Added specific ID fields with multiple alternates to catch various backend naming conventions
    @SerializedName(value = "id", alternate = {"ID", "service_id", "serviceID", "insert_id", "id_service", "new_id", "pk"})
    private Integer id;
    
    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public Object getData() { return data; }
    public Integer getId() { return id; }
    
    // Helper to get ID regardless of which field was populated
    public Integer getServiceId() {
        return id;
    }
}
