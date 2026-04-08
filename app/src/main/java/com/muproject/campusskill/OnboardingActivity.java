package com.muproject.campusskill;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

// App ki info dikhane wali screen (Intro Pages)
public class OnboardingActivity extends AppCompatActivity {

    private ViewPager2 viewPager;
    private Button btnNext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_onboarding layout file ko load kar raha hai
        setContentView(R.layout.activity_onboarding);

        // UI elements ko link kar raha hai
        viewPager = findViewById(R.id.viewPager);
        btnNext = findViewById(R.id.btnNext);

        // ViewPager ko batane ke liye ki pages kaise dikhane hain (Adapter use hota hai)
        OnboardingAdapter adapter = new OnboardingAdapter(this);
        viewPager.setAdapter(adapter);

        // Jab page change hoga, tab check karega ki button ka text kya hona chahiye
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                // Agar last page (index 2) hai toh button "Start" dikhao
                if (position == 2) {
                    btnNext.setText("Start");
                } else {
                    // Baaki pages ke liye "Next" dikhao
                    btnNext.setText("Next");
                }
            }
        });

        // Button click hone par kya hoga
        btnNext.setOnClickListener(v -> {
            if (viewPager.getCurrentItem() < 2) {
                // Agar last page nahi hai toh agle page par jao
                viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            } else {
                // Agar "Start" click kiya (last page), toh storage mein save kar do ki intro dekh liya hai
                SharedPreferences prefs = getSharedPreferences("AppPrefs", MODE_PRIVATE);
                prefs.edit().putBoolean("isFirstRun", false).apply();
                
                // Final login screen par le jao
                startActivity(new Intent(OnboardingActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    // Yeh class manage karti hai ki ViewPager mein kaunsa page dikhana hai
    private static class OnboardingAdapter extends FragmentStateAdapter {
        public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
            super(fragmentActivity);
        }

        @NonNull
        @Override
        public Fragment createFragment(int position) {
            // Har page ke liye alag text aur title bhej raha hai
            switch (position) {
                case 0:
                    return OnboardingFragment.newInstance("Welcome", "CampusSkill is a peer-to-peer micro-service marketplace for students.");
                case 1:
                    return OnboardingFragment.newInstance("Connect", "Search for services or offer your own skills to fellow students.");
                case 2:
                    return OnboardingFragment.newInstance("Grow", "Build your portfolio and earn while studying.");
                default:
                    return new OnboardingFragment();
            }
        }

        @Override
        public int getItemCount() {
            // Total 3 intro pages hain
            return 3;
        }
    }
}
