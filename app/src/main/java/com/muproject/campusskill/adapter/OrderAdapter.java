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

    public OrderAdapter(List<Order> orders, String role) {
        this.orders = orders;
        this.role = role;
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
        } else {
            String buyer = order.getBuyerName();
            holder.tvPerson.setText(buyer != null && !buyer.isEmpty() ? "Buyer: " + buyer : "");
            holder.tvPerson.setVisibility(buyer != null && !buyer.isEmpty() ? View.VISIBLE : View.GONE);
        }

        // Status badge styling (Hinglish: Status ke hisaab se rang badalo)
        String status = order.getStatus() != null ? order.getStatus().toLowerCase() : "pending";
        holder.tvStatus.setText(capitalize(status));

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

        OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvOrderId = itemView.findViewById(R.id.tvOrderId);
            tvServiceTitle = itemView.findViewById(R.id.tvOrderServiceTitle);
            tvStatus = itemView.findViewById(R.id.tvOrderStatus);
            tvPerson = itemView.findViewById(R.id.tvOrderPerson);
            tvPrice = itemView.findViewById(R.id.tvOrderPrice);
            tvDate = itemView.findViewById(R.id.tvOrderDate);
        }
    }
}
