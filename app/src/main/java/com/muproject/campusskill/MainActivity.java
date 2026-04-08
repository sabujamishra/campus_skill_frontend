package com.muproject.campusskill;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

// Yeh main screen hai jo fragments (Login/Register) ko host karti hai
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // activity_main layout set kar raha hai jisme fragment container hai
        setContentView(R.layout.activity_main);

        // Jab app start hogi, agar pehle se koi state save nahi hai toh Login page load karo
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

    // Dusre/Naye fragment (Jaise Register) par switch karne ke liye function
    public void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
                // Naya fragment puraane wale ki jagah le lega
                .replace(R.id.fragment_container, fragment)
                // Back button dabane par pichle fragment par wapas ja sakein
                .addToBackStack(null)
                // Switch hone par transition animation dikhao
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                .commit();
    }
}