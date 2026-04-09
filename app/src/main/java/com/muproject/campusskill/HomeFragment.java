package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Home screen placeholder (Hinglish: Home screen ka basic structure)
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(android.R.layout.simple_list_item_1, container, false);
        TextView tv = view.findViewById(android.R.id.text1);
        tv.setText("Welcome to CampusSkill Home!\nServices Marketplace coming soon...");
        tv.setPadding(50, 50, 50, 50);
        return view;
    }
}
