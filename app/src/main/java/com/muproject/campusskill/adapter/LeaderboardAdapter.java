package com.muproject.campusskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.R;
import com.muproject.campusskill.model.LeaderboardItem;
import java.util.List;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    private List<LeaderboardItem> items;
    private String type = "earners"; // earners, rated, active

    public LeaderboardAdapter(List<LeaderboardItem> items) {
        this.items = items;
    }

    public void setItems(List<LeaderboardItem> items, String type) {
        this.items = items;
        this.type = type;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leaderboard, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LeaderboardItem item = items.get(position);
        holder.tvRank.setText(String.valueOf(position + 1));
        holder.tvName.setText(item.getName() != null ? item.getName() : "Anonymous");
        holder.tvExtra.setText(item.getExtraLabel() != null ? item.getExtraLabel() : "Campus Elite");

        String value = item.getValue();
        if (value == null || value.equals("null")) value = "0";

        if (type.equals("earners")) {
            holder.tvValue.setText("₹" + value);
            holder.tvValue.setBackgroundResource(R.drawable.bg_price_pill);
        } else if (type.equals("rated")) {
            holder.tvValue.setText("⭐ " + value);
            holder.tvValue.setBackgroundResource(R.drawable.bg_rating_pill);
        } else {
            holder.tvValue.setText(value + " pts");
            holder.tvValue.setBackgroundResource(R.drawable.bg_tag_rounded);
        }

        String img = item.getProfileImage();
        if (img != null && !img.isEmpty()) {
            String url = img.startsWith("http") ? img : "https://lightgrey-dogfish-642647.hostingersite.com/" + img;
            com.bumptech.glide.Glide.with(holder.itemView.getContext())
                    .load(url)
                    .placeholder(R.drawable.ic_profile)
                    .circleCrop()
                    .into(holder.ivProfile);
        } else {
            holder.ivProfile.setImageResource(R.drawable.ic_profile);
        }

        holder.itemView.setOnClickListener(v -> {
            if (v.getContext() instanceof com.muproject.campusskill.MainActivity) {
                ((com.muproject.campusskill.MainActivity) v.getContext())
                        .replaceFragment(com.muproject.campusskill.PublicProfileFragment.newInstance(item.getId()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvRank, tvName, tvValue, tvExtra;
        ImageView ivProfile;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.tvRank);
            tvName = itemView.findViewById(R.id.tvLeaderName);
            tvValue = itemView.findViewById(R.id.tvLeaderValue);
            tvExtra = itemView.findViewById(R.id.tvLeaderExtra);
            ivProfile = itemView.findViewById(R.id.ivLeaderProfile);
        }
    }
}
