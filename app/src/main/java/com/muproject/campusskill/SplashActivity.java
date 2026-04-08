package com.muproject.campusskill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimatedVectorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

// Ye screen app khulte hi sabse pehle dikhti hai (Splash Screen)
public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_splash layout file ko set kar raha hai screen par
        setContentView(R.layout.activity_splash);

        // Logo image ko find kar raha hai taaki animation start kar sakein
        ImageView logoView = findViewById(R.id.logo_view);
        Drawable drawable = logoView.getBackground();
        
        // Agar logo animated background hai toh usey start kar do
        if (drawable instanceof AnimatedVectorDrawable) {
            ((AnimatedVectorDrawable) drawable).start();
        } else if (drawable instanceof AnimatedVectorDrawableCompat) {
            ((AnimatedVectorDrawableCompat) drawable).start();
        }

        // 3 seconds ka wait karega phir decide karega kaha jana hai
        new Handler().postDelayed(() -> {
            // "AppPrefs" name ki file se data check kar raha hai
            SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
            // Check kar raha hai ki app pehli baar install hui hai ya nahi
            boolean isFirstRun = prefs.getBoolean("isFirstRun", true);

            if (isFirstRun) {
                // Agar pehli baar hai toh Onboarding (Info) screen par le jao
                startActivity(new Intent(SplashActivity.this, OnboardingActivity.class));
            } else {
                // Agar pehle bhi khul chuki hai toh seedha Login/Home par le jao
                startActivity(new Intent(SplashActivity.this, MainActivity.class));
            }
            // Splash activity ko band kar do taaki user back na aa sake
            finish();
        }, 3000);
    }
}
