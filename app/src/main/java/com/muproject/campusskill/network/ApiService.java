package com.muproject.campusskill.network;

import com.muproject.campusskill.model.LoginRequest;
import com.muproject.campusskill.model.LoginResponse;
import com.muproject.campusskill.model.RegisterRequest;
import com.muproject.campusskill.model.RegisterResponse;
import com.muproject.campusskill.model.VersionResponse;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

// Saare API endpoints yahan define honge (Hinglish: Network calls ki list)
public interface ApiService {

    // Base URL par call karke version check karega
    @GET("./")
    Call<VersionResponse> getVersion();

    // Register call (Hinglish: Naya account banane ke liye)
    @POST("register")
    Call<RegisterResponse> register(@Body RegisterRequest request);

    // Login call (Hinglish: Login karne ke liye)
    @POST("login")
    Call<LoginResponse> login(@Body LoginRequest request);
}
