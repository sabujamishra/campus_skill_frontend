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
import com.muproject.campusskill.adapter.ServiceAdapter;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Market Discovery: Isme non-blocking load add kiya hai crashes prevent karne ke liye
public class ServicesFragment extends Fragment {

    private RecyclerView rvServices, rvCategories;
    private ServiceAdapter adapter;
    private com.muproject.campusskill.adapter.CategoryAdapter categoryAdapter;
    private EditText etSearch;
    private Integer currentCategoryId = null;
    private int lastSortId = R.id.chipNewest;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        etSearch = view.findViewById(R.id.etSearchMarket);
        rvServices = view.findViewById(R.id.rvMarketServices);
        View btnFilter = view.findViewById(R.id.btnFilter);

        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        com.muproject.campusskill.network.SessionManager sessionManager = new com.muproject.campusskill.network.SessionManager(requireContext());
        int userId = sessionManager.getUserId();
        adapter = new ServiceAdapter(new ArrayList<>(), userId);
        rvServices.setAdapter(adapter);

        rvCategories = view.findViewById(R.id.rvMarketCategories);
        rvCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        categoryAdapter = new com.muproject.campusskill.adapter.CategoryAdapter(new ArrayList<>(), category -> {
            currentCategoryId = (category.getId() == -1) ? null : category.getId();
            loadServices(etSearch.getText().toString().trim(), true);
        });
        rvCategories.setAdapter(categoryAdapter);

        // Filter button click logic
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> showFilterBottomSheet());
        }

        loadCategories();
        loadServices(null, false);

        etSearch.setHint("Search anything...");
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadServices(etSearch.getText().toString().trim(), true);
                return true;
            }
            return false;
        });

        return view;
    }

    private void loadServices(String query, boolean showLoader) {
        // Only show blocking loader if explicitly requested (e.g. search click)
        android.app.ProgressDialog pd = null;
        if (showLoader && getContext() != null) {
            pd = new android.app.ProgressDialog(getContext());
            pd.setMessage("Searching...");
            pd.show();
        }

        final android.app.ProgressDialog finalPd = pd;
        RetrofitClient.getApiService().getServices(currentCategoryId, query).enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (finalPd != null) finalPd.dismiss();
                if (!isAdded() || getContext() == null) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setServices(response.body().getData());
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                if (finalPd != null) finalPd.dismiss();
                if (!isAdded()) return;
            }
        });
    }

    private void loadCategories() {
        RetrofitClient.getApiService().getCategories().enqueue(new Callback<com.muproject.campusskill.model.CategoryResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.CategoryResponse> call, Response<com.muproject.campusskill.model.CategoryResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    List<com.muproject.campusskill.model.Category> cats = new ArrayList<>();
                    cats.add(new com.muproject.campusskill.model.Category(-1, "All", 0));
                    cats.addAll(response.body().getData());
                    categoryAdapter.setCategories(cats);
                }
            }
            @Override
            public void onFailure(Call<com.muproject.campusskill.model.CategoryResponse> call, Throwable t) {}
        });
    }

    private void showFilterBottomSheet() {
        if (getContext() == null) return;
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());
        View bottomSheetView = getLayoutInflater().inflate(R.layout.layout_filter_bottom_sheet, null);
        dialog.setContentView(bottomSheetView);

        com.google.android.material.chip.ChipGroup cgSort = bottomSheetView.findViewById(R.id.cgSort);
        View btnApply = bottomSheetView.findViewById(R.id.btnApplyFilters);

        // Purana selection dikhao group mein
        cgSort.check(lastSortId);

        btnApply.setOnClickListener(v -> {
            lastSortId = cgSort.getCheckedChipId();
            applySorting(lastSortId);
            dialog.dismiss();
            Toast.makeText(getContext(), "Filters applied", Toast.LENGTH_SHORT).show();
        });

        dialog.show();
    }

    private void applySorting(int checkedId) {
        if (adapter == null || adapter.getServices() == null) return;
        List<com.muproject.campusskill.model.Service> currentList = new ArrayList<>(adapter.getServices());
        if (currentList.isEmpty()) return;

        if (checkedId == R.id.chipPriceLow) {
            java.util.Collections.sort(currentList, (s1, s2) -> Double.compare(parsePrice(s1.getPrice()), parsePrice(s2.getPrice())));
        } else if (checkedId == R.id.chipPriceHigh) {
            java.util.Collections.sort(currentList, (s1, s2) -> Double.compare(parsePrice(s2.getPrice()), parsePrice(s1.getPrice())));
        } else if (checkedId == R.id.chipNewest) {
            java.util.Collections.sort(currentList, (s1, s2) -> Integer.compare(s2.getId(), s1.getId()));
        }

        adapter.setServices(currentList);
    }

    private double parsePrice(String price) {
        if (price == null || price.isEmpty()) return 0.0;
        try {
            return Double.parseDouble(price.replaceAll("[^0-9.]", "").trim());
        } catch (Exception e) {
            return 0.0;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        loadCategories();
        loadServices(null, false);
    }
}
