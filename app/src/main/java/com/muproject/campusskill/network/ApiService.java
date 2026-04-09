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
    Call<com.muproject.campusskill.model.LoginResponse> login(@Body com.muproject.campusskill.model.LoginRequest request);

    // Profile Endpoints
    
    // Apna khud ka profile mangne ke liye (Hinglish: My Profile data)
    @GET("user/me")
    Call<com.muproject.campusskill.model.ProfileResponse> getMyProfile();

    // Kisi doosre user ka profile dekhne ke liye (Hinglish: Public Profile)
    @GET("user/profile")
    Call<com.muproject.campusskill.model.ProfileResponse> getPublicProfile(@retrofit2.http.Query("id") int userId);

    // Profile details update karne ke liye (Hinglish: Data edit karo)
    @retrofit2.http.PUT("user/update")
    Call<com.muproject.campusskill.model.ProfileResponse> updateProfile(@Body com.muproject.campusskill.model.UpdateProfileRequest request);

    // Service photo upload karne ke liye (Hinglish: Image upload multipart flow)
    @retrofit2.http.Multipart
    @POST("user/upload-image")
    Call<com.muproject.campusskill.model.ProfileResponse> uploadProfileImage(@retrofit2.http.Part okhttp3.MultipartBody.Part image);

    // --- Service Module ---

    // Naya service banane ke liye (Hinglish: Post a new service)
    @POST("service/create")
    Call<com.muproject.campusskill.model.CommonResponse> createService(@Body com.muproject.campusskill.model.ServiceCreateRequest request);

    // Saari active services mangwane ke liye (Hinglish: Marketplace service list)
    @GET("services")
    Call<com.muproject.campusskill.model.ServiceListResponse> getServices(
            @retrofit2.http.Query("category_id") Integer categoryId,
            @retrofit2.http.Query("search") String search
    );

    // Saari service categories mangwayen (Hinglish: List of all categories)
    @GET("categories")
    Call<com.muproject.campusskill.model.CategoryResponse> getCategories();

    // Naya category banane ke liye (Hinglish: Add a new service category)
    @POST("categories")
    Call<com.muproject.campusskill.model.CommonResponse> createCategory(@Body java.util.Map<String, String> body);

    // User ki apni banayi hui services (Hinglish: My Services list)
    @GET("user/services")
    Call<com.muproject.campusskill.model.ServiceListResponse> getMyServices();

    // Service image upload karne ke liye (Hinglish: Service cover photo upload)
    @retrofit2.http.Multipart
    @POST("service/upload-image")
    Call<com.muproject.campusskill.model.CommonResponse> uploadServiceImage(
            @retrofit2.http.Part("service_id") okhttp3.RequestBody serviceId,
            @retrofit2.http.Part okhttp3.MultipartBody.Part image
    );

    // --- Order Module ---
    
    // Naya order place karne ke liye (Hinglish: Book a service)
    @POST("order/create")
    Call<com.muproject.campusskill.model.CommonResponse> createOrder(@Body java.util.Map<String, Integer> body);

    // Order history (Hinglish: Buyer ya Seller ke orders ki list)
    @GET("orders")
    Call<com.muproject.campusskill.model.OrderListResponse> getOrders(@retrofit2.http.Query("role") String role);
}
