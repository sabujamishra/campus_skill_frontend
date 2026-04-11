package com.muproject.campusskill.network;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

// Token aur user session securely save karne ke liye manager
public class SessionManager {
    private static final String PREF_NAME = "CampusSkillSecurePrefs";
    private static final String KEY_TOKEN = "auth_token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_USER_IMAGE = "user_image";
    
    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        try {
            // Tijori ki chabi: Android Keystore se encryption key generate karna
            MasterKey masterKey = new MasterKey.Builder(context)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            // Encrypted SharedPreferences initialize kar rahe hain
            sharedPreferences = EncryptedSharedPreferences.create(
                    context,
                    PREF_NAME,
                    masterKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
            // Agar encryption fail ho toh fallback to normal prefs (not ideal but safe for app not to crash)
            sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        }
    }

    // Token save karne ke liye function
    public void saveToken(String token) {
        sharedPreferences.edit().putString(KEY_TOKEN, token).apply();
    }

    // Token wapas nikalne (retrieve) ke liye
    public String getToken() {
        return sharedPreferences.getString(KEY_TOKEN, null);
    }

    // User details save karne ke liye
    public void saveUserDetails(int id, String name, String imageUrl) {
        sharedPreferences.edit()
                .putInt(KEY_USER_ID, id)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_USER_IMAGE, imageUrl)
                .apply();
    }

    public int getUserId() {
        return sharedPreferences.getInt(KEY_USER_ID, -1);
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, null);
    }

    public String getUserImage() {
        return sharedPreferences.getString(KEY_USER_IMAGE, null);
    }

    // Logout karne par token delete karne ke liye
    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    // Auto-login setting save karne ke liye
    public void setRememberMe(boolean value) {
        sharedPreferences.edit().putBoolean("remember_me", value).apply();
    }

    // Remember Me flag check karo
    public boolean isRememberMe() {
        return sharedPreferences.getBoolean("remember_me", false);
    }
}
