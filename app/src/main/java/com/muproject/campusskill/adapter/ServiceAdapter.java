package com.muproject.campusskill.adapter;

import android.util.Log;
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

// Services list ke liye adapter (Hinglish: API se aane wale services ke cards dikhane wala manager)
public class ServiceAdapter extends RecyclerView.Adapter<ServiceAdapter.ViewHolder> {

    private List<Service> services;
    private int currentUserId;

    public ServiceAdapter(List<Service> services, int currentUserId) {
        this.services = services;
        this.currentUserId = currentUserId;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void setCurrentUserId(int id) {
        this.currentUserId = id;
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
        
        holder.tvTitle.setText(service.getTitle() != null ? service.getTitle() : "No Title");
        holder.tvSeller.setText(service.getSellerName() != null ? service.getSellerName() : "Unknown");
        holder.tvPrice.setText("₹" + service.getPrice());
        holder.tvRating.setText("⭐ " + service.getAverageRating());

        // YOUR SERVICE Detection (Hinglish: Global ID list se ownership match kar rahe hain optimized logic)
        boolean isOwner = com.muproject.campusskill.MainActivity.myServiceIds != null && 
                com.muproject.campusskill.MainActivity.myServiceIds.contains(service.getId());

        if (isOwner) {
            holder.tvYourService.setVisibility(View.VISIBLE);
        } else {
            holder.tvYourService.setVisibility(View.GONE);
        }
        
        if (holder.tvCategory != null) {
            holder.tvCategory.setText(service.getCategory() != null ? service.getCategory() : "Misc");
        }
        
        // Image logic (Thumbnail) (Hinglish: Service ki photo load ho rahi hai)
        String thumbUrl = service.getThumbnail();
        if (thumbUrl != null && !thumbUrl.isEmpty()) {
            String url = thumbUrl.startsWith("http") ? thumbUrl : "https://lightgrey-dogfish-642647.hostingersite.com/" + thumbUrl;
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.service_placeholder)
                    .into(holder.ivImage);
        } else {
            holder.ivImage.setImageResource(R.drawable.service_placeholder);
        }
        
        // Seller avatar logic (Hinglish: Seller ki photo load ho rahi hai)
        String avatarUrl = service.getSellerProfileImage();
        if (avatarUrl != null && !avatarUrl.isEmpty()) {
            String url;
            if (avatarUrl.startsWith("http")) {
                url = avatarUrl;
            } else {
                if (avatarUrl.startsWith("/")) avatarUrl = avatarUrl.substring(1);
                url = "https://lightgrey-dogfish-642647.hostingersite.com/" + avatarUrl;
            }
            
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_profile)
                    .error(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.ivSellerAvatar);
        } else {
            holder.ivSellerAvatar.setImageResource(R.drawable.ic_profile);
        }

        // Logic to open Service Details (Hinglish: Card click par detail fragment load karo)
        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof com.muproject.campusskill.MainActivity) {
                ((com.muproject.campusskill.MainActivity) v.getContext())
                        .replaceFragment(com.muproject.campusskill.ServiceDetailsFragment.newInstance(service));
            }
        });
    }

    @Override
    public int getItemCount() {
        return services != null ? services.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImage, ivSellerAvatar;
        TextView tvTitle, tvSeller, tvPrice, tvRating, tvCategory, tvYourService;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImage = itemView.findViewById(R.id.ivServiceImage);
            ivSellerAvatar = itemView.findViewById(R.id.ivSellerAvatar);
            tvTitle = itemView.findViewById(R.id.tvServiceTitle);
            tvSeller = itemView.findViewById(R.id.tvSellerName);
            tvPrice = itemView.findViewById(R.id.tvPrice);
            tvRating = itemView.findViewById(R.id.tvRating);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvYourService = itemView.findViewById(R.id.tvYourService);
        }
    }
}
