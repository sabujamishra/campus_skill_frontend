package com.muproject.campusskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.R;
import com.muproject.campusskill.model.Service;
import java.util.List;

// Services list ke liye adapter (Hinglish: Services ke cards dikhane wala manager)
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<Service> services;

    public ServiceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Service service = services.get(position);
        holder.tvTitle.setText(service.getTitle());
        holder.tvSeller.setText(service.getSellerName());
        holder.tvPrice.setText("₹" + (int)service.getPrice());
        holder.tvRating.setText("⭐ " + service.getRating());
        // For now using placeholder
        holder.ivImage.setImageResource(R.drawable.rounded_placeholder);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle, tvSeller, tvPrice, tvRating;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivServiceImage);
            tvTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvSeller = itemView.findViewById(R.id.tvSellerName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
        }
    }
}
