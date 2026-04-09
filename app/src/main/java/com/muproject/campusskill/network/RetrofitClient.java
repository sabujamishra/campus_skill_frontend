package com.muproject.campusskill.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit ko initialize karne ke liye singleton class (Hinglish: Network connector)
public class RetrofitClient {
    private static final String BASE_URL = "https://lightgrey-dogfish-642647.hostingersite.com/api/";
    private static Retrofit retrofit = null;
    private static android.content.Context appContext;

    // App ke startup par context initialize karne ke liye (Hinglish: Context set kar rahe hain taaki SessionManager chale)
    public static void init(android.content.Context context) {
        appContext = context.getApplicationContext();
    }

    // Yeh function humein ApiService ka object bana ke deta hai network calls ke liye (Hinglish: API call karne ka engine yahan se milta hai)
    public static ApiService getApiService() {
        // Singleton pattern: Check kar raha hai ki kya Retrofit pehle se bana hua hai?
        if (retrofit == null) {
            okhttp3.OkHttpClient.Builder clientBuilder = SslUtils.getUnsafeOkHttpClient();

            // Interceptor adding Authorization header (Hinglish: Token khud-ba-khud header mein add karega)
            clientBuilder.addInterceptor(chain -> {
                okhttp3.Request.Builder requestBuilder = chain.request().newBuilder();
                if (appContext != null) {
                    SessionManager sessionManager = new SessionManager(appContext);
                    String token = sessionManager.getToken();
                    if (token != null) {
                        requestBuilder.addHeader("Authorization", "Bearer " + token);
                    }
                }
                return chain.proceed(requestBuilder.build());
            });

            // Global Auth Guard (Hinglish: Server 401 de toh seedha login page par bhejo)
            clientBuilder.addInterceptor(chain -> {
                okhttp3.Response response = chain.proceed(chain.request());
                if (response.code() == 401 && appContext != null) {
                    Log.w("RetrofitClient", "401 Unauthorized detected. Redirecting to Login.");
                    SessionManager sm = new SessionManager(appContext);
                    sm.clearSession();
                    new Handler(Looper.getMainLooper()).post(() -> {
                        Intent intent = new Intent(appContext, com.muproject.campusskill.MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        appContext.startActivity(intent);
                    });
                }
                return response;
            });

            // Humne yahan SslUtils use kiya hai taaki hosting ki certificate errors bypass ho sakein
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .client(clientBuilder.build()) // SSL errors handle karne ke liye unsafe client with Auth
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        // ApiService interface ko implement karke executable object return kar raha hai
        return retrofit.create(ApiService.class);
    }
}
