package com.muproject.campusskill.network;

import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// Retrofit initialization singleton: Yahan se pura network connectivity manage hoti hai
public class RetrofitClient {
    private static final String BASE_URL = "https://lightgrey-dogfish-642647.hostingersite.com/api/";
    private static Retrofit retrofit = null;
    private static android.content.Context appContext;

    // App startup par context set karna taaki sessionManager ko token mil sake
    public static void init(android.content.Context context) {
        appContext = context.getApplicationContext();
    }

    // ApiService ka singleton instance banata hai network transactions ke liye
    public static ApiService getApiService() {
        // Singleton pattern: Check kar raha hai ki kya Retrofit pehle se bana hua hai?
        if (retrofit == null) {
            okhttp3.OkHttpClient.Builder clientBuilder = SslUtils.getUnsafeOkHttpClient();

            // Interceptor: Har request mein token load karne ke liye
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

            // Global Auth Guard: Server 401 (Unauthorized) de toh seedha login par phek do
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
