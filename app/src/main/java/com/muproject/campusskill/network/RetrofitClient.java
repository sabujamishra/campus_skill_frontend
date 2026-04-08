package com.muproject.campusskill.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit ko initialize karne ke liye singleton class (Hinglish: Network connector)
public class RetrofitClient {
    private static final String BASE_URL = "https://lightgrey-dogfish-642647.hostingersite.com/api/";
    private static Retrofit retrofit = null;

    // Yeh function humein ApiService ka object bana ke deta hai network calls ke liye (Hinglish: API call karne ka engine yahan se milta hai)
    public static ApiService getApiService() {
        // Singleton pattern: Check kar raha hai ki kya Retrofit pehle se bana hua hai?
        if (retrofit == null) {
            // Agar nahi bana, toh naya Retrofit client build karo
            // Humne yahan SslUtils use kiya hai taaki hosting ki certificate errors bypass ho sakein
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(SslUtils.getUnsafeOkHttpClient().build()) // SSL errors handle karne ke liye unsafe client
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // ApiService interface ko implement karke executable object return kar raha hai
        return retrofit.create(ApiService.class);
    }
}
