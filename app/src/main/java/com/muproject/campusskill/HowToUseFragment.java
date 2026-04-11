package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Guide Fragment: Users ko app use karne ka tarika sikhane ke liye (Hinglish: Naya explanatory page)
public class HowToUseFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_how_to_use, container, false);

        ImageView btnBack = view.findViewById(R.id.btnBackHow);
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });

        return view;
    }
}
