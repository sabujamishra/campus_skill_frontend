package com.muproject.campusskill.model;

import com.google.gson.annotations.SerializedName;

// Register API se jo reply aayega (Hinglish: Register hone ke baad ka response)
public class RegisterResponse {
    @SerializedName("status")
    private String status;
    
    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private com.google.gson.JsonElement data;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public com.google.gson.JsonElement getDataElement() { return data; }
    
    // Hinglish: "data" agar array hua toh null return karega, object hua toh convert karke dega
    public Data getData() {
        if (data != null && data.isJsonObject()) {
            return new com.google.gson.Gson().fromJson(data, Data.class);
        }
        return null;
    }

    public static class Data {
        @SerializedName("token")
        private String token;

        @SerializedName("user")
        private User user;

        public String getToken() { return token; }
        public User getUser() { return user; }
    }
}
