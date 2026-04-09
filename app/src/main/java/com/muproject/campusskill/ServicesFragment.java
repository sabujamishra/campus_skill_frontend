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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Market Discovery Fragment (Hinglish: Safe context fetching aur loading logic update kiya gaya hai)
public class ServicesFragment extends Fragment {

    private RecyclerView rvServices;
    private ServiceAdapter adapter;
    private EditText etSearch;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_services, container, false);

        etSearch = view.findViewById(R.id.etSearchMarket);
        rvServices = view.findViewById(R.id.rvMarketServices);

        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

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
        RetrofitClient.getApiService().getServices(null, query).enqueue(new Callback<ServiceListResponse>() {
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
                Log.e("ServicesFragment", "Error loading services", t);
            }
        });
    }
}
