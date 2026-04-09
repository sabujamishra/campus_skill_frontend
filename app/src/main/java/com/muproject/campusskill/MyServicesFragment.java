package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

// User's own services management (Hinglish: Background loading safety aur lifecycle fixes)
public class MyServicesFragment extends Fragment {

    private RecyclerView rvServices;
    private ServiceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_services, container, false);

        rvServices = view.findViewById(R.id.rvMyServices);
        
        android.widget.ImageView btnBack = view.findViewById(R.id.btnBackMyServices);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).goBack();
                }
            });
        }

        rvServices.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

        loadMyServices();

        return view;
    }

    private void loadMyServices() {
        // Lifecycle Tip: No blocking dialog for non-action data loads to avoid navigation crashes
        RetrofitClient.getApiService().getMyServices().enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (!isAdded() || getContext() == null) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setServices(response.body().getData());
                    if (response.body().getData().isEmpty()) {
                        Toast.makeText(getContext(), "No services found!", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                if (!isAdded()) return;
                Log.e("MyServices", "Failed to load", t);
            }
        });
    }
}
