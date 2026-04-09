package com.muproject.campusskill;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.adapter.CategoryAdapter;
import com.muproject.campusskill.adapter.ServiceAdapter;
import com.muproject.campusskill.model.Category;
import com.muproject.campusskill.model.Service;
import java.util.ArrayList;
import java.util.List;

// Home screen placeholder (Hinglish: Home screen ka main marketplace logic)
public class HomeFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        view.findViewById(R.id.fabCreateService).setOnClickListener(v -> {
            // Dashboard fragment se fragment transition handle kar rahe hain (Hinglish: Naya screen open ho raha hai)
            if (getActivity() != null) {
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, new CreateServiceFragment())
                        .addToBackStack(null)
                        .commit();
            }
        });

        return view;
    }
}
