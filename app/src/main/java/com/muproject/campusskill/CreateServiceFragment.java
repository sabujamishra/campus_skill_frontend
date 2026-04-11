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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.muproject.campusskill.model.Category;
import com.muproject.campusskill.model.CategoryResponse;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.Service;
import com.muproject.campusskill.model.ServiceCreateRequest;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreateServiceFragment extends Fragment {

    private static final String TAG = "CreateServiceFragment";
    private EditText etTitle, etDesc, etPrice, etTime;
    private Spinner spinnerCategory;
    private ImageView ivServicePreview;
    private LinearLayout layoutUploadPlaceholder;
    private List<Category> categoryList = new ArrayList<>();
    private Uri selectedImageUri;
    private Service existingService;

    public static CreateServiceFragment newInstance(Service service) {
        CreateServiceFragment fragment = new CreateServiceFragment();
        Bundle args = new Bundle();
        args.putSerializable("existing_service", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            existingService = (Service) getArguments().getSerializable("existing_service");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_service, container, false);

        etTitle = view.findViewById(R.id.etServiceTitle);
        etDesc = view.findViewById(R.id.etServiceDesc);
        etPrice = view.findViewById(R.id.etServicePrice);
        etTime = view.findViewById(R.id.etServiceTime);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        ivServicePreview = view.findViewById(R.id.ivServicePreview);
        layoutUploadPlaceholder = view.findViewById(R.id.layoutUploadPlaceholder);
        
        Button btnPublish = view.findViewById(R.id.btnCreateService);
        ImageView btnBack = view.findViewById(R.id.btnBackCreate);

        setupPlaceholderSpinner();
        loadCategories();

        if (existingService != null) {
            btnPublish.setText("Update Service");
            etTitle.setText(existingService.getTitle());
            etDesc.setText(existingService.getDescription());
            etPrice.setText(String.valueOf(existingService.getPrice()));
            etTime.setText(String.valueOf(existingService.getDeliveryTime()));
            
            if (existingService.getThumbnail() != null && !existingService.getThumbnail().isEmpty()) {
                String thumbUrl = existingService.getThumbnail();
                String url = thumbUrl.startsWith("http") ? thumbUrl : "https://lightgrey-dogfish-642647.hostingersite.com/" + (thumbUrl.startsWith("/") ? thumbUrl.substring(1) : thumbUrl);
                com.bumptech.glide.Glide.with(this).load(url).into(ivServicePreview);
                ivServicePreview.setVisibility(View.VISIBLE);
                layoutUploadPlaceholder.setVisibility(View.GONE);
            }
        }

        btnPublish.setOnClickListener(v -> handlePublishOrUpdate());
        btnBack.setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });
        
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
                    int selectionIdx = 0;
                    for (int i = 0; i < categoryList.size(); i++) {
                        Category cat = categoryList.get(i);
                        categoryNames.add(cat.getName());
                        if (existingService != null && cat.getName().equals(existingService.getCategory())) selectionIdx = i;
                    }
                    if (isAdded() && getContext() != null) {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, categoryNames);
                        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
                        spinnerCategory.setAdapter(adapter);
                        if (existingService != null) spinnerCategory.setSelection(selectionIdx);
                    }
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    private void setupPlaceholderSpinner() {
        if (!isAdded() || getContext() == null) return;
        List<String> placeholder = new ArrayList<>();
        placeholder.add("Loading categories...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item, placeholder);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void handlePublishOrUpdate() {
        try {
            String title = etTitle.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();
            String priceStr = etPrice.getText().toString().trim();
            String timeStr = etTime.getText().toString().trim();

            if (categoryList.isEmpty() || spinnerCategory.getSelectedItem() == null || spinnerCategory.getSelectedItem().toString().contains("Loading")) {
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

            android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
            pd.setMessage("Syncing service details...");
            pd.setCancelable(false);
            pd.show();

            proceedToServiceApi(pd, title, desc, categoryId, price, time);

        } catch (Exception e) {
            Toast.makeText(getContext(), "Invalid inputs", Toast.LENGTH_SHORT).show();
        }
    }

    private void proceedToServiceApi(android.app.ProgressDialog pd, String title, String desc, int catId, double price, int time) {
        ServiceCreateRequest request = new ServiceCreateRequest(title, desc, catId, price, time);
        
        Call<CommonResponse> call = (existingService == null) 
                ? RetrofitClient.getApiService().createService(request)
                : RetrofitClient.getApiService().updateService(existingService.getId(), request);

        call.enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    int serviceId = (existingService != null) ? existingService.getId() : extractIdFromResponse(response.body());
                    Log.d(TAG, "Service synced. ID detected: " + serviceId);
                    
                    if (serviceId != -1 && selectedImageUri != null) {
                        pd.setMessage("Uploading thumbnail...");
                        uploadAndLinkImage(serviceId, pd, title, desc, catId, price, time);
                    } else if (selectedImageUri != null) {
                        pd.setMessage("Detecting new service ID...");
                        fetchLatestServiceAndUpload(pd, title, desc, catId, price, time);
                    } else {
                        pd.dismiss();
                        finishFlow("Success!");
                    }
                } else {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Failed to sync service data", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchLatestServiceAndUpload(android.app.ProgressDialog pd, String title, String desc, int catId, double price, int time) {
        RetrofitClient.getApiService().getMyServices().enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Service> myServices = response.body().getData();
                    int latestId = -1;
                    for (Service s : myServices) {
                        if (s.getTitle().equalsIgnoreCase(title) && s.getId() > latestId) latestId = s.getId();
                    }
                    if (latestId == -1 && !myServices.isEmpty()) {
                        for (Service s : myServices) if (s.getId() > latestId) latestId = s.getId();
                    }

                    if (latestId != -1) {
                        pd.setMessage("Uploading thumbnail...");
                        uploadAndLinkImage(latestId, pd, title, desc, catId, price, time);
                    } else {
                        pd.dismiss();
                        finishFlow("Service created, but could not detect ID.");
                    }
                } else {
                    pd.dismiss();
                    finishFlow("Service created, but failed to fetch ID list.");
                }
            }
            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                pd.dismiss();
                finishFlow("Fetch list network error.");
            }
        });
    }

    private void uploadAndLinkImage(int serviceId, android.app.ProgressDialog pd, String title, String desc, int catId, double price, int time) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), selectedImageUri);
            
            // Resize if too large (Max 1024px)
            if (bitmap.getWidth() > 1024 || bitmap.getHeight() > 1024) {
                float ratio = Math.min(1024f / bitmap.getWidth(), 1024f / bitmap.getHeight());
                bitmap = Bitmap.createScaledBitmap(bitmap, (int)(bitmap.getWidth()*ratio), (int)(bitmap.getHeight()*ratio), true);
            }

            File tempFile = new File(getContext().getCacheDir(), "service_" + serviceId + ".jpg");
            OutputStream os = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, os);
            os.flush();
            os.close();

            RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
            // Changed key from "image" to "service_image" to match common backend naming
            MultipartBody.Part imagePart = MultipartBody.Part.createFormData("service_image", tempFile.getName(), reqFile);
            RequestBody serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(serviceId));

            RetrofitClient.getApiService().uploadServiceImage(serviceIdBody, imagePart).enqueue(new Callback<CommonResponse>() {
                @Override
                public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String imagePath = extractPath(response.body());
                        if (imagePath != null) {
                            pd.setMessage("Linking image...");
                            updateServiceWithThumbnail(serviceId, imagePath, pd, title, desc, catId, price, time);
                        } else {
                            pd.dismiss();
                            finishFlow("Upload success, but path extraction failed.");
                        }
                    } else {
                        // If "service_image" failed, let's try with "image" as key (fallback)
                        Log.w(TAG, "Upload with service_image failed, trying fallback 'image' key...");
                        tryUploadWithFallbackKey(serviceId, tempFile, pd, title, desc, catId, price, time);
                    }
                }
                @Override
                public void onFailure(Call<CommonResponse> call, Throwable t) {
                    pd.dismiss();
                    finishFlow("Upload network error: " + t.getMessage());
                }
            });
        } catch (Exception e) {
            pd.dismiss();
            finishFlow("Error processing image: " + e.getMessage());
        }
    }

    private void tryUploadWithFallbackKey(int serviceId, File tempFile, android.app.ProgressDialog pd, String title, String desc, int catId, double price, int time) {
        RequestBody reqFile = RequestBody.create(MediaType.parse("image/jpeg"), tempFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", tempFile.getName(), reqFile);
        RequestBody serviceIdBody = RequestBody.create(MediaType.parse("text/plain"), String.valueOf(serviceId));

        RetrofitClient.getApiService().uploadServiceImage(serviceIdBody, imagePart).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String imagePath = extractPath(response.body());
                    if (imagePath != null) {
                        updateServiceWithThumbnail(serviceId, imagePath, pd, title, desc, catId, price, time);
                    } else {
                        pd.dismiss();
                        finishFlow("Fallback upload success, but path extraction failed.");
                    }
                } else {
                    pd.dismiss();
                    String err = "Rejected by server.";
                    try { if (response.errorBody() != null) err = response.errorBody().string(); } catch (Exception e) {}
                    finishFlow("Image upload failed: " + err);
                }
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                pd.dismiss();
                finishFlow("Fallback upload network error.");
            }
        });
    }

    private void updateServiceWithThumbnail(int serviceId, String path, android.app.ProgressDialog pd, String title, String desc, int catId, double price, int time) {
        ServiceCreateRequest request = new ServiceCreateRequest(title, desc, catId, price, time);
        request.setServiceImage(path);

        RetrofitClient.getApiService().updateService(serviceId, request).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                pd.dismiss();
                finishFlow("Service Published with Image!");
            }
            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                pd.dismiss();
                finishFlow("Service created, but final link failed.");
            }
        });
    }

    private int extractIdFromResponse(CommonResponse body) {
        if (body == null) return -1;
        if (body.getServiceId() != null) return body.getServiceId();
        if (body.getId() != null) return body.getId();
        int idFromData = findIdInObject(body.getData());
        if (idFromData != -1) return idFromData;
        
        String msg = body.getMessage();
        if (msg != null && !msg.isEmpty()) {
            Pattern pattern = Pattern.compile("(?:id|ID|service_id)[:\\s]+(\\d+)", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(msg);
            if (matcher.find()) return Integer.parseInt(matcher.group(1));
            Pattern lastResort = Pattern.compile("(\\d+)");
            Matcher lastMatcher = lastResort.matcher(msg);
            if (lastMatcher.find()) return Integer.parseInt(lastMatcher.group(1));
        }
        return -1;
    }

    private int findIdInObject(Object obj) {
        if (obj == null) return -1;
        if (obj instanceof Number) return ((Number) obj).intValue();
        if (obj instanceof String) try { return Integer.parseInt((String) obj); } catch (Exception e) {}
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            String[] keys = {"id", "service_id", "serviceID", "ID", "insert_id"};
            for (String key : keys) {
                Object val = map.get(key);
                if (val != null) {
                    int id = findIdInObject(val);
                    if (id != -1) return id;
                }
            }
            for (Object val : map.values()) {
                if (val instanceof Map) {
                    int nestedId = findIdInObject(val);
                    if (nestedId != -1) return nestedId;
                }
            }
        }
        return -1;
    }

    private String extractPath(CommonResponse body) {
        if (body == null) return null;
        Object data = body.getData();
        if (data instanceof String && ((String) data).contains(".")) return (String) data;
        if (data instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) data;
            String[] keys = {"path", "url", "thumbnail", "image", "service_image"};
            for (String key : keys) {
                Object val = map.get(key);
                if (val instanceof String && ((String) val).contains(".")) return (String) val;
            }
        }
        String msg = body.getMessage();
        if (msg != null && msg.contains(".") && (msg.contains("/") || msg.length() > 5)) return msg;
        return null;
    }

    private void finishFlow(String msg) {
        if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).refreshMyServiceIds();
        Toast.makeText(getContext(), msg, Toast.LENGTH_SHORT).show();
        if (getActivity() != null) getActivity().getSupportFragmentManager().popBackStack();
    }
}
