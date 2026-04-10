package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.muproject.campusskill.adapter.OrderAdapter;
import com.muproject.campusskill.model.Order;
import com.muproject.campusskill.model.OrderListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.ArrayList;
import java.util.List;

// Orders Fragment (Hinglish: Buyer/Seller ke orders dikhane wala fragment)
public class OrdersFragment extends Fragment {

    private RecyclerView rvOrders;
    private ProgressBar progressBar;
    private View layoutEmpty;
    private OrderAdapter adapter;
    private String currentRole = "buyer";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_orders, container, false);

        rvOrders = view.findViewById(R.id.rvOrders);
        progressBar = view.findViewById(R.id.progressOrders);
        layoutEmpty = view.findViewById(R.id.layoutEmptyOrders);

        rvOrders.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new OrderAdapter(new ArrayList<Order>(), currentRole, new OrderAdapter.OnOrderActionListener() {
            @Override
            public void onAccept(Order order) {
                handleAcceptOrder(order);
            }

            @Override
            public void onComplete(Order order) {
                handleCompleteOrder(order);
            }
        });
        rvOrders.setAdapter(adapter);

        TabLayout tabLayout = view.findViewById(R.id.tabOrderRole);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                currentRole = tab.getPosition() == 0 ? "buyer" : "seller";
                loadOrders();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        // Load default tab (Hinglish: Pehle buyer orders load karo)
        loadOrders();

        return view;
    }

    private void loadOrders() {
        progressBar.setVisibility(View.VISIBLE);
        rvOrders.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        RetrofitClient.getApiService().getOrders(currentRole).enqueue(new Callback<OrderListResponse>() {
            @Override
            public void onResponse(Call<OrderListResponse> call, Response<OrderListResponse> response) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    List<Order> orders = response.body().getData();
                    if (orders.isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvOrders.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvOrders.setVisibility(View.VISIBLE);
                        adapter = new OrderAdapter(orders, currentRole, new OrderAdapter.OnOrderActionListener() {
                            @Override
                            public void onAccept(Order order) {
                                handleAcceptOrder(order);
                            }

                            @Override
                            public void onComplete(Order order) {
                                handleCompleteOrder(order);
                            }

                            @Override
                            public void onReview(Order order) {
                                handleReviewOrder(order);
                            }
                        });
                        rvOrders.setAdapter(adapter);
                    }
                } else {
                    layoutEmpty.setVisibility(View.VISIBLE);
                    Log.e("OrdersFragment", "Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<OrderListResponse> call, Throwable t) {
                if (!isAdded()) return;
                progressBar.setVisibility(View.GONE);
                layoutEmpty.setVisibility(View.VISIBLE);
                Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleAcceptOrder(Order order) {
        // Show progress loader
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Accepting order...");
        pd.setCancelable(false);
        pd.show();

        RetrofitClient.getApiService().acceptOrder(order.getId()).enqueue(new Callback<com.muproject.campusskill.model.CommonResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.CommonResponse> call, Response<com.muproject.campusskill.model.CommonResponse> response) {
                pd.dismiss();
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(getContext(), "Order Accepted!", Toast.LENGTH_SHORT).show();
                    loadOrders(); // List refresh karo
                }
            }

            @Override
            public void onFailure(Call<com.muproject.campusskill.model.CommonResponse> call, Throwable t) {
                pd.dismiss();
                if (isAdded()) Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleCompleteOrder(Order order) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Completing order...");
        pd.setCancelable(false);
        pd.show();

        RetrofitClient.getApiService().completeOrder(order.getId()).enqueue(new Callback<com.muproject.campusskill.model.CommonResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.CommonResponse> call, Response<com.muproject.campusskill.model.CommonResponse> response) {
                pd.dismiss();
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(getContext(), "Order Completed! Enjoy your service.", Toast.LENGTH_LONG).show();
                    loadOrders();
                } else {
                    Toast.makeText(getContext(), "Failed to complete order", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.muproject.campusskill.model.CommonResponse> call, Throwable t) {
                pd.dismiss();
                if (isAdded()) Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void handleReviewOrder(Order order) {
        View dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_submit_review, null);
        android.widget.RatingBar rb = dialogView.findViewById(R.id.ratingBarReview);
        com.google.android.material.textfield.TextInputEditText etComment = dialogView.findViewById(R.id.etReviewComment);

        new com.google.android.material.dialog.MaterialAlertDialogBuilder(requireContext())
                .setTitle("Submit Review")
                .setView(dialogView)
                .setPositiveButton("Submit", (dialog, which) -> {
                    int rating = (int) rb.getRating();
                    if (rating == 0) {
                        Toast.makeText(getContext(), "Please select at least 1 star!", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String comment = etComment.getText().toString().trim();
                    submitReviewToServer(order.getId(), rating, comment);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitReviewToServer(int orderId, int rating, String comment) {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(requireContext());
        pd.setMessage("Submitting review...");
        pd.setCancelable(false);
        pd.show();

        java.util.Map<String, Object> body = new java.util.HashMap<>();
        body.put("order_id", orderId);
        body.put("rating", rating);
        body.put("comment", comment);

        RetrofitClient.getApiService().submitReview(body).enqueue(new Callback<com.muproject.campusskill.model.CommonResponse>() {
            @Override
            public void onResponse(Call<com.muproject.campusskill.model.CommonResponse> call, Response<com.muproject.campusskill.model.CommonResponse> response) {
                pd.dismiss();
                if (!isAdded()) return;

                if (response.isSuccessful() && response.body() != null && "success".equals(response.body().getStatus())) {
                    Toast.makeText(getContext(), "Review submitted successfully!", Toast.LENGTH_SHORT).show();
                    loadOrders();
                } else {
                    Toast.makeText(getContext(), "Failed to submit review", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<com.muproject.campusskill.model.CommonResponse> call, Throwable t) {
                pd.dismiss();
                if (isAdded()) Toast.makeText(getContext(), "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
