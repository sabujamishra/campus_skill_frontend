package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.adapter.CategoryAdapter;
import com.muproject.campusskill.adapter.ServiceAdapter;
import com.muproject.campusskill.model.Category;
import com.muproject.campusskill.model.CategoryResponse;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Marketplace Home screen (Hinglish: Dynamic categories aur services filter ho rahe hain)
public class HomeFragment extends Fragment {

    private RecyclerView rvServices, rvCategories;
    private ServiceAdapter serviceAdapter;
    private CategoryAdapter categoryAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup Services List
        rvServices = view.findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new LinearLayoutManager(requireContext())); 
        serviceAdapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(serviceAdapter);

        // Setup Categories List
        rvCategories = view.findViewById(R.id.rvCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            // Category click logic (Hinglish: Filter ke saath services dobara load karo)
            if (category.getId() == -1) {
                loadServices(null); // -1 remains "All"
            } else {
                loadServices(category.getId());
            }
        });
        rvCategories.setAdapter(categoryAdapter);

        // Initial Data Fetch
        loadCategories();
        loadServices(null);
        loadHeaderProfile(view);

        // FAB to open Create Service screen
        view.findViewById(R.id.fabCreateService).setOnClickListener(v -> {
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateServiceFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> cats = new ArrayList<>();
                    cats.add(new Category(-1, "All", 0)); // Static "All" option
                    cats.addAll(response.body().getData());
                    categoryAdapter.setCategories(cats);
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    private void loadServices(Integer categoryId) {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Updating marketplace...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RetrofitClient.getApiService().getServices(categoryId, null).enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    serviceAdapter.setServices(response.body().getData());
                } else {
                    Toast.makeText(requireContext(), "Failed to load services", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                progressDialog.dismiss();
                Toast.makeText(requireContext(), "Network Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadHeaderProfile(View view) {
        android.widget.ImageView ivHeader = view.findViewById(R.id.ivHeaderProfile);
        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<com.muproject.campusskill.model.ProfileResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.ProfileResponse> call, Response<com.muproject.campusskill.model.ProfileResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String img = response.body().getData().getProfileImage();
                    if (img != null && !img.isEmpty()) {
                        String url = img.startsWith("http") ? img : "https://lightgrey-dogfish-642647.hostingersite.com/" + img;
                        com.bumptech.glide.Glide.with(HomeFragment.this)
                                .load(url)
                                .placeholder(R.drawable.ic_profile)
                                .error(R.drawable.ic_profile)
                                .circleCrop()
                                .into(ivHeader);
                    }
                }
            }
            @Override
            public void onFailure(Call<com.muproject.campusskill.model.ProfileResponse> call, Throwable t) {}
        });
    }
}
