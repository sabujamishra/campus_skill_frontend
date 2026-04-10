package com.muproject.campusskill;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

// Yeh main screen hai jo fragments (Login/Register) ko host karti hai
public class MainActivity extends AppCompatActivity {

    private long backPressedTime;
    private android.widget.Toast backToast;
    public static java.util.List<Integer> myServiceIds = new java.util.ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // App ko hamesha Light Mode mein rakhne ke liye
        androidx.appcompat.app.AppCompatDelegate.setDefaultNightMode(androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_NO);
        
        // RetrofitClient ko context de rahe hain (Hinglish: Network engine start kar rahe hain context ke saath)
        com.muproject.campusskill.network.RetrofitClient.init(this);

        super.onCreate(savedInstanceState);
        // activity_main layout set kar raha hai jisme fragment container hai
        setContentView(R.layout.activity_main);

        // Standard Back logic (Hinglish: Fragment pops aur Double-back handling)
        getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                    getSupportFragmentManager().popBackStack();
                    return;
                }

                if (backPressedTime + 2000 > System.currentTimeMillis()) {
                    if (backToast != null) backToast.cancel();
                    finish();
                } else {
                    backToast = android.widget.Toast.makeText(MainActivity.this, "Press back again to exit", android.widget.Toast.LENGTH_SHORT);
                    backToast.show();
                }
                backPressedTime = System.currentTimeMillis();
            }
        });

        // Auth Guard: Token must exist to enter Dashboard (Hinglish: Bina token ke dashboard nahi khulega)
        com.muproject.campusskill.network.SessionManager sessionManager = new com.muproject.campusskill.network.SessionManager(this);
        String token = sessionManager.getToken();

        if (token != null && !token.isEmpty() && sessionManager.isRememberMe()) {
            // Token exists + Remember Me → Go straight to Dashboard
            refreshMyServiceIds();
            loadFragment(new DashboardFragment());
            return;
        } else {
            // No valid session → Clear everything and show Login
            sessionManager.clearSession();
        }

        // Default: Show Login page
        if (savedInstanceState == null) {
            loadFragment(new LoginFragment());
        }
    }

    // Pehle fragment (Login) ko container mein daalne ke liye function
    private void loadFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Enter Dashboard (Hinglish: Dashboard mein jaane ka fresh tarika, no back to login)
    public void loadMainFragment(Fragment fragment) {
        getSupportFragmentManager().popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE);
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }

    // Naye fragment par switch karne ke liye logic (Hinglish: Switch to a new fragment with backstack)
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    // Safe way to go back (Hinglish: Fragments se dashboard wapas jaane ka safe tarika)
    public void goBack() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            loadFragment(new DashboardFragment());
        }
    }

    public void refreshMyServiceIds() {
        com.muproject.campusskill.network.SessionManager session = new com.muproject.campusskill.network.SessionManager(this);
        String token = session.getToken();
        if (token != null && !token.isEmpty()) {
            com.muproject.campusskill.network.RetrofitClient.getApiService().getMyServices().enqueue(new retrofit2.Callback<com.muproject.campusskill.model.ServiceListResponse>() {
                @Override
                public void onResponse(retrofit2.Call<com.muproject.campusskill.model.ServiceListResponse> call, retrofit2.Response<com.muproject.campusskill.model.ServiceListResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        myServiceIds.clear();
                        if (response.body().getData() != null) {
                            for (com.muproject.campusskill.model.Service s : response.body().getData()) {
                                myServiceIds.add(s.getId());
                            }
                        }
                    }
                }
                @Override
                public void onFailure(retrofit2.Call<com.muproject.campusskill.model.ServiceListResponse> call, Throwable t) {}
            });
        }
    }
}