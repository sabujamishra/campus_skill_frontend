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
import com.muproject.campusskill.network.SessionManager;
import com.muproject.campusskill.model.User;

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
            tvId.setText("#" + service.getId());
        }

        // Load Images
        if (service.getThumbnail() != null) {
            String url = service.getThumbnail().startsWith("http") ? service.getThumbnail() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getThumbnail();
            Glide.with(this).load(url).placeholder(R.drawable.service_placeholder).into(ivImage);
        } else {
            ivImage.setImageResource(R.drawable.service_placeholder);
        }

        if (service.getSellerProfileImage() != null && !service.getSellerProfileImage().isEmpty()) {
            String avatarUrl = service.getSellerProfileImage();
            String url;
            if (avatarUrl.startsWith("http")) {
                url = avatarUrl;
            } else {
                String baseUrl = "https://lightgrey-dogfish-642647.hostingersite.com/";
                if (avatarUrl.startsWith("/")) avatarUrl = avatarUrl.substring(1);
                url = baseUrl + avatarUrl;
            }
            Glide.with(this).load(url).circleCrop().placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).into(ivAvatar);
        } else {
            ivAvatar.setImageResource(R.drawable.ic_profile);
        }

        btnBack.setOnClickListener(v -> ((MainActivity)requireActivity()).goBack());
        
        // Owner Check (Hinglish: Check karo kya user khud hi seller hai)
        SessionManager session = new SessionManager(requireContext());
        int currentUserId = session.getUserId();
        
        if (currentUserId != -1) {
            boolean isOwner = com.muproject.campusskill.MainActivity.myServiceIds != null && 
                    com.muproject.campusskill.MainActivity.myServiceIds.contains(service.getId());
            
            if (isOwner) {
                // Hinglish: Agar owner hai toh booking band karo aur ribbon dikhao
                btnBook.setEnabled(false);
                btnBook.setText("This is your service");
                btnBook.setAlpha(0.6f);
                btnBook.setBackgroundTintList(android.content.res.ColorStateList.valueOf(android.graphics.Color.GRAY));
                
                View badge = view.findViewById(R.id.tvYourServiceBadge);
                if (badge != null) badge.setVisibility(View.VISIBLE);

                // Hinglish: "About Seller" hide nahi karna, session details se fill karna hai agar API blank hai
                if (service.getSellerName() == null || service.getSellerName().isEmpty()) {
                    tvSeller.setText(session.getUserName());
                    String sessionImg = session.getUserImage();
                    if (sessionImg != null) {
                        String url = sessionImg.startsWith("http") ? sessionImg : "https://lightgrey-dogfish-642647.hostingersite.com/" + sessionImg;
                        Glide.with(this).load(url).circleCrop().placeholder(R.drawable.ic_profile).into(ivAvatar);
                    }
                }
            }
        }
        
        btnBook.setOnClickListener(v -> handlePlaceOrder());
        
        view.findViewById(R.id.rowSellerInfo).setOnClickListener(v -> {
            if (service != null && getActivity() instanceof MainActivity) {
                // If is owner, maybe go to Profile? Or just Public Profile (which works fine even for current user)
                ((MainActivity) getActivity()).replaceFragment(PublicProfileFragment.newInstance(service.getSellerId()));
            }
        });

        return view;
    }

    private void handlePlaceOrder() {
        if (service == null) return;

        // Validation for ownership (Hinglish: Global ID Registry se final check)
        if (com.muproject.campusskill.MainActivity.myServiceIds != null && 
                com.muproject.campusskill.MainActivity.myServiceIds.contains(service.getId())) {
            android.widget.Toast.makeText(requireContext(), "You cannot book your own service!", android.widget.Toast.LENGTH_SHORT).show();
            return;
        }

        // Custom Confirmation Dialog (Hinglish: Naya modern confirm screen)
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_confirm_booking, null);
        
        ((TextView) dialogView.findViewById(R.id.tvConfirmTitle)).setText(service.getTitle());
        ((TextView) dialogView.findViewById(R.id.tvConfirmSeller)).setText("by " + service.getSellerName());
        ((TextView) dialogView.findViewById(R.id.tvConfirmPrice)).setText("₹" + service.getPrice());
        ((TextView) dialogView.findViewById(R.id.tvConfirmTime)).setText(service.getDeliveryTime() + " Days");

        ImageView iv = dialogView.findViewById(R.id.ivConfirmServiceImg);
        if (service.getThumbnail() != null) {
            String url = service.getThumbnail().startsWith("http") ? service.getThumbnail() : "https://lightgrey-dogfish-642647.hostingersite.com/" + service.getThumbnail();
            Glide.with(this).load(url).placeholder(R.drawable.service_placeholder).centerCrop().into(iv);
        }

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .setCancelable(true)
                .create();
        
        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btnFinalConfirm).setOnClickListener(v -> {
            dialog.dismiss();
            processOrderAPI();
        });

        dialogView.findViewById(R.id.btnCancelBooking).setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }

    private void processOrderAPI() {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Placing your order...");
        pd.setCancelable(false);
        pd.show();

        java.util.Map<String, Integer> body = new java.util.HashMap<>();
        body.put("service_id", service.getId());

        com.muproject.campusskill.network.RetrofitClient.getApiService().createOrder(body).enqueue(new retrofit2.Callback<com.muproject.campusskill.model.CommonResponse>() {
            @Override
            public void onResponse(retrofit2.Call<com.muproject.campusskill.model.CommonResponse> call, retrofit2.Response<com.muproject.campusskill.model.CommonResponse> response) {
                pd.dismiss();
                if (!isAdded()) return;
                
                if (response.isSuccessful() && response.body() != null) {
                    showSuccessDialog(response.body().getMessage());
                } else {
                    String error = "Failed to place order.";
                    try {
                        if (response.errorBody() != null) {
                            com.muproject.campusskill.model.CommonResponse err = new com.google.gson.Gson().fromJson(response.errorBody().charStream(), com.muproject.campusskill.model.CommonResponse.class);
                            if (err != null) error = err.getMessage();
                        }
                    } catch (Exception e) {}
                    android.widget.Toast.makeText(requireContext(), error, android.widget.Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(retrofit2.Call<com.muproject.campusskill.model.CommonResponse> call, Throwable t) {
                pd.dismiss();
                if (!isAdded()) return;
                android.widget.Toast.makeText(requireContext(), "Network Error", android.widget.Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showSuccessDialog(String message) {
        if (getContext() == null) return;

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_order_success, null);
        TextView tvMsg = dialogView.findViewById(R.id.tvSuccessMessage);
        if (message != null) tvMsg.setText(message);

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }

        dialogView.findViewById(R.id.btnViewOrders).setOnClickListener(v -> {
            dialog.dismiss();
            DashboardFragment.setTab(R.id.nav_orders);
            
            // Explicitly find nav and select it if possible
            if (getActivity() != null) {
                View navView = getActivity().findViewById(R.id.bottom_navigation);
                if (navView instanceof com.google.android.material.bottomnavigation.BottomNavigationView) {
                    ((com.google.android.material.bottomnavigation.BottomNavigationView) navView).setSelectedItemId(R.id.nav_orders);
                }
            }
            
            ((MainActivity)requireActivity()).goBack();
        });

        dialogView.findViewById(R.id.btnDone).setOnClickListener(v -> {
            dialog.dismiss();
            ((MainActivity)requireActivity()).goBack();
        });

        dialog.show();
    }
}
