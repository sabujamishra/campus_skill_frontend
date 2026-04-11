package com.muproject.campusskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.R;
import com.muproject.campusskill.model.Order;
import java.util.List;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private String role; // "buyer" or "seller"
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAccept(Order order);
        void onComplete(Order order);
        void onReview(Order order);
        void onChat(Order order);
    }

    public OrderAdapter(List<Order> orders, String role, OnOrderActionListener listener) {
        this.orders = orders;
        this.role = role;
        this.listener = listener;
    }

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Order order = orders.get(position);

        holder.tvOrderId.setText("Order #" + order.getId());
        holder.tvServiceTitle.setText(order.getServiceTitle() != null ? order.getServiceTitle() : "Service #" + order.getServiceId());
        holder.tvPrice.setText("₹" + (order.getAmount() != null ? order.getAmount() : "N/A"));

        if ("buyer".equals(role)) {
            String seller = order.getSellerName();
            holder.tvPerson.setText(seller != null && !seller.isEmpty() ? "Seller: " + seller : "");
            holder.tvPerson.setVisibility(seller != null && !seller.isEmpty() ? View.VISIBLE : View.GONE);
            
            holder.tvPerson.setOnClickListener(v -> {
                if (v.getContext() instanceof com.muproject.campusskill.MainActivity && order.getSellerId() > 0) {
                    ((com.muproject.campusskill.MainActivity) v.getContext())
                            .replaceFragment(com.muproject.campusskill.PublicProfileFragment.newInstance(order.getSellerId()));
                }
            });
        } else {
            String buyer = order.getBuyerName();
            holder.tvPerson.setText(buyer != null && !buyer.isEmpty() ? "Buyer: " + buyer : "");
            holder.tvPerson.setVisibility(buyer != null && !buyer.isEmpty() ? View.VISIBLE : View.GONE);

            holder.tvPerson.setOnClickListener(v -> {
                if (v.getContext() instanceof com.muproject.campusskill.MainActivity && order.getBuyerId() > 0) {
                    ((com.muproject.campusskill.MainActivity) v.getContext())
                            .replaceFragment(com.muproject.campusskill.PublicProfileFragment.newInstance(order.getBuyerId()));
                }
            });
        }

        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "pending";
        holder.tvStatus.setText(capitalize(status));

        if ("seller".equals(role) && "pending".equals(status)) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(order);
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
        }

        if ("buyer".equals(role) && "accepted".equals(status)) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnComplete.setOnClickListener(v -> {
                if (listener != null) listener.onComplete(order);
            });
        } else {
            holder.btnComplete.setVisibility(View.GONE);
        }

        if ("buyer".equals(role) && "completed".equals(status)) {
            holder.btnReview.setVisibility(View.VISIBLE);
            holder.btnReview.setOnClickListener(v -> {
                if (listener != null) listener.onReview(order);
            });
        } else {
            holder.btnReview.setVisibility(View.GONE);
        }

        switch (status) {
            case "completed":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_green);
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_green_dark));
                break;
            case "cancelled":
            case "rejected":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_red);
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
                break;
            case "in_progress":
            case "accepted":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_blue);
                holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
                holder.tvStatus.setText("In Progress");
                break;
            default:
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
                holder.tvStatus.setTextColor(0xFFE65100);
                break;
        }

        if (order.getCreatedAt() != null) {
            try {
                java.text.SimpleDateFormat inputFmt = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.getDefault());
                java.text.SimpleDateFormat outputFmt = new java.text.SimpleDateFormat("dd MMM yyyy, hh:mm a", java.util.Locale.getDefault());
                java.util.Date date = inputFmt.parse(order.getCreatedAt());
                holder.tvDate.setText("Placed on: " + outputFmt.format(date));
            } catch (Exception e) {
                holder.tvDate.setText("Placed on: " + order.getCreatedAt());
            }
        } else {
            holder.tvDate.setText("");
        }

        holder.btnChat.setText("Chat with " + ("buyer".equals(role) ? "Seller" : "Buyer"));
        holder.btnChat.setOnClickListener(v -> {
            if (listener != null) listener.onChat(order);
        });
    }

    @Override
    public int getItemCount() {
        return orders != null ? orders.size() : 0;
    }

    public void updateData(List<Order> newOrders) {
        this.orders = newOrders;
        notifyDataSetChanged();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "Pending";
        return s.substring(0, 1).toUpperCase() + s.substring(1).replace("_", " ");
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView tvOrderId, tvServiceTitle, tvStatus, tvPerson, tvPrice, tvDate;
        android.widget.Button btnAccept, btnComplete, btnReview, btnChat;

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvServiceTitle = itemView.findViewById(R.id.tvOrderServiceTitle);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPerson = itemView.findViewById(R.id.tvOrderPerson);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
            btnAccept = itemView.findViewById(R.id.btnAcceptOrder);
            btnComplete = itemView.findViewById(R.id.btnCompleteOrder);
            btnReview = itemView.findViewById(R.id.btnReviewOrder);
            btnChat = itemView.findViewById(R.id.btnChatOrder);
        }
    }
}
