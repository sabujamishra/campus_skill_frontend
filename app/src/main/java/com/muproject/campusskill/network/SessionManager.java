package com.muproject.campusskill.network;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;
import java.io.IOException;
import java.security.GeneralSecurityException;

// Token aur user session ko securely save karne ke liye manager (Hinglish: Token save karne waali safe)
public class SessionManager {
    private static final String PREF_NAME = "CampusSkillSecurePrefs";
    private static final String KEY_TOKEN = "auth_token";
    
    private SharedPreferences sharedPreferences;

    public SessionManager(Context context) {
        try {
            // Android Keystore se ek Master Key bana rahe hain (Hinglish: Tijori ki chabi)
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

    // Logout karne par token delete karne ke liye
    public void clearSession() {
        sharedPreferences.edit().clear().apply();
    }

    // Remember Me flag save karo (Hinglish: Auto-login on/off rakhne ke liye)
    public void setRememberMe(boolean value) {
        sharedPreferences.edit().putBoolean("remember_me", value).apply();
    }

    // Remember Me flag check karo
    public boolean isRememberMe() {
        return sharedPreferences.getBoolean("remember_me", false);
    }
}
