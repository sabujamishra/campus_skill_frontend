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
import com.muproject.campusskill.utils.SessionManager;
import com.muproject.campusskill.data.model.User;

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
        TextView tvCategory = view.findViewById(R.id.tvDetailCategory);
        TextView tvPrice = view.findViewById(R.id.tvDetailPrice);
        TextView tvRating = view.findViewById(R.id.tvDetailRating);
        TextView tvTime = view.findViewById(R.id.tvDetailDeliverTime);
        TextView tvSeller = view.findViewById(R.id.tvDetailSellerName);
        TextView tvDesc = view.findViewById(R.id.tvDetailDesc);
        Button btnBook = view.findViewById(R.id.btnBookNow);
        View btnBack = view.findViewById(R.id.btnBackDetail);

        // Set Data
        tvTitle.setText(service.getTitle());
        tvCategory.setText(service.getCategory() != null ? service.getCategory() : "General");
        tvPrice.setText("₹" + service.getPrice());
        tvRating.setText("⭐ " + service.getAverageRating());
        tvTime.setText(service.getDeliveryTime() + " days delivery");
        tvSeller.setText(service.getSellerName());
        tvDesc.setText(service.getDescription());
        
        TextView tvId = view.findViewById(R.id.tvDetailId);
        if (tvId != null) {
            tvId.setText("Service Description #" + service.getId());
        }

        // Load Images
        if (service.getThumbnail() != null) {
            String url = service.getThumbnail().startsWith("http") ? service.getThumbnail() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getThumbnail();
            Glide.with(this).load(url).placeholder(R.drawable.service_placeholder).into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.service_placeholder);
        }

        if (service.getSellerProfileImage() != null) {
            String url = service.getSellerProfileImage().startsWith("http") ? service.getSellerProfileImage() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getSellerProfileImage();
            Glide.with(this).load(url).circleCrop().placeholder(R.drawable.ic_profile).into(ivAvatar);
        }

        btnBack.setOnClickListener(v -> ((MainActivity)requireActivity()).goBack());
        
        view.findViewById(R.id.rowSellerInfo).setOnClickListener(v -> {
            if (service != null && getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).replaceFragment(PublicProfileFragment.newInstance(service.getSellerId()));
            }
        });
        
        // Owner Check (Hinglish: Check karo kya user khud hi seller hai)
        SessionManager session = new SessionManager(requireContext());
        User currentUser = session.getUser();
        if (currentUser != null) {
            boolean isOwner = (currentUser.getId() == service.getSellerId()) || 
                             (service.getSellerName() != null && service.getSellerName().equals(currentUser.getName()));
            
            if (isOwner) {
                btnBook.setEnabled(false);
                btnBook.setText("This is your service");
                btnBook.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
                view.findViewById(R.id.tvYourServiceBadge).setVisibility(View.VISIBLE);
            }
        }
        
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
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_success, null);
        TextView tvMsg = dialogView.findViewById(R.id.tvSuccessMessage);
        if (message != null) tvMsg.setText(message);

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setCancelable(false)
                .create();

        dialogView.findViewById(R.id.btnViewOrders).setOnClickListener(v -> {
            dialog.dismiss();
            // In future: Switch to Dashboard Orders tab
            ((MainActivity)requireActivity()).goBack();
        });

        dialogView.findViewById(R.id.btnDone).setOnClickListener(v -> {
            dialog.dismiss();
            ((MainActivity)requireActivity()).goBack();
        });

        dialog.show();
    }
}
