package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.adapter.ServiceAdapter;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Marketplace Home screen (Hinglish: Services ki list dikhane wala main page)
public class HomeFragment extends Fragment {

    private RecyclerView rvServices;
    private ServiceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Setup RecyclerView (Hinglish: List ko grid format mein set kar rahe hain)
        rvServices = view.findViewById(R.id.rvServices);
        rvServices.setLayoutManager(new GridLayoutManager(requireContext(), 2)); // 2 columns for a modern look
        
        adapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

        // Fetch services (Hinglish: Server se saari services mangwa rahe hain)
        loadServices();

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

    private void loadServices() {
        android.app.ProgressDialog progressDialog = new android.app.ProgressDialog(requireContext());
        progressDialog.setMessage("Fetching services...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // API call with no filters initially (Hinglish: Bina kisi filter ke pehle saare services load karo)
        RetrofitClient.getApiService().getServices(null, null).enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                progressDialog.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    if ("success".equals(response.body().getStatus())) {
                        adapter.setServices(response.body().getData());
                    } else {
                        Toast.makeText(requireContext(), response.body().getMessage(), Toast.LENGTH_SHORT).show();
                    }
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
}
