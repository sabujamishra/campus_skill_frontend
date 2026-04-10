package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
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

// Marketplace Home screen (Hinglish: Lifecycle safety aur non-blocking load add kiya gaya hai)
public class HomeFragment extends Fragment {

    private RecyclerView rvServices, rvCategories;
    private ServiceAdapter serviceAdapter;
    private CategoryAdapter categoryAdapter;
    private EditText etSearch;
    private Integer currentCategoryId = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Initialize UI
        etSearch = view.findViewById(R.id.etSearch);
        rvServices = view.findViewById(R.id.rvServices);
        rvCategories = view.findViewById(R.id.rvCategories);

        // Setup Services List
        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        com.muproject.campusskill.network.SessionManager sessionManager = new com.muproject.campusskill.network.SessionManager(requireContext());
        int userId = sessionManager.getUserId();
        serviceAdapter = new ServiceAdapter(new ArrayList<>(), userId);
        rvServices.setAdapter(serviceAdapter);

        // Setup Categories List
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new CategoryAdapter(new ArrayList<>(), category -> {
            currentCategoryId = (category.getId() == -1) ? null : category.getId();
            loadServices();
        });
        rvCategories.setAdapter(categoryAdapter);

        // Search logic
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadServices();
                return true;
            }
            return false;
        });

        // Initial Data Fetch
        loadCategories();
        loadServices();
        loadHeaderProfile(view);

        // "View All" logic
        view.findViewById(R.id.tvViewAllHome).setOnClickListener(v -> {
            if (getParentFragment() instanceof DashboardFragment && getActivity() != null) {
                View navView = getActivity().findViewById(R.id.bottom_navigation);
                if (navView instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                    ((com.google.android.material.bottomnavigation.BottomNavigationView) navView).setSelectedItemId(R.id.nav_services);
                }
            }
        });

        // FAB to open Create Service screen
        view.findViewById(R.id.fabCreateService).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(new CreateServiceFragment());
            }
        });

        return view;
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<CategoryResponse>() {
            @Override
            public void onResponse(Call<CategoryResponse> call, Response<CategoryResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<Category> cats = new ArrayList<>();
                    cats.add(new Category(-1, "All", 0));
                    cats.addAll(response.body().getData());
                    categoryAdapter.setCategories(cats);
                }
            }
            @Override
            public void onFailure(Call<CategoryResponse> call, Throwable t) {}
        });
    }

    private void loadServices() {
        String searchQuery = etSearch.getText().toString().trim();
        if (searchQuery.isEmpty()) searchQuery = null;

        // Lifecycle tip: No blocking ProgressDialog for discovery screens, it causes crashes on back navigation
        RetrofitClient.getApiService().getServices(currentCategoryId, searchQuery).enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    serviceAdapter.setServices(response.body().getData());
                } else {
                    Log.e("HomeFragment", "Service load failed: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                if (!isAdded() || getContext() == null) return;
                Log.e("HomeFragment", "Network Error in Home", t);
            }
        });
    }

    private void loadHeaderProfile(View view) {
        android.widget.ImageView ivHeader = view.findViewById(R.id.ivHeaderProfile);
        RetrofitClient.getApiService().getMyProfile().enqueue(new Callback<com.muproject.campusskill.model.ProfileResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.ProfileResponse> call, Response<com.muproject.campusskill.model.ProfileResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    String img = response.body().getData().getProfileImage();
                    if (img != null && !img.isEmpty() && ivHeader != null) {
                        String url = img.startsWith("http") ? img : "https://lightgrey-dogfish-642647.hostingersite.com/" + img;
                        com.bumptech.glide.Glide.with(HomeFragment.this)
                                .load(url)
                                .placeholder(R.drawable.ic_profile)
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
