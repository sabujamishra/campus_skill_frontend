package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.muproject.campusskill.model.Category;
import com.muproject.campusskill.model.CategoryResponse;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.ServiceCreateRequest;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Naya service create karne ka logic (Hinglish: Category creation functionality add kari gayi hai)
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
        TextView tvAddCategory = view.findViewById(R.id.tvAddCategory);
        Button btnCreate = view.findViewById(R.id.btnCreateService);
        android.widget.ImageView btnBack = view.findViewById(R.id.btnBackCreate);

        setupPlaceholderSpinner();
        loadCategories();

        btnCreate.setOnClickListener(v -> handleCreateService());
        btnBack.setOnClickListener(v -> getParentFragmentManager().popBackStack());
        
        // Category creation dialog (Hinglish: Naya category banane ke liye popup)
        tvAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Category");

        final EditText input = new EditText(requireContext());
        input.setHint("Category Name (e.g. Video Editing)");
        builder.setView(input);

        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) {
                createNewCategory(name);
            } else {
                Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void createNewCategory(String name) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Adding category...");
        pd.show();

        Map<String, String> body = new HashMap<>();
        body.put("name", name);

        RetrofitClient.getApiService().createCategory(body).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                pd.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(requireContext(), "Category added successfully!", Toast.LENGTH_SHORT).show();
                    loadCategories(); 
                } else {
                    String errorMsg = "Failed";
                    try {
                        if (response.errorBody() != null) {
                            errorMsg = response.errorBody().string();
                        }
                    } catch (Exception e) {}
                    Toast.makeText(requireContext(), "Status " + response.code() + ": " + errorMsg, Toast.LENGTH_LONG).show();
                    Log.e("CreateService", "Category Add Error: " + errorMsg);
                }
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPlaceholderSpinner() {
        List<String> placeholder = new ArrayList<>();
        placeholder.add("Loading categories...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, placeholder);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    categoryList = response.body().getData();
                    List<String> categoryNames = new ArrayList<>();
                    for (Category cat : categoryList) {
                        categoryNames.add(cat.getName());
                    }
                    
                    if (isAdded() && getContext() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, categoryNames);
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                    }
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    private void handleCreateService() {
        try {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();

            if (categoryList.isEmpty() || spinnerCategory.getSelectedItem().toString().contains("Loading")) {
                Toast.makeText(requireContext(), "Please select a valid category!", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIdx = spinnerCategory.getSelectedItemPosition();
            int categoryId = categoryList.get(selectedIdx).getId();

            if (title.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int time = Integer.parseInt(timeStr);

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
