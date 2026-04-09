package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.muproject.campusskill.model.Category;
import com.muproject.campusskill.model.CategoryResponse;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.ServiceCreateRequest;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Naya service create karne ka logic (Hinglish: Category select karne ke liye Spinner mapping use ho rahi hai)
public class CreateServiceFragment extends Fragment {

    private EditText etTitle, etDesc, etPrice, etTime;
    private Spinner spinnerCategory;
    private List<Category> categoryList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_service, container, false);

        // Initialize UI
        etTitle = view.findViewById(R.id.etServiceTitle);
        etDesc = view.findViewById(R.id.etServiceDesc);
        etPrice = view.findViewById(R.id.etServicePrice);
        etTime = view.findViewById(R.id.etServiceTime);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        Button btnCreate = view.findViewById(R.id.btnCreateService);
        android.widget.ImageView btnBack = view.findViewById(R.id.btnBackCreate);

        // Load Categories for Spinner (Hinglish: Backend se categories mangwa rahe hain)
        loadCategories();

        btnCreate.setOnClickListener(v -> handleCreateService());
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    for (Category cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }
                    
                    if (getContext() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, categoryNames);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {
                Toast.makeText(requireContext(), "Failed to load categories", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCreateService() {
        try {
            // Data nikal rahe hain form se
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();

            if (categoryList.isEmpty()) {
                Toast.makeText(requireContext(), "Catalog not loaded yet!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Selected Category ID nikalo (Hinglish: Index se ID nikal rahe hain)
            int selectedIdx = spinnerCategory.getSelectedItemPosition();
            int categoryId = categoryList.get(selectedIdx).getId();

            // Validation
            if (title.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int time = Integer.parseInt(timeStr);

            // API call
            android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
            progressDialog.setMessage("Launching your service...");
            progressDialog.setCancelable(false);
            progressDialog.show();

            ServiceCreateRequest request = new ServiceCreateRequest(title, desc, categoryId, price, time);

            RetrofitClient.getApiService().createService(request).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    progressDialog.dismiss();
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
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
