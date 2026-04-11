package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

// Dashboard screen: Jahan main bottom navigation bar host hoti hai
public class DashboardFragment extends Fragment {

    // Pichli baar kaunsa tab khula tha uska record rakhne ke liye
    public static int lastSelectedId = R.id.nav_home;

    public static void setTab(int navId) {
        lastSelectedId = navId;
    }
    private BottomNavigationView bottomNav;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Back navigation interceptor: Kisi bhi tab se Home par wapas bhej deta hai
        requireActivity().getOnBackPressedDispatcher().addCallback(this, new androidx.activity.OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (bottomNav != null && bottomNav.getSelectedItemId() != R.id.nav_home) {
                    bottomNav.setSelectedItemId(R.id.nav_home);
                } else {
                    setEnabled(false);
                    requireActivity().getOnBackPressedDispatcher().onBackPressed();
                    setEnabled(true);
                }
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);

        bottomNav = view.findViewById(R.id.bottom_navigation);

        // Bottom bar ke items select hone par fragment switch karna
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_services) {
                // Services browse karne wala fragment
                selectedFragment = new ServicesFragment();
            } else if (id == R.id.nav_orders) {
                // Orders history dikhane ke liye
                selectedFragment = new OrdersFragment();
            } else if (id == R.id.nav_profile) {
                // Profile section load karo
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                lastSelectedId = id; // Yaad rakho kaunsa tab select hua
                getChildFragmentManager().beginTransaction()
                        .replace(R.id.dashboard_container, selectedFragment)
                        .commit();
            }
            return true;
        });

        bottomNav.setSelectedItemId(lastSelectedId);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (bottomNav != null && bottomNav.getSelectedItemId() != lastSelectedId) {
            bottomNav.setSelectedItemId(lastSelectedId);
        }
    }
}
