package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.tabs.TabLayout;
import com.muproject.campusskill.adapter.LeaderboardAdapter;
import com.muproject.campusskill.model.LeaderboardResponse;
import com.muproject.campusskill.network.RetrofitClient;
import java.util.ArrayList;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LeaderboardFragment extends Fragment {

    private RecyclerView rvLeaderboard;
    private LeaderboardAdapter adapter;
    private LinearLayout layoutEmpty;
    private String currentType = "earners";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_leaderboard, container, false);

        rvLeaderboard = view.findViewById(R.id.rvLeaderboard);
        layoutEmpty = view.findViewById(R.id.layoutEmptyLeaderboard);
        TabLayout tabLayout = view.findViewById(R.id.tabLeaderboard);

        rvLeaderboard.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new LeaderboardAdapter(new ArrayList<>());
        rvLeaderboard.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0: currentType = "earners"; break;
                    case 1: currentType = "rated"; break;
                    case 2: currentType = "active"; break;
                }
                loadLeaderboard();
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        loadLeaderboard();

        return view;
    }

    private void loadLeaderboard() {
        android.app.ProgressDialog pd = new android.app.ProgressDialog(getContext());
        pd.setMessage("Loading rankings...");
        pd.show();

        Call<LeaderboardResponse> call;
        if (currentType.equals("earners")) {
            call = RetrofitClient.getApiService().getTopEarners();
        } else if (currentType.equals("rated")) {
            call = RetrofitClient.getApiService().getTopRated();
        } else {
            call = RetrofitClient.getApiService().getMostActive();
        }

        call.enqueue(new Callback<LeaderboardResponse>() {
            @Override
            public void onResponse(Call<LeaderboardResponse> call, Response<LeaderboardResponse> response) {
                pd.dismiss();
                if (!isAdded() || getContext() == null || getView() == null) return;
                View root = getView();

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvLeaderboard.setVisibility(View.GONE);
                    } else {
                        java.util.List<com.muproject.campusskill.model.LeaderboardItem> all = response.body().getData();
                        layoutEmpty.setVisibility(View.GONE);
                        
                        // Separate Podium (Top 3) if available
                        if (all.size() >= 1) {
                            root.findViewById(R.id.layoutPodium).setVisibility(View.VISIBLE);
                            root.findViewById(R.id.tvOthersTitle).setVisibility(View.VISIBLE);
                            updatePodium(all, root);
                            
                            // The rest for the adapter (Rank 4+)
                            java.util.List<com.muproject.campusskill.model.LeaderboardItem> others = new ArrayList<>();
                            if (all.size() > 3) {
                                others.addAll(all.subList(3, all.size()));
                            }
                            adapter.setItems(others, currentType, 3); // Tell adapter to start rank from 4
                        } else {
                            root.findViewById(R.id.layoutPodium).setVisibility(View.GONE);
                            root.findViewById(R.id.tvOthersTitle).setVisibility(View.GONE);
                            adapter.setItems(all, currentType, 0);
                        }
                        
                        rvLeaderboard.setVisibility(View.VISIBLE);
                    }
                } else {
                    layoutEmpty.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<LeaderboardResponse> call, Throwable t) {
                pd.dismiss();
                if (isAdded()) layoutEmpty.setVisibility(View.VISIBLE);
            }
        });
    }

    private void updatePodium(java.util.List<com.muproject.campusskill.model.LeaderboardItem> all, View root) {
        // Reset visibility
        root.findViewById(R.id.podium1).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.podium2).setVisibility(View.INVISIBLE);
        root.findViewById(R.id.podium3).setVisibility(View.INVISIBLE);

        for (int i = 0; i < Math.min(all.size(), 3); i++) {
            com.muproject.campusskill.model.LeaderboardItem item = all.get(i);
            int layoutId = (i == 0) ? R.id.podium1 : (i == 1) ? R.id.podium2 : R.id.podium3;
            int nameId = (i == 0) ? R.id.tvPodiumName1 : (i == 1) ? R.id.tvPodiumName2 : R.id.tvPodiumName3;
            int valId = (i == 0) ? R.id.tvPodiumValue1 : (i == 1) ? R.id.tvPodiumValue2 : R.id.tvPodiumValue3;
            int imgId = (i == 0) ? R.id.ivPodium1 : (i == 1) ? R.id.ivPodium2 : R.id.ivPodium3;

            View pView = root.findViewById(layoutId);
            pView.setVisibility(View.VISIBLE);

            ((android.widget.TextView) root.findViewById(nameId)).setText(item.getName());
            
            String displayVal = "";
            if (currentType.equals("earners")) displayVal = "₹" + (item.getTotalEarnings() != null ? item.getTotalEarnings() : "0");
            else if (currentType.equals("rated")) displayVal = "⭐ " + item.getAverageRating();
            else displayVal = item.getLeaderboardScore() + " pts";
            
            ((android.widget.TextView) root.findViewById(valId)).setText(displayVal);

            android.widget.ImageView iv = root.findViewById(imgId);
            String img = item.getProfileImage();
            if (img != null && !img.isEmpty()) {
                String url = img.startsWith("http") ? img : "https://lightgrey-dogfish-642647.hostingersite.com/" + img;
                com.bumptech.glide.Glide.with(this).load(url).placeholder(R.drawable.ic_profile).circleCrop().into(iv);
            }
            
            pView.setOnClickListener(v -> {
                if (getActivity() instanceof MainActivity) {
                    ((MainActivity) getActivity()).replaceFragment(PublicProfileFragment.newInstance(item.getId()));
                }
            });
        }
    }
}
