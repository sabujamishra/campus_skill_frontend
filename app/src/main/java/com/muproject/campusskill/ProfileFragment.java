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

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
        view.findViewById(R.id.btnMyServices).setOnClickListener(v -> ((MainActivity)requireActivity()).replaceFragment(new MyServicesFragment()));

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
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Loading profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                progressDialog.dismiss();
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
                        // Date format DD-MM-YYYY (Hinglish: Date ko readable format mein badlo)
                        String memberDate = "N/A";
                        if (user.getCreatedAt() != null) {
                            try {
                                java.text.SimpleDateFormat inputFmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                                java.text.SimpleDateFormat outputFmt = new java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault());
                                java.util.Date date = inputFmt.parse(user.getCreatedAt());
                                memberDate = outputFmt.format(date);
                            } catch (Exception ex) { memberDate = user.getCreatedAt().split(" ")[0]; }
                        }
                        tvMemberSince.setText("Member since: " + memberDate);

                        // Glide image loading (Hinglish: Server URL se image load kar rahe hain)
                        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty()) {
                            String imageUrl = user.getProfileImage();
                            if (!imageUrl.startsWith("http")) {
                                // Relative path logic (Hinglish: Agar adha URL hai toh pura domain jodo)
                                imageUrl = "https://lightgrey-dogfish-642647.hostingersite.com/" + imageUrl;
                            }

                            com.bumptech.glide.Glide.with(ProfileFragment.this)
                                    .load(imageUrl)
                                    .placeholder(R.drawable.rounded_placeholder)
                                    .error(R.drawable.rounded_placeholder) // Error fallback
                                    .circleCrop()
                                    .listener(new com.bumptech.glide.request.RequestListener<android.graphics.drawable.Drawable>() {
                                        @Override
                                        public boolean onLoadFailed(@Nullable com.bumptech.glide.load.engine.GlideException e, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, boolean isFirstResource) {
                                            android.util.Log.e("GlideError", "Image load failed: " + (e != null ? e.getMessage() : "unknown"));
                                            return false;
                                        }
                                        @Override
                                        public boolean onResourceReady(android.graphics.drawable.Drawable resource, Object model, com.bumptech.glide.request.target.Target<android.graphics.drawable.Drawable> target, com.bumptech.glide.load.DataSource dataSource, boolean isFirstResource) {
                                            return false;
                                        }
                                    })
                                    .into(ivProfileImage);
                        }
                    }
                } catch (Exception e) {
                    Toast.makeText(requireContext(), "Parse Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Image turant dikhao aur server par upload karo (Hinglish: Photo select → dikhao → upload karo)
    private void uploadProfileImage(android.net.Uri uri) {
        // Instant local preview
        com.bumptech.glide.Glide.with(this).load(uri).circleCrop().into(ivProfileImage);
        Toast.makeText(requireContext(), "Uploading image...", Toast.LENGTH_SHORT).show();

        try {
            // Uri se Bitmap load karo compression ke liye (Hinglish: Pehle image ko memory mein lo compress karne ke liye)
            android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            
            // Image size compress karo (70% quality usually brings it under 2MB easily)
            java.io.File tempFile = new java.io.File(requireContext().getCacheDir(), "profile_compressed.jpg");
            java.io.OutputStream outputStream = new java.io.FileOutputStream(tempFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.flush();
            outputStream.close();

            // Multipart request body banao
            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), tempFile);
            okhttp3.MultipartBody.Part imagePart = okhttp3.MultipartBody.Part.createFormData("profile_image", "profile.jpg", requestBody);

            // Server par upload karo
            android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
            progressDialog.setMessage("Uploading photo...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            RetrofitClient.getApiService().uploadProfileImage(imagePart).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful()) {
                        Toast.makeText(requireContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                        loadProfile();
                    } else {
                        // Server error body parse karo (Hinglish: Server ne kya galat bataya dekhte hain)
                        String errorMsg = "Upload failed: " + response.code();
                        try {
                            if (response.errorBody() != null) {
                                errorMsg = response.errorBody().string();
                            }
                        } catch (Exception ex) {}
                        Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Upload error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Toast.makeText(requireContext(), "File error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    // Profile edit dialog (Hinglish: Name, Email, Department change karne wala popup)
    private void showEditProfileDialog() {
        android.widget.LinearLayout layout = new android.widget.LinearLayout(requireContext());
        layout.setOrientation(android.widget.LinearLayout.VERTICAL);
        layout.setPadding(60, 40, 60, 0);

        android.widget.EditText etName = new android.widget.EditText(requireContext());
        etName.setHint("Name");
        etName.setText(tvName.getText());
        layout.addView(etName);

        android.widget.EditText etEmailEdit = new android.widget.EditText(requireContext());
        etEmailEdit.setHint("Email");
        etEmailEdit.setText(tvEmail.getText());
        layout.addView(etEmailEdit);

        android.widget.EditText etDeptEdit = new android.widget.EditText(requireContext());
        etDeptEdit.setHint("Department");
        etDeptEdit.setText(tvDept.getText());
        layout.addView(etDeptEdit);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Profile")
                .setView(layout)
                .setPositiveButton("Update", (dialog, which) -> {
                    String newName = etName.getText().toString().trim();
                    String newEmail = etEmailEdit.getText().toString().trim();
                    String newDept = etDeptEdit.getText().toString().trim();
                    if (!newName.isEmpty()) {
                        updateProfile(newName, newEmail, newDept);
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // Profile update karo (Hinglish: Server par naam, email, dept update bhejo)
    private void updateProfile(String name, String email, String dept) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        com.muproject.campusskill.model.UpdateProfileRequest request = new com.muproject.campusskill.model.UpdateProfileRequest(name, email, dept);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadProfile(); // Re-fetch to refresh UI (Hinglish: Dobara data lo taaki naya naam dikhe)
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                progressDialog.dismiss();
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
