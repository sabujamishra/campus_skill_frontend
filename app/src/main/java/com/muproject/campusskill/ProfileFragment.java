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
import com.muproject.campusskill.model.User;
import com.muproject.campusskill.network.RetrofitClient;
import com.muproject.campusskill.network.SessionManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// User Profile logic (Hinglish: Profile data dikhane aur edit karne wala fragment)
public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvDept, tvEarnings, tvRating, tvScore;
    private TextView tvOrders, tvRepeatClients, tvResponseRate, tvMemberSince;
    private ImageView ivProfileImage;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements (Hinglish: Saare text views ko connect kar rahe hain)
        tvName = view.findViewById(R.id.tvProfileName);
        tvEmail = view.findViewById(R.id.tvProfileEmail);
        tvDept = view.findViewById(R.id.tvProfileDept);
        tvEarnings = view.findViewById(R.id.tvEarnings);
        tvRating = view.findViewById(R.id.tvRating);
        tvScore = view.findViewById(R.id.tvScore);
        tvOrders = view.findViewById(R.id.tvOrders);
        tvRepeatClients = view.findViewById(R.id.tvRepeatClients);
        tvResponseRate = view.findViewById(R.id.tvResponseRate);
        tvMemberSince = view.findViewById(R.id.tvMemberSince);
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

    // Image picker launcher (Hinglish: Gallery se photo chunne ka launcher)
    private final androidx.activity.result.ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    android.net.Uri imageUri = result.getData().getData();
                    uploadProfileImage(imageUri);
                }
            }
    );

    // Server se profile data load karo (Hinglish: API se data mangwa ke UI mein set karo)
    private void loadProfile() {
        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        User user = response.body().getData();

                        // Sabhi fields set karo (Hinglish: Response ke saare data dikhao)
                        tvName.setText(user.getName() != null ? user.getName() : "N/A");
                        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
                        tvDept.setText(user.getDepartment() != null ? user.getDepartment() : "N/A");
                        tvEarnings.setText("₹" + (user.getTotalEarnings() != null ? user.getTotalEarnings() : "0"));
                        tvRating.setText(String.valueOf(user.getAverageRating()));
                        tvScore.setText(String.valueOf(user.getLeaderboardScore()));
                        tvOrders.setText(String.valueOf(user.getTotalCompletedOrders()));
                        tvRepeatClients.setText(String.valueOf(user.getRepeatClients()));
                        tvResponseRate.setText(user.getResponseRate() + "%");
                        tvMemberSince.setText("Member since: " + (user.getCreatedAt() != null ? user.getCreatedAt().split(" ")[0] : "N/A"));

                        // Glide image loading (Hinglish: Server URL se image load kar rahe hain)
                        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                            com.bumptech.glide.Glide.with(ProfileFragment.this)
                                    .load(user.getProfileImage())
                                    .placeholder(R.drawable.rounded_placeholder)
                                    .circleCrop()
                                    .into(ivProfileImage);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Image turant dikhao aur upload karo (Hinglish: Photo select hote hi dikhao)
    private void uploadProfileImage(android.net.Uri uri) {
        com.bumptech.glide.Glide.with(this).load(uri).circleCrop().into(ivProfileImage);
        Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();
        // TODO: Add real Multipart upload logic here
    }

    // Profile edit dialog (Hinglish: Naam change karne wala popup)
    private void showEditProfileDialog() {
        android.widget.EditText etName = new android.widget.EditText(requireContext());
        etName.setHint("New Name");
        etName.setText(tvName.getText());
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Profile")
                .setView(etName)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        updateProfile(newName, tvDept.getText().toString());
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Profile update karo (Hinglish: Server par naam update bhejo)
    private void updateProfile(String name, String dept) {
        com.muproject.campusskill.model.UpdateProfileRequest request = new com.muproject.campusskill.model.UpdateProfileRequest(name, dept);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadProfile(); // Re-fetch to refresh UI (Hinglish: Dobara data lo taaki naya naam dikhe)
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Update Failed: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Logout (Hinglish: Token hata ke login screen par bhejo)
    private void logout() {
        sessionManager.clearSession();
        Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
