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

    // --- Service Management ---

    // Service update karo (Hinglish: Apni service edit karo)
    @retrofit2.http.PUT("service/update")
    Call<com.muproject.campusskill.model.CommonResponse> updateService(
            @retrofit2.http.Query("id") int serviceId,
            @Body com.muproject.campusskill.model.ServiceCreateRequest request
    );

    // Service delete karo (Hinglish: Apni service hatao)
    @retrofit2.http.DELETE("service/delete")
    Call<com.muproject.campusskill.model.CommonResponse> deleteService(@retrofit2.http.Query("id") int serviceId);
    // Order accept karo (Hinglish: Seller order approve kar sakta hai)
    @retrofit2.http.PUT("order/accept")
    Call<com.muproject.campusskill.model.CommonResponse> acceptOrder(@retrofit2.http.Query("id") int orderId);

    // Order complete karo (Hinglish: Buyer order done mark kar sakta hai)
    @retrofit2.http.PUT("order/complete")
    Call<com.muproject.campusskill.model.CommonResponse> completeOrder(@retrofit2.http.Query("id") int orderId);

    // Review submit karo (Hinglish: Completed orders par rating aur comment dena)
    @retrofit2.http.POST("review/add")
    Call<com.muproject.campusskill.model.CommonResponse> submitReview(@retrofit2.http.Body java.util.Map<String, Object> body);

    // --- Leaderboard Module ---

    @GET("leaderboard/top-earners")
    Call<com.muproject.campusskill.model.LeaderboardResponse> getTopEarners();

    @GET("leaderboard/top-rated")
    Call<com.muproject.campusskill.model.LeaderboardResponse> getTopRated();

    @GET("leaderboard/most-active")
    Call<com.muproject.campusskill.model.LeaderboardResponse> getMostActive();

    // --- Chat Module ---

    @POST("message/send")
    Call<com.muproject.campusskill.model.CommonResponse> sendMessage(@Body java.util.Map<String, Object> body);

    @GET("message/history")
    Call<com.muproject.campusskill.model.MessageListResponse> getChatHistory(@retrofit2.http.Query("order_id") int orderId);
}
