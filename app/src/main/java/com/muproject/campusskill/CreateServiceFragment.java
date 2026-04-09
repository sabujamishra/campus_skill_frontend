package com.muproject.campusskill;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Naya service create karne ka logic with Image Upload (Hinglish: Image upload capability add kari gayi hai)
public class CreateServiceFragment extends Fragment {

    private EditText etTitle, etDesc, etPrice, etTime;
    private Spinner spinnerCategory;
    private ImageView ivServicePreview;
    private LinearLayout layoutUploadPlaceholder;
    private List<Category> categoryList = new ArrayList<>();
    private Uri selectedImageUri;

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
        ivServicePreview = view.findViewById(R.id.ivServicePreview);
        layoutUploadPlaceholder = view.findViewById(R.id.layoutUploadPlaceholder);
        
        TextView tvAddCategory = view.findViewById(R.id.tvAddCategory);
        Button btnCreate = view.findViewById(R.id.btnCreateService);
        ImageView btnBack = view.findViewById(R.id.btnBackCreate);

        setupPlaceholderSpinner();
        loadCategories();

        btnCreate.setOnClickListener(v -> handleCreateService());
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });
        
        tvAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        // Image selection click
        view.findViewById(R.id.cardUploadImage).setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            imagePickerLauncher.launch(intent);
        });

        return view;
    }

    private final androidx.activity.result.ActivityResultLauncher<Intent> imagePickerLauncher = registerForActivityResult(
            new androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == android.app.Activity.RESULT_OK && result.getData() != null) {
                    selectedImageUri = result.getData().getData();
                    ivServicePreview.setImageURI(selectedImageUri);
                    ivServicePreview.setVisibility(View.VISIBLE);
                    layoutUploadPlaceholder.setVisibility(View.GONE);
                }
            }
    );

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

    private void setupPlaceholderSpinner() {
        List<String> placeholder = new ArrayList<>();
        placeholder.add("Loading categories...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.spinner_item, placeholder);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void handleCreateService() {
        try {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();

            if (categoryList.isEmpty() || spinnerCategory.getSelectedItem().toString().contains("Loading")) {
                Toast.makeText(requireContext(), "Select a valid category", Toast.LENGTH_SHORT).show();
                return;
            }

            int selectedIdx = spinnerCategory.getSelectedItemPosition();
            int categoryId = categoryList.get(selectedIdx).getId();

            if (title.isEmpty() || desc.isEmpty() || priceStr.isEmpty() || timeStr.isEmpty()) {
                Toast.makeText(requireContext(), "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }

            double price = Double.parseDouble(priceStr);
            int time = Integer.parseInt(timeStr);

            android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
            pd.setMessage("Creating service...");
            pd.setCancelable(false);
            pd.show();

            ServiceCreateRequest request = new ServiceCreateRequest(title, desc, categoryId, price, time);

            RetrofitClient.getApiService().createService(request).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        // Service creation success, now check for image
                        Object data = response.body().getData();
                        int serviceId = -1;
                        
                        // Extract ID from response data (Map)
                        if (data instanceof Map) {
                            try {
                                Double idDouble = (Double) ((Map) data).get("id");
                                if (idDouble != null) serviceId = idDouble.intValue();
                            } catch (Exception e) {}
                        }

                        if (serviceId != -1 && selectedImageUri != null) {
                            pd.setMessage("Uploading cover image...");
                            uploadServiceImage(serviceId, pd);
                        } else {
                            pd.dismiss();
                            Toast.makeText(requireContext(), "Service Published!", Toast.LENGTH_SHORT).show();
                            requireActivity().getSupportFragmentManager().popBackStack();
                        }
                    } else {
                        pd.dismiss();
                        Toast.makeText(requireContext(), "Failed to create service", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });

        } catch (Exception e) {
            Toast.makeText(requireContext(), "Invalid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private void uploadServiceImage(int serviceId, android.app.ProgressDialog pd) {
        try {
            // Process Image
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), selectedImageUri);
            File tempFile = new File(requireContext().getCacheDir(), "service_img_" + serviceId + ".jpg");
            OutputStream os = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, os);
            os.flush();
            os.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("service_image", tempFile.getName(), reqFile);
            RequestBody serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(serviceId));

            RetrofitClient.getApiService().uploadServiceImage(serviceIdBody, imagePart).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    pd.dismiss();
                    Toast.makeText(requireContext(), "Service & Image Published!", Toast.LENGTH_SHORT).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }

                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    pd.dismiss();
                    Toast.makeText(requireContext(), "Image upload failed, but service was created.", Toast.LENGTH_LONG).show();
                    requireActivity().getSupportFragmentManager().popBackStack();
                }
            });

        } catch (Exception e) {
            pd.dismiss();
            Log.e("CreateService", "Image process error", e);
            requireActivity().getSupportFragmentManager().popBackStack();
        }
    }

    private void showAddCategoryDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Create New Category");
        final EditText input = new EditText(requireContext());
        input.setHint("Category Name");
        builder.setView(input);
        builder.setPositiveButton("Create", (dialog, which) -> {
            String name = input.getText().toString().trim();
            if (!name.isEmpty()) createNewCategory(name);
        });
        builder.setNegativeButton("Cancel", null);
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
                    Toast.makeText(requireContext(), "Category added!", Toast.LENGTH_SHORT).show();
                    loadCategories();
                }
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) { pd.dismiss(); }
        });
    }
}
