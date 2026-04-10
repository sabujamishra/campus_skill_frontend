package com.muproject.campusskill;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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

// User Profile logic (Hinglish: Background loading safety aur crash preventer add kiye gaye hain)
public class ProfileFragment extends Fragment {

    private TextView tvName, tvEmail, tvDept, tvEarnings, tvRating, tvScore;
    private TextView tvOrders, tvRepeatClients, tvResponseRate, tvMemberSince;
    private ImageView ivProfileImage;
    private SessionManager sessionManager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
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

        sessionManager = new SessionManager(requireContext());

        // Profile data load karo (Hinglish: Bina blocking loader ke taaki back aane par crash na ho)
        loadProfile();

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> logout());
        view.findViewById(R.id.btnEditProfile).setOnClickListener(v -> showEditProfileDialog());
        view.findViewById(R.id.btnMyServices).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new MyServicesFragment());
            }
        });
        view.findViewById(R.id.btnLeaderboard).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new LeaderboardFragment());
            }
        });

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
        // Lifecycle Tip: Blocking dialogs in onCreateView lead to crashes during fragment restoration
        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (!isAdded() || getContext() == null) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    User user = response.body().getData();
                    updateUI(user);
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("ProfileFragment", "Load failed", t);
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) return;
        tvName.setText(user.getName() != null ? user.getName() : "N/A");
        tvEmail.setText(user.getEmail() != null ? user.getEmail() : "N/A");
        tvDept.setText(user.getDepartment() != null ? user.getDepartment() : "N/A");
        tvEarnings.setText("₹" + (user.getTotalEarnings() != null ? user.getTotalEarnings() : "0"));
        tvRating.setText(String.valueOf(user.getAverageRating()));
        tvScore.setText(String.valueOf(user.getLeaderboardScore()));
        tvOrders.setText(String.valueOf(user.getTotalCompletedOrders()));
        tvRepeatClients.setText(String.valueOf(user.getRepeatClients()));
        tvResponseRate.setText(user.getResponseRate() + "%");

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

        if (user.getProfileImage() != null && !user.getProfileImage().isEmpty() && isAdded()) {
            String imageUrl = user.getProfileImage();
            if (!imageUrl.startsWith("http")) {
                imageUrl = "https://lightgrey-dogfish-642647.hostingersite.com/" + imageUrl;
            }
            com.bumptech.glide.Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.rounded_placeholder)
                    .circleCrop()
                    .into(ivProfileImage);
        }
    }

    private void uploadProfileImage(android.net.Uri uri) {
        if (getContext() == null) return;
        com.bumptech.glide.Glide.with(this).load(uri).circleCrop().into(ivProfileImage);
        
        try {
            android.graphics.Bitmap bitmap = android.provider.MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), uri);
            java.io.File tempFile = new java.io.File(requireContext().getCacheDir(), "profile_compressed.jpg");
            java.io.OutputStream outputStream = new java.io.FileOutputStream(tempFile);
            bitmap.compress(android.graphics.Bitmap.CompressFormat.JPEG, 70, outputStream);
            outputStream.flush();
            outputStream.close();

            okhttp3.RequestBody requestBody = okhttp3.RequestBody.create(okhttp3.MediaType.parse("image/jpeg"), tempFile);
            okhttp3.MultipartBody.Part imagePart = okhttp3.MultipartBody.Part.createFormData("profile_image", "profile.jpg", requestBody);

            android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(getContext());
            progressDialog.setMessage("Uploading photo...");
            progressDialog.show();

            RetrofitClient.getApiService().uploadProfileImage(imagePart).enqueue(new Callback<ProfileResponse>() {
                @Override
                public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    if (!isAdded()) return;
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Image uploaded!", Toast.LENGTH_SHORT).show();
                        loadProfile();
                    }
                }
                @Override
                public void onFailure(Call<ProfileResponse> call, Throwable t) {
                    if (progressDialog.isShowing()) progressDialog.dismiss();
                    if (isAdded()) Toast.makeText(getContext(), "Upload error", Toast.LENGTH_SHORT).show();
                }
            });
        } catch (Exception e) {
            Log.e("Profile", "Upload failed", e);
        }
    }

    private void showEditProfileDialog() {
        if (getContext() == null) return;
        
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_update_profile, null);
        com.google.android.material.textfield.TextInputEditText etName = dialogView.findViewById(R.id.etEditName);
        com.google.android.material.textfield.TextInputEditText etEmail = dialogView.findViewById(R.id.etEditEmail);
        com.google.android.material.textfield.TextInputEditText etDept = dialogView.findViewById(R.id.etEditDept);
        com.google.android.material.button.MaterialButton btnSave = dialogView.findViewById(R.id.btnDialogSave);
        com.google.android.material.button.MaterialButton btnCancel = dialogView.findViewById(R.id.btnDialogCancel);

        // Pre-fill existing data
        etName.setText(tvName.getText());
        etEmail.setText(tvEmail.getText());
        etDept.setText(tvDept.getText());

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        btnSave.setOnClickListener(v -> {
            String newName = etName.getText().toString().trim();
            String newEmail = etEmail.getText().toString().trim();
            String newDept = etDept.getText().toString().trim();
            if (!newName.isEmpty()) {
                dialog.dismiss();
                updateProfile(newName, newEmail, newDept);
            }
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private void updateProfile(String name, String email, String dept) {
        if (getContext() == null) return;
        android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
        pd.setMessage("Updating...");
        pd.show();

        com.muproject.campusskill.model.UpdateProfileRequest request = new com.muproject.campusskill.model.UpdateProfileRequest(name, email, dept);
        RetrofitClient.getApiService().updateProfile(request).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (pd.isShowing()) pd.dismiss();
                if (isAdded() && response.isSuccessful()) {
                    Toast.makeText(getContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
                    loadProfile();
                }
            }
            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (pd.isShowing()) pd.dismiss();
            }
        });
    }

    private void logout() {
        if (sessionManager != null) sessionManager.clearSession();
        Intent intent = new Intent(requireActivity(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        requireActivity().finish();
    }
}
