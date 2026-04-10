package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.bumptech.glide.Glide;
import com.muproject.campusskill.model.User;
import com.muproject.campusskill.model.ProfileResponse;
import com.muproject.campusskill.network.RetrofitClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PublicProfileFragment extends Fragment {

    private int userId;
    private ImageView ivProfile;
    private TextView tvName, tvDept, tvOrders, tvRating, tvResponse, tvMemberSince;
    private androidx.recyclerview.widget.RecyclerView rvServices;
    private com.muproject.campusskill.adapter.ServiceAdapter adapter;

    public static PublicProfileFragment newInstance(int userId) {
        PublicProfileFragment fragment = new PublicProfileFragment();
        Bundle args = new Bundle();
        args.putInt("user_id", userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            userId = getArguments().getInt("user_id");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_public_profile, container, false);

        ivProfile = view.findViewById(R.id.ivPublicProfileImage);
        tvName = view.findViewById(R.id.tvPublicName);
        tvDept = view.findViewById(R.id.tvPublicDept);
        tvOrders = view.findViewById(R.id.tvPublicOrders);
        tvRating = view.findViewById(R.id.tvPublicRating);
        tvResponse = view.findViewById(R.id.tvPublicResponse);
        tvMemberSince = view.findViewById(R.id.tvPublicMemberSince);

        view.findViewById(R.id.btnBackProfile).setOnClickListener(v -> {
            if (getActivity() instanceof MainActivity) {
                ((MainActivity) getActivity()).goBack();
            }
        });

        loadPublicProfile();

        rvServices = view.findViewById(R.id.rvPublicServices);
        rvServices.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));
        // Get logged in user id for "YOUR SERVICE" badge logic in adapter
        int currentUserId = new com.muproject.campusskill.network.SessionManager(requireContext()).getUserId();
        adapter = new com.muproject.campusskill.adapter.ServiceAdapter(new ArrayList<>(), currentUserId);
        rvServices.setAdapter(adapter);

        loadSellerServices();

        View btnContact = view.findViewById(R.id.btnContactSeller);
        if (userId == currentUserId) {
            btnContact.setVisibility(View.GONE);
        }

        btnContact.setOnClickListener(v -> {
            // Find if there's an active order between current user and this seller
            // Hinglish: Hum orders check kar rahe hain taaki participant access mil sake
            RetrofitClient.getApiService().getOrders("buyer").enqueue(new Callback<com.muproject.campusskill.model.OrderListResponse>() {
                @Override
                public void onResponse(Call<com.muproject.campusskill.model.OrderListResponse> call, Response<com.muproject.campusskill.model.OrderListResponse> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null) {
                        java.util.List<com.muproject.campusskill.model.Order> sellerOrders = new ArrayList<>();
                        java.util.List<com.muproject.campusskill.model.Order> allOrders = response.body().getData();
                        if (allOrders != null) {
                            for (com.muproject.campusskill.model.Order o : allOrders) {
                                if (o.getSellerId() == userId) {
                                    sellerOrders.add(o);
                                }
                            }
                        }

                        if (sellerOrders.size() == 1) {
                            com.muproject.campusskill.model.Order match = sellerOrders.get(0);
                            ((MainActivity) getActivity()).replaceFragment(ChatFragment.newInstance(match.getId(), match.getStatus()));
                        } else if (sellerOrders.size() > 1) {
                            showOrderSelectionDialog(sellerOrders);
                        } else {
                            Toast.makeText(getContext(), "You can only chat after booking a service!", Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<com.muproject.campusskill.model.OrderListResponse> call, Throwable t) {
                    if (isAdded()) Toast.makeText(getContext(), "Chat unavailable", Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }

    private void showOrderSelectionDialog(java.util.List<com.muproject.campusskill.model.Order> orders) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_select_order, null);
        androidx.recyclerview.widget.RecyclerView rv = dialogView.findViewById(R.id.rvSelectOrder);
        rv.setLayoutManager(new androidx.recyclerview.widget.LinearLayoutManager(getContext()));

        androidx.appcompat.app.AlertDialog dialog = new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext(), R.style.CustomDialogTheme)
                .setView(dialogView)
                .create();

        rv.setAdapter(new androidx.recyclerview.widget.RecyclerView.Adapter<SimpleOrderViewHolder>() {
            @NonNull
            @Override
            public SimpleOrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_dialog_order, parent, false);
                return new SimpleOrderViewHolder(v);
            }

            @Override
            public void onBindViewHolder(@NonNull SimpleOrderViewHolder holder, int position) {
                com.muproject.campusskill.model.Order o = orders.get(position);
                holder.tvTitle.setText(o.getServiceTitle());
                holder.tvId.setText("Order #" + o.getId());
                holder.itemView.setOnClickListener(v -> {
                    dialog.dismiss();
                    ((MainActivity) getActivity()).replaceFragment(ChatFragment.newInstance(o.getId(), o.getStatus()));
                });
            }

            @Override
            public int getItemCount() { return orders.size(); }
        });

        dialogView.findViewById(R.id.btnCancelSelect).setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    private static class SimpleOrderViewHolder extends androidx.recyclerview.widget.RecyclerView.ViewHolder {
        TextView tvTitle, tvId;
        SimpleOrderViewHolder(View v) {
            super(v);
            tvTitle = v.findViewById(R.id.tvSelectOrderTitle);
            tvId = v.findViewById(R.id.tvSelectOrderId);
        }
    }

    private void loadPublicProfile() {
        if (userId <= 0) {
            if (getActivity() instanceof MainActivity) ((MainActivity) getActivity()).goBack();
            return;
        }

        RetrofitClient.getApiService().getPublicProfile(userId).enqueue(new Callback<ProfileResponse>() {
            @Override
            public void onResponse(Call<ProfileResponse> call, Response<ProfileResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    updateUI(response.body().getData());
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "Could not load profile", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ProfileResponse> call, Throwable t) {
                if (isAdded()) Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateUI(User user) {
        if (user == null) return;

        tvName.setText(user.getName());
        tvDept.setText(user.getDepartment());
        tvOrders.setText(String.valueOf(user.getTotalCompletedOrders()));
        tvRating.setText(String.format("%.1f", user.getAverageRating()));
        tvResponse.setText(user.getResponseRate() + "%");

        if (user.getCreatedAt() != null) {
            try {
                java.text.SimpleDateFormat inFmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                java.text.SimpleDateFormat outFmt = new java.text.SimpleDateFormat("MMM yyyy", java.util.Locale.getDefault());
                tvMemberSince.setText("Member since " + outFmt.format(inFmt.parse(user.getCreatedAt())));
            } catch (Exception e) {
                tvMemberSince.setText("Member since " + user.getCreatedAt().split(" ")[0]);
            }
        }

        // Profile image (Hinglish: Photo load karo)
        String imgPath = user.getProfileImage();
        if (imgPath != null && !imgPath.isEmpty()) {
            String url = imgPath.startsWith("http") ? imgPath : "https://lightgrey-dogfish-642647.hostingersite.com/" + (imgPath.startsWith("/") ? imgPath.substring(1) : imgPath);
            Glide.with(this).load(url).placeholder(R.drawable.ic_profile).error(R.drawable.ic_profile).circleCrop().into(ivProfile);
        } else {
            ivProfile.setImageResource(R.drawable.ic_profile);
        }
    }

    private void loadSellerServices() {
        // Fetch all services and filter for this specific seller
        // Hinglish: Saare services mangwa kar is seller ke liye filter kar rahe hain
        RetrofitClient.getApiService().getServices(null, null).enqueue(new Callback<com.muproject.campusskill.model.ServiceListResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.ServiceListResponse> call, Response<com.muproject.campusskill.model.ServiceListResponse> response) {
                if (!isAdded() || getContext() == null) return;
                if (response.isSuccessful() && response.body() != null) {
                    java.util.List<com.muproject.campusskill.model.Service> allServices = response.body().getData();
                    java.util.List<com.muproject.campusskill.model.Service> filtered = new ArrayList<>();
                    if (allServices != null) {
                        for (com.muproject.campusskill.model.Service s : allServices) {
                            if (s.getSellerId() == userId) {
                                filtered.add(s);
                            }
                        }
                    }
                    adapter.setServices(filtered);
                }
            }

            @Override
            public void onFailure(Call<com.muproject.campusskill.model.ServiceListResponse> call, Throwable t) {
                if (isAdded()) Log.e("PublicProfile", "Error loading services", t);
            }
        });
    }
}
