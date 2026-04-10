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
                if (!isAdded() || getContext() == null) return;

                if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                    if (response.body().getData().isEmpty()) {
                        layoutEmpty.setVisibility(View.VISIBLE);
                        rvLeaderboard.setVisibility(View.GONE);
                    } else {
                        layoutEmpty.setVisibility(View.GONE);
                        rvLeaderboard.setVisibility(View.VISIBLE);
                        adapter.setItems(response.body().getData(), currentType);
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
}
