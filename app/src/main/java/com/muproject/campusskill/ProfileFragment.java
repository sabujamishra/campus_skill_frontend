package com.muproject.campusskill;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.muproject.campusskill.model.ProfileResponse;
import com.muproject.campusskill.network.RetrofitClient;
import com.muproject.campusskill.network.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// User Profile logic (Hinglish: Profile data dikhane aur edit karne wala fragment)
public class ProfileFragment extends Fragment {

    private TextView tvName, tvDept, tvEarnings, tvRating, tvScore;
    private ImageView ivProfileImage;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI
        tvName = view.findViewById(R.id.tvProfileName);
        tvDept = view.findViewById(R.id.tvProfileDept);
        tvEarnings = view.findViewById(R.id.tvEarnings);
        tvRating = view.findViewById(R.id.tvRating);
        tvScore = view.findViewById(R.id.tvScore);
        ivProfileImage = view.findViewById(R.id.ivUserProfileImage);

        Button btnEdit = view.findViewById(R.id.btnEditProfile);
        Button btnLogout = view.findViewById(R.id.btnLogout);

        sessionManager = new SessionManager(requireContext());

        // Profile data load karo (Hinglish: Server se profile details mangwa rahe hain)
        loadProfile();

        btnLogout.setOnClickListener(v -> logout());

        btnEdit.setOnClickListener(v -> showEditProfileDialog());
        
        view.findViewById(R.id.fabEditImage).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        return view;
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    android.net.Uri imageUri = result.getData().getData();
                    uploadProfileImage(imageUri);
                }
            }
    );

    private void loadProfile() {
        // Glide logic for image placeholder (Hinglish: Pehle se stored image agar hai toh profile pic dikhao)
        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        com.muproject.campusskill.model.User user = response.body().getData();
                        tvName.setText(user.getName());
                        tvDept.setText(user.getDepartment());
                        tvEarnings.setText("₹" + user.getTotalEarnings());
                        tvRating.setText(String.valueOf(user.getAverageRating()));
                        tvScore.setText(String.valueOf(user.getLeaderboardScore()));

                        // Glide image loading (Hinglish: Server URL se image load kar rahe hain)
                        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                            com.bumptech.glide.Glide.with(ProfileFragment.this)
                                    .load(user.getProfileImage())
                                    .placeholder(R.drawable.rounded_placeholder)
                                    .circleCrop()
                                    .into(ivProfileImage);
                        }
                    }
                } catch (Exception e) {}
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }

    private void uploadProfileImage(android.net.Uri uri) {
        // Instant feedback (Hinglish: Turant UI par image set kar rahi hai feedback ke liye)
        com.bumptech.glide.Glide.with(this).load(uri).circleCrop().into(ivProfileImage);

        try {
            Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
            // TODO: Add real Multipart upload logic here
        } catch (Exception e) {}
    }

    private void showEditProfileDialog() {
        // Edit Profile dialog implementation (Hinglish: Profile update karne wala popup)
        android.widget.EditText etName = new android.widget.EditText(requireContext());
        etName.setHint("New Name");
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Profile")
                .setView(etName)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    updateProfile(newName, tvDept.getText().toString());
                })
                .show();
    }

    private void updateProfile(String name, String dept) {
        com.muproject.campusskill.model.UpdateProfileRequest request = new com.muproject.campusskill.model.UpdateProfileRequest(name, dept);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadProfile();
                }
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {}
        });
    }

    private void logout() {
        // Session clear karo (Hinglish: Token tijori se hatao)
        sessionManager.clearSession();
        
        // App ko restart/main screen par bhej do
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
