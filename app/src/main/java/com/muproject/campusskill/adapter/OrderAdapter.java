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

// Order list adapter (Hinglish: Orders ki list dikhane wala adapter)
public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.OrderViewHolder> {

    private List<Order> orders;
    private String role; // "buyer" or "seller"
    private OnOrderActionListener listener;

    public interface OnOrderActionListener {
        void onAccept(Order order);
        void onComplete(Order order);
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

        // Show relevant person based on role (Hinglish: Role ke hisaab se naam dikhao)
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

        // Status badge styling (Hinglish: Status ke hisaab se rang badalo)
        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "pending";
        holder.tvStatus.setText(capitalize(status));

        // Accept Button Logic (Hinglish: Agar seller hai aur status pending hai, toh Accept button dikhao)
        if ("seller".equals(role) && "pending".equals(status)) {
            holder.btnAccept.setVisibility(View.VISIBLE);
            holder.btnAccept.setOnClickListener(v -> {
                if (listener != null) listener.onAccept(order);
            });
        } else {
            holder.btnAccept.setVisibility(View.GONE);
        }

        // Complete Button Logic (Hinglish: Agar buyer hai aur status accepted hai, toh Complete button dikhao)
        if ("buyer".equals(role) && "accepted".equals(status)) {
            holder.btnComplete.setVisibility(View.VISIBLE);
            holder.btnComplete.setOnClickListener(v -> {
                if (listener != null) listener.onComplete(order);
            });
        } else {
            holder.btnComplete.setVisibility(View.GONE);
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
            default: // pending
                holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
                holder.tvStatus.setTextColor(0xFFE65100);
                break;
        }

        // Date formatting (Hinglish: Date ko readable format mein dikhao)
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
        android.widget.Button btnAccept, btnComplete;

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
        }
    }
}
