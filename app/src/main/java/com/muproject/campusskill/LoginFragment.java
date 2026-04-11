package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Login screen ka logic yahan handle ho raha hai
public class LoginFragment extends Fragment {
    private android.widget.CheckBox cbRememberMe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_login layout ko inflate kar raha hai
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        // UI components link kar raha hai
        android.widget.EditText etEmail = view.findViewById(R.id.etEmail);
        android.widget.EditText etPassword = view.findViewById(R.id.etPassword);
        TextView tvRegister = view.findViewById(R.id.tvRegister);
        Button btnLogin = view.findViewById(R.id.btnLogin);
        cbRememberMe = view.findViewById(R.id.cbRememberMe);

        // Sabse pehle version consistency check karenge
        checkVersion();

        // Agar user "Register" link par click kare
        tvRegister.setOnClickListener(v -> {
            // MainActivity ko RegisterFragment par switch karne ke liye bol raha hai
            ((MainActivity) requireActivity()).replaceFragment(new RegisterFragment());
        });

        // Login button press hone par validation aur API call ka logic
        btnLogin.setOnClickListener(v -> {
            try {
                String email = etEmail.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();

                // Pehle check karo ki fields khali toh nahi
                if (email.isEmpty() || pass.isEmpty()) {
                    throw new Exception("Please enter email and password!");
                }

                // API call shuru karo
                performLogin(email, pass);

            } catch (Exception e) {
                android.widget.Toast.makeText(requireContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Login API call handling function
    private void performLogin(String email, String pass) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Logging in... please wait.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        com.muproject.campusskill.model.LoginRequest request = new com.muproject.campusskill.model.LoginRequest(email, pass);

        com.muproject.campusskill.network.RetrofitClient.getApiService().login(request).enqueue(new retrofit2.Callback<com.muproject.campusskill.model.LoginResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.muproject.campusskill.model.LoginResponse> call, retrofit2.Response<com.muproject.campusskill.model.LoginResponse> response) {
                progressDialog.dismiss();
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if ("success".equals(response.body().getStatus())) {
                            if (!isAdded()) return;
                            
                            // Token save karna must hai naye requests ke liye
                            com.muproject.campusskill.network.SessionManager sessionManager = new com.muproject.campusskill.network.SessionManager(requireContext());
                            sessionManager.saveToken(response.body().getData().getToken());
                            sessionManager.saveUserDetails(response.body().getData().getUser().getId(), 
                                    response.body().getData().getUser().getName(),
                                    response.body().getData().getUser().getProfileImage());
                            
                            // Remember Me off hone par app band hone par token clear hoga
                            sessionManager.setRememberMe(cbRememberMe.isChecked());
                            
                            android.widget.Toast.makeText(requireContext(), "Welcome back, " + response.body().getData().getUser().getName(), android.widget.Toast.LENGTH_SHORT).show();
                            
                            // Login successful, ab dashboard par le jao
                            if (getActivity() instanceof MainActivity) {
                                ((MainActivity) getActivity()).refreshMyServiceIds();
                                ((MainActivity) getActivity()).loadMainFragment(new DashboardFragment());
                            }
                        } else {
                            throw new Exception(response.body().getMessage());
                        }
                    } else {
                        // Error handling: Email/Password galat hone par specific error parse karo
                        String errorMsg = "Login failed: " + response.code();
                        if (response.errorBody() != null) {
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            com.muproject.campusskill.model.CommonResponse errorResponse = 
                                gson.fromJson(response.errorBody().charStream(), com.muproject.campusskill.model.CommonResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage(); // Safe message extraction
                            }
                        }
                        throw new Exception(errorMsg);
                    }
                } catch (Exception e) {
                    android.widget.Toast.makeText(requireContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.muproject.campusskill.model.LoginResponse> call, Throwable t) {
                progressDialog.dismiss();
                android.widget.Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Server se version match karne ka function
    private void checkVersion() {
        com.muproject.campusskill.network.RetrofitClient.getApiService().getVersion().enqueue(new retrofit2.Callback<com.muproject.campusskill.model.VersionResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.muproject.campusskill.model.VersionResponse> call, retrofit2.Response<com.muproject.campusskill.model.VersionResponse> response) {
                try {
                    // Agar response theek nahi aaya toh seedha error state mein
                    if (!response.isSuccessful() || response.body() == null) {
                        throw new Exception("Server communication failed.");
                    }

                    String serverVersion = response.body().getData().getVersion();
                    String appVersion = "1.0.0"; 

                    // Version match check
                    if (!appVersion.equals(serverVersion)) {
                        showUpdateDialog();
                    }
                } catch (Exception e) {
                    android.util.Log.e("VersionCheck", e.getMessage());
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.muproject.campusskill.model.VersionResponse> call, Throwable t) {
                // Async failure case
            }
        });
    }

    // Update karne ke liye popup dikhany ka function
    private void showUpdateDialog() {
        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Update Available")
                .setMessage("A new version of CampusSkill is available. Please update to continue.")
                .setCancelable(false) // User ise skip nahi kar sakta
                .setPositiveButton("Update Now", (dialog, which) -> {
                    // Yahan play store ya web link par bhej sakte hain
                })
                .show();
    }
}
