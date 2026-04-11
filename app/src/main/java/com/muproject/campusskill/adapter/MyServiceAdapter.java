package com.muproject.campusskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.muproject.campusskill.R;
import com.muproject.campusskill.model.Service;
import java.util.List;

// Adapter specifically for managing own services (Hinglish: User ki apni banayi services ke liye adapter)
public class MyServiceAdapter extends RecyclerView.Adapter<MyServiceAdapter.ViewHolder> {

    private List<Service> services;
    private OnServiceActionListener listener;

    public interface OnServiceActionListener {
        void onEdit(Service service);
        void onDelete(Service service);
        void onClick(Service service);
    }

    public MyServiceAdapter(List<Service> services, OnServiceActionListener listener) {
        this.services = services;
        this.listener = listener;
    }

    public void setServices(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_my_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);

        holder.tvTitle.setText(service.getTitle());
        holder.tvPrice.setText("₹" + service.getPrice());
        holder.tvCategory.setText(service.getCategory() != null ? service.getCategory() : "Misc");
        holder.tvRating.setText("⭐ " + service.getAverageRating());

        // Status logic (Hinglish: Service ka current status dikhao)
        String status = service.getStatus() != null ? service.getStatus() : "active";
        holder.tvStatus.setText(status.toUpperCase());
        if (status.equalsIgnoreCase("active")) {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_blue);
            holder.tvStatus.setTextColor(holder.itemView.getContext().getColor(R.color.button_blue));
        } else {
            holder.tvStatus.setBackgroundResource(R.drawable.bg_tag_orange);
            holder.tvStatus.setTextColor(0xFFE65100);
        }

        // Image loading (Path cleaning included)
        String thumbUrl = service.getThumbnail();
        if (thumbUrl != null && !thumbUrl.isEmpty()) {
            String url;
            if (thumbUrl.startsWith("http")) {
                url = thumbUrl;
            } else {
                String cleanPath = thumbUrl.startsWith("/") ? thumbUrl.substring(1) : thumbUrl;
                url = "https://lightgrey-dogfish-642647.hostingersite.com/" + cleanPath;
            }
            Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.service_placeholder)
                    .centerCrop()
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.service_placeholder);
        }

        // Action Listeners
        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(service);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(service);
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onClick(service);
        });
    }

    @Override
    public int getItemCount() {
        return services != null ? services.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        View btnEdit, btnDelete;
        TextView tvTitle, tvPrice, tvCategory, tvRating, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivMyServiceImage);
            btnEdit = itemView.findViewById(R.id.btnEditService);
            btnDelete = itemView.findViewById(R.id.btnDeleteService);
            tvTitle = itemView.findViewById(R.id.tvMyServiceTitle);
            tvPrice = itemView.findViewById(R.id.tvMyServicePrice);
            tvCategory = itemView.findViewById(R.id.tvMyServiceCategory);
            tvRating = itemView.findViewById(R.id.tvMyServiceRating);
            tvStatus = itemView.findViewById(R.id.tvMyServiceStatus);
        }
    }
}
