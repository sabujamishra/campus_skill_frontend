package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.Service;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Service ki poori detail dikhane wala aur order place karne wala screen (Hinglish: Order Module integration)
public class ServiceDetailsFragment extends Fragment {

    private Service service;

    public static ServiceDetailsFragment newInstance(Service service) {
        ServiceDetailsFragment fragment = new ServiceDetailsFragment();
        Bundle args = new Bundle();
        args.putSerializable("service", service);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            service = (Service) getArguments().getSerializable("service");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service_details, container, false);

        if (service == null) return view;

        // Initialize UI
        ImageView ivImage = view.findViewById(R.id.ivDetailServiceImage);
        ImageView ivAvatar = view.findViewById(R.id.ivDetailSellerAvatar);
        TextView tvTitle = view.findViewById(R.id.tvDetailTitle);
        TextView tvPrice = view.findViewById(R.id.tvDetailPrice);
        TextView tvRating = view.findViewById(R.id.tvDetailRating);
        TextView tvTime = view.findViewById(R.id.tvDetailDeliverTime);
        TextView tvSeller = view.findViewById(R.id.tvDetailSellerName);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);
        Button btnBook = view.findViewById(R.id.btnBookNow);
        View btnBack = view.findViewById(R.id.btnBackDetail);

        // Set Data
        tvTitle.setText(service.getTitle());
        tvPrice.setText("₹" + service.getPrice());
        tvRating.setText("⭐ " + service.getAverageRating());
        tvTime.setText(service.getDeliveryTime() + " days delivery");
        tvSeller.setText(service.getSellerName());
        tvDesc.setText(service.getDescription());

        // Load Images
        if (service.getThumbnail() != null) {
            String url = service.getThumbnail().startsWith("http") ? service.getThumbnail() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getThumbnail();
            Glide.with(this).load(url).placeholder(R.drawable.rounded_placeholder).into(ivImage);
        }

        if (service.getSellerProfileImage() != null) {
            String url = service.getSellerProfileImage().startsWith("http") ? service.getSellerProfileImage() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getSellerProfileImage();
            Glide.with(this).load(url).circleCrop().placeholder(R.drawable.ic_profile).into(ivAvatar);
        }

        btnBack.setOnClickListener(v -> ((MainActivity)requireActivity()).goBack());
        
        btnBook.setOnClickListener(v -> handlePlaceOrder());

        return view;
    }

    private void handlePlaceOrder() {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Placing your order...");
        pd.setCancelable(false);
        pd.show();

        Map<String, Integer> body = new HashMap<>();
        body.put("service_id", service.getId());

        RetrofitClient.getApiService().createOrder(body).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                pd.dismiss();
                if (response.isSuccessful() && response.body() != null) {
                    // Success!
                    showSuccessDialog(response.body().getMessage());
                } else {
                    String error = "Cannot order your own service or technical error.";
                    try {
                        if (response.errorBody() != null) error = response.errorBody().string();
                    } catch (Exception e) {}
                    Toast.makeText(requireContext(), "Failed: " + error, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                pd.dismiss();
                Toast.makeText(requireContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog(String message) {
        new androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Order Successful! ✅")
                .setMessage(message != null ? message : "Your order has been placed. You can track it in the Orders section.")
                .setPositiveButton("View Orders", (dialog, which) -> {
                    // Switch to Orders Tab or just go back for now
                    ((MainActivity)requireActivity()).goBack();
                })
                .setNegativeButton("Close", (dialog, which) -> ((MainActivity)requireActivity()).goBack())
                .show();
    }
}
