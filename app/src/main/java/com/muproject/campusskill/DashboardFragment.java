package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Main App Dashboard handling fragment (Hinglish: App ka main hub jahan bottom bar hai)
public class DashboardFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        BottomNavigationView bottomNav = view.findViewById(R.id.bottom_navigation);

        // Sidebar click handling logic (Hinglish: Bar ke items click hone par fragments change karna)
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_services) {
                // Future: ServicesFragment
                selectedFragment = new HomeFragment(); // Placeholder
            } else if (id == R.id.nav_orders) {
                // Future: OrdersFragment
                selectedFragment = new HomeFragment(); // Placeholder
            } else if (id == R.id.nav_profile) {
                // Profile screen load karo (Hinglish: Naye Profile fragment par switch)
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.dashboard_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        // Default: Home fragment load karo jab dashboard shuru ho
        if (savedInstanceState == null) {
            getChildFragmentManager().beginTransaction()
                    .replace(R.id.dashboard_container, new HomeFragment())
                    .commit();
        }

        return view;
    }
}
