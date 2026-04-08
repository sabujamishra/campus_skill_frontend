package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Onboarding viewpager ka ek individual page handling fragment
public class OnboardingFragment extends Fragment {

    private static final String ARG_TITLE = "title";
    private static final String ARG_DESC = "desc";

    // Naya page banane ke liye helper function, jo data pass karta hai
    public static OnboardingFragment newInstance(String title, String desc) {
        OnboardingFragment fragment = new OnboardingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TITLE, title);
        args.putString(ARG_DESC, desc);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_onboarding layout file ko fragment par chipka raha hai (inflate)
        View view = inflater.inflate(R.layout.fragment_onboarding, container, false);
        TextView titleTv = view.findViewById(R.id.onboardingTitle);
        TextView descTv = view.findViewById(R.id.onboardingDesc);

        // Pass kiya hua Title aur Description page par set kar raha hai
        if (getArguments() != null) {
            titleTv.setText(getArguments().getString(ARG_TITLE));
            descTv.setText(getArguments().getString(ARG_DESC));
        }

        return view;
    }
}
