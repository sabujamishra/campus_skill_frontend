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
import com.muproject.campusskill.adapter.MyServiceAdapter;
import com.muproject.campusskill.model.Service;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// User's own services management (Hinglish: Background loading safety aur lifecycle fixes)
public class MyServicesFragment extends Fragment {

    private RecyclerView rvServices;
    private MyServiceAdapter adapter;

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
        adapter = new MyServiceAdapter(new ArrayList<>(), new MyServiceAdapter.OnServiceActionListener() {
            @Override
            public void onEdit(Service service) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(CreateServiceFragment.newInstance(service));
                }
            }

            @Override
            public void onDelete(Service service) {
                showDeleteConfirmation(service);
            }

            @Override
            public void onClick(Service service) {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(ServiceDetailsFragment.newInstance(service));
                }
            }
        });
        rvServices.setAdapter(adapter);

        loadMyServices();

        return view;
    }

    private void showDeleteConfirmation(Service service) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Delete Service?")
                .setMessage("Are you sure you want to delete '" + service.getTitle() + "'? This action cannot be undone.")
                .setPositiveButton("Delete", (dialog, which) -> handleDelete(service))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void handleDelete(Service service) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Deleting...");
        pd.show();

        RetrofitClient.getApiService().deleteService(service.getId()).enqueue(new Callback<com.muproject.campusskill.model.CommonResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.CommonResponse> call, Response<com.muproject.campusskill.model.CommonResponse> response) {
                pd.dismiss();
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Service deleted", Toast.LENGTH_SHORT).show();
                    loadMyServices();
                } else {
                    Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.muproject.campusskill.model.CommonResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadMyServices() {
        // Lifecycle Tip: No blocking dialog for non-action data loads to avoid navigation crashes
        RetrofitClient.getApiService().getMyServices().enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                if (!isAdded() || getContext() == null) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<Service> services = response.body().getData();
                    // Inject current user ID since API for my services might skip it
                    // Hinglish: "My Services" API ID nahi bhejta toh manual set kar rahe hain ownership check ke liye
                    int myId = new com.muproject.campusskill.network.SessionManager(requireContext()).getUserId();
                    if (services != null) {
                        for (Service s : services) {
                            s.setSellerId(myId);
                        }
                    }
                    adapter.setServices(services);
                    if (services != null && services.isEmpty()) {
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
