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
import com.muproject.campusskill.adapter.ServiceAdapter;
import com.muproject.campusskill.model.ServiceListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// User's own services management (Hinglish: Yahan sirf logged-in user ki posts dikhengi)
public class MyServicesFragment extends Fragment {

    private RecyclerView rvServices;
    private ServiceAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_services, container, false);

        rvServices = view.findViewById(R.id.rvMyServices);
        view.findViewById(R.id.btnBackMyServices).setOnClickListener(v -> getParentFragmentManager().popBackStack());

        rvServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

        loadMyServices();

        return view;
    }

    private void loadMyServices() {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Fetching your creations...");
        pd.show();

        RetrofitClient.getApiService().getMyServices().enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setServices(response.body().getData());
                    if (response.body().getData().isEmpty()) {
                        Toast.makeText(requireContext(), "You haven't posted any services yet!", Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ServiceListResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(requireContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
