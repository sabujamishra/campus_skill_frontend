package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.ServiceCreateRequest;
import com.muproject.campusskill.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Naya service create karne ka logic (Hinglish: Service post karne wala fragment)
public class CreateServiceFragment extends Fragment {

    private EditText etTitle, etDesc, etPrice, etTime, etCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_service, container, false);

        // Initialize UI (Hinglish: Form ke saare fields connect kar rahe hain)
        etTitle = view.findViewById(R.id.etServiceTitle);
        etDesc = view.findViewById(R.id.etServiceDesc);
        etPrice = view.findViewById(R.id.etServicePrice);
        etTime = view.findViewById(R.id.etServiceTime);
        etCategory = view.findViewById(R.id.etServiceCategory);
        Button btnCreate = view.findViewById(R.id.btnCreateService);
        android.widget.ImageView btnBack = view.findViewById(R.id.btnBackCreate);

        btnCreate.setOnClickListener(v -> handleCreateService());
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void handleCreateService() {
        try {
            // Data nikal rahe hain form se
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();
            String catStr = etCategory.getText().toString().trim();

            // Validation (Hinglish: Check kar rahe hain ki koi field khali na ho)
            if (title.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || timeStr.isEmpty() || catStr.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int time = Integer.parseInt(timeStr);
            int categoryId = Integer.parseInt(catStr);

            // Request object
            ServiceCreateRequest request = new ServiceCreateRequest(title, desc, categoryId, price, time);

            // API call logic (Hinglish: Server par naya service post kar rahe hain)
            android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
            progressDialog.setMessage("Launching your service...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            RetrofitClient.getApiService().createService(request).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        // Dashboard par wapas bhej do ya clear form (Hinglish: Success hone par pichle screen par)
                        requireActivity().getSupportFragmentManager().popBackStack();
                    } else {
                        Toast.makeText(requireContext(), "Failed to create service!", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    progressDialog.dismiss();
                    Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid input format!", Toast.LENGTH_SHORT).show();
        }
    }
}
