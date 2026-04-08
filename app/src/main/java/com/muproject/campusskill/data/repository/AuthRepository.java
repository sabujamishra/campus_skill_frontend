package com.muproject.campusskill.data.repository;

import com.muproject.campusskill.data.api.ApiService;
import com.muproject.campusskill.data.api.RetrofitClient;
import com.muproject.campusskill.data.model.ApiResponse;
import com.muproject.campusskill.data.model.User;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    private ApiService apiService;

    public AuthRepository() {
        apiService = RetrofitClient.getApiService();
    }

    public interface AuthCallback {
        void onSuccess(User user);
        void onError(String message);
    }

    public void login(String email, String password, AuthCallback callback) {
        Map<String, String> fields = new HashMap<>();
        fields.put("email", email);
        fields.put("password", password);

        apiService.mapLogin(fields).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onError(response.body().getMessage());
                    }
                } else {
                    callback.onError("Server error");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void register(String name, String email, String dept, String password, AuthCallback callback) {
        Map<String, String> fields = new HashMap<>();
        fields.put("name", name);
        fields.put("email", email);
        fields.put("department", dept);
        fields.put("password", password);

        apiService.mapRegister(fields).enqueue(new Callback<ApiResponse<User>>() {
            @Override
            public void onResponse(Call<ApiResponse<User>> call, Response<ApiResponse<User>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().isSuccess()) {
                        callback.onSuccess(response.body().getData());
                    } else {
                        callback.onError(response.body().getMessage());
                    }
                } else {
                    callback.onError("Server error");
                }
            }

            @Override
            public void onFailure(Call<ApiResponse<User>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }
}
