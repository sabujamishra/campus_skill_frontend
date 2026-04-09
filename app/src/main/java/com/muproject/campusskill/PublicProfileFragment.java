package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.muproject.campusskill.model.User;
import com.muproject.campusskill.model.ProfileResponse;
import com.muproject.campusskill.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicProfileFragment extends Fragment {

    private int userId;
    private ImageView ivProfile;
    private TextView tvName, tvDept, tvOrders, tvRating, tvResponse, tvMemberSince;

    public static PublicProfileFragment newInstance(int userId) {
        PublicProfileFragment fragment = new PublicProfileFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("user_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_profile, container, false);

        ivProfile = view.findViewById(R.id.ivPublicProfileImage);
        tvName = view.findViewById(R.id.tvPublicName);
        tvDept = view.findViewById(R.id.tvPublicDept);
        tvOrders = view.findViewById(R.id.tvPublicOrders);
        tvRating = view.findViewById(R.id.tvPublicRating);
        tvResponse = view.findViewById(R.id.tvPublicResponse);
        tvMemberSince = view.findViewById(R.id.tvPublicMemberSince);

        view.findViewById(R.id.btnBackProfile).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });

        loadPublicProfile();

        return view;
    }

    private void loadPublicProfile() {
        if (userId <= 0) {
            Toast.makeText(getContext(), "Error: Valid User ID not found in service data.", Toast.LENGTH_LONG).show();
            return;
        }

        RetrofitClient.getApiService().getPublicProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    updateUI(response.body().getData());
                } else {
                    String error = "Unknown Error";
                    try { if (response.errorBody() != null) error = response.errorBody().string(); } catch (Exception e) {}
                    Toast.makeText(getContext(), "Server Error (" + response.code() + "): " + error, Toast.LENGTH_LONG).show();
                    Log.e("PublicProfile", "Failed to load ID " + userId + ". Code: " + response.code() + " Error: " + error);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (isAdded()) {
                    Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) return;

        tvName.setText(user.getName());
        tvDept.setText(user.getDepartment());
        tvOrders.setText(String.valueOf(user.getTotalCompletedOrders()));
        tvRating.setText(String.format("%.1f", user.getAverageRating()));
        tvResponse.setText(user.getResponseRate() + "%");

        if (user.getCreatedAt() != null) {
            String date = user.getCreatedAt().split(" ")[0];
            tvMemberSince.setText("Member since " + date);
        }

        // Image loading with safety (Hinglish: Photo load karne ka safe tarikha)
        String imgPath = user.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            String url;
            if (imgPath.startsWith("http")) {
                url = imgPath;
            } else {
                String baseUrl = "https://lightgrey-dogfish-642647.hostingersite.com/";
                if (imgPath.startsWith("/")) imgPath = imgPath.substring(1);
                url = baseUrl + imgPath;
            }

            Glide.with(this)
                    .load(url)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.service_placeholder)
                    .circleCrop()
                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                            Log.e("PublicProfile", "Glide Load Failed for URL: " + url, e);
                            return false; 
                        }
                        @Override
                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                            Log.d("PublicProfile", "Glide Load Success: " + url);
                            return false;
                        }
                    })
                    .into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.ic_profile);
        }
    }
}
