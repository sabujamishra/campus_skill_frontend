package com.muproject.campusskill.data.api;

import com.muproject.campusskill.data.model.ApiResponse;
import com.muproject.campusskill.data.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @FormUrlEncoded
    @POST("register.php")
    Call<ApiResponse<User>> mapRegister(@FieldMap Map<String, String> fields);

    @FormUrlEncoded
    @POST("login.php")
    Call<ApiResponse<User>> mapLogin(@FieldMap Map<String, String> fields);
}
