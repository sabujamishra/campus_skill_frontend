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

    // Tab state persistence (Hinglish: Yaad rakhein pichli baar kaunsa tab khula tha)
    private static int lastSelectedId = R.id.nav_home;
    private BottomNavigationView bottomNav;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Back interception (Hinglish: Kisi bhi tab se Home par wapas bhejene ka logic)
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

        // Sidebar click handling logic (Hinglish: Bar ke items click hone par fragments change karna)
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();

            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_services) {
                // Discover services (Hinglish: Services browse karne wala page)
                selectedFragment = new ServicesFragment();
            } else if (id == R.id.nav_orders) {
                // Future: OrdersFragment
                selectedFragment = new HomeFragment(); // Placeholder
            } else if (id == R.id.nav_profile) {
                // Profile screen load karo (Hinglish: Naye Profile fragment par switch)
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

        // Default: Pichla select kiya hua tab load karo (Hinglish: Puraana selection restore karo)
        bottomNav.setSelectedItemId(lastSelectedId);

        return view;
    }
}
