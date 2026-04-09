package com.muproject.campusskill;

import android.os.Bundle;
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

// Market Discovery Fragment (Hinglish: Naya 'Services' page jahan browsing aur search ho sakti hai)
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

        rvServices.setLayoutManager(new LinearLayoutManager(requireContext()));
        adapter = new ServiceAdapter(new ArrayList<>());
        rvServices.setAdapter(adapter);

        // Fetch all services initially (Hinglish: Page khulte hi saare suggested services dikhao)
        loadServices(null);

        // Search logic
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                loadServices(etSearch.getText().toString().trim());
                return true;
            }
            return false;
        });

        return view;
    }

    private void loadServices(String query) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Loading services...");
        pd.show();

        RetrofitClient.getApiService().getServices(null, query).enqueue(new Callback<ServiceListResponse>() {
            @Override
            public void onResponse(Call<ServiceListResponse> call, Response<ServiceListResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    adapter.setServices(response.body().getData());
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
