package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// About Us Component (Hinglish: App ki history aur mission page)
public class AboutFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        ImageView btnBack = view.findViewById(R.id.btnBackAbout);
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });

        android.widget.TextView tvVersion = view.findViewById(R.id.tvAppVersion);
        
        // Dynamic Version Fetch (Hinglish: API se current version mangwa rahe hain)
        com.muproject.campusskill.network.RetrofitClient.getApiService().getVersion().enqueue(new retrofit2.Callback<com.muproject.campusskill.model.VersionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.muproject.campusskill.model.VersionResponse> call, retrofit2.Response<com.muproject.campusskill.model.VersionResponse> response) {
                if (!isAdded()) return;
                if (response.isSuccessful() && response.body() != null) {
                    if (response.body().getData() != null && response.body().getData().getVersion() != null) {
                        tvVersion.setText("Version " + response.body().getData().getVersion());
                    } else {
                        tvVersion.setText("Version 1.0.0");
                    }
                } else {
                    tvVersion.setText("Version 1.0.0");
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.muproject.campusskill.model.VersionResponse> call, Throwable t) {
                if (isAdded()) {
                    tvVersion.setText("Version 1.0.0 (Offline)");
                }
            }
        });

        return view;
    }
}
