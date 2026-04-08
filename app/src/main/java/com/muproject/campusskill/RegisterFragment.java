package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

// Naya account banane (Register) wali screen ka logic handle ho raha hai
public class RegisterFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // fragment_register layout ko host activity par lagane ke liye (inflate)
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        // UI elements ko link kar raha hai (Hinglish: Elements ko variable se connect kar raha hai)
        ImageView btnBack = view.findViewById(R.id.btnBack);
        Button btnSignUp = view.findViewById(R.id.btnSignUp);
        
        android.widget.EditText etFullName = view.findViewById(R.id.etFullName);
        android.widget.EditText etEmail = view.findViewById(R.id.etEmailRegister);
        android.widget.EditText etDepartment = view.findViewById(R.id.etDepartment);
        android.widget.EditText etPassword = view.findViewById(R.id.etPasswordRegister);
        android.widget.EditText etConfirmPassword = view.findViewById(R.id.etConfirmPassword);

        // Agar user piche (Login screen par) wapas jana chahe
        btnBack.setOnClickListener(v -> {
            // Activity ko back press command bhej raha hai
            requireActivity().onBackPressed();
        });

        // Sign Up button press hone par logic (Hinglish: Jab Sign Up dabayein)
        btnSignUp.setOnClickListener(v -> {
            try {
                // Input fields se text nikal rahe hain
                String name = etFullName.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String dept = etDepartment.getText().toString().trim();
                String pass = etPassword.getText().toString().trim();
                String confirmPass = etConfirmPassword.getText().toString().trim();

                // Validation: Check if fields are empty
                if (name.isEmpty() || email.isEmpty() || dept.isEmpty() || pass.isEmpty()) {
                    throw new Exception("Please fill all fields!");
                }

                // Check if passwords match
                if (!pass.equals(confirmPass)) {
                    throw new Exception("Passwords do not match!");
                }

                // Agar sab sahi hai toh API call shuru karo
                performRegistration(name, email, dept, pass);

            } catch (Exception e) {
                // Agar koi bhi error (Exception) aaye toh message dikhao bin if-else ke
                android.widget.Toast.makeText(requireContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // Function to send data to the backend
    private void performRegistration(String name, String email, String dept, String pass) {
        // Show progress dialog
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Creating account... please wait.");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Create request object
        com.muproject.campusskill.model.RegisterRequest request = new com.muproject.campusskill.model.RegisterRequest(name, email, dept, pass);
        
        com.muproject.campusskill.network.RetrofitClient.getApiService().register(request).enqueue(new retrofit2.Callback<com.muproject.campusskill.model.RegisterResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.muproject.campusskill.model.RegisterResponse> call, retrofit2.Response<com.muproject.campusskill.model.RegisterResponse> response) {
                progressDialog.dismiss(); // Dismiss loader
                
                try {
                    if (response.isSuccessful() && response.body() != null) {
                        if ("success".equals(response.body().getStatus())) {
                            android.widget.Toast.makeText(requireContext(), "Registration successful! Please login.", android.widget.Toast.LENGTH_LONG).show();
                            requireActivity().onBackPressed();
                        } else {
                            throw new Exception(response.body().getMessage());
                        }
                    } else {
                        // Agar error aaya (jaise 409: Email exists), toh errorBody parse karo (Hinglish: Error message nikal rahe hain)
                        String errorMsg = "Server Error: " + response.code();
                        if (response.errorBody() != null) {
                            com.google.gson.Gson gson = new com.google.gson.Gson();
                            com.muproject.campusskill.model.RegisterResponse errorResponse = 
                                gson.fromJson(response.errorBody().charStream(), com.muproject.campusskill.model.RegisterResponse.class);
                            if (errorResponse != null && errorResponse.getMessage() != null) {
                                errorMsg = errorResponse.getMessage(); // Jaise "Email already exists"
                            }
                        }
                        throw new Exception(errorMsg);
                    }
                } catch (Exception e) {
                    android.widget.Toast.makeText(requireContext(), e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.muproject.campusskill.model.RegisterResponse> call, Throwable t) {
                progressDialog.dismiss(); // Loader hata do
                android.util.Log.e("RegisterAPI", "Error: ", t);
                android.widget.Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }
}
