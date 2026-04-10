package com.muproject.campusskill;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.adapter.ChatAdapter;
import com.muproject.campusskill.model.ChatMessage;
import com.muproject.campusskill.model.CommonResponse;
import com.muproject.campusskill.model.MessageListResponse;
import com.muproject.campusskill.network.RetrofitClient;
import com.muproject.campusskill.network.SessionManager;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatFragment extends Fragment {

    private int orderId;
    private String orderStatus;
    private RecyclerView rvChat;
    private ChatAdapter adapter;
    private EditText etMessage;
    private SessionManager sessionManager;

    public static ChatFragment newInstance(int orderId, String status) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putInt("order_id", orderId);
        args.putString("status", status);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderId = getArguments().getInt("order_id");
            orderStatus = getArguments().getString("status");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        rvChat = view.findViewById(R.id.rvChat);
        etMessage = view.findViewById(R.id.etMessage);
        sessionManager = new SessionManager(requireContext());

        rvChat.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new ChatAdapter(new ArrayList<>(), sessionManager.getUserId());
        rvChat.setAdapter(adapter);

        androidx.appcompat.widget.Toolbar toolbar = view.findViewById(R.id.chatToolbar);
        toolbar.setTitle("Chat (Order #" + orderId + ")");
        toolbar.setNavigationOnClickListener(v -> ((MainActivity) getActivity()).goBack());
        
        view.findViewById(R.id.btnSendMessage).setOnClickListener(v -> sendMessage());

        if ("completed".equalsIgnoreCase(orderStatus)) {
            etMessage.setEnabled(false);
            etMessage.setHint("Messaging blocked for completed orders");
            view.findViewById(R.id.btnSendMessage).setEnabled(false);
            view.findViewById(R.id.btnSendMessage).setAlpha(0.5f);
        }

        loadChatHistory();

        return view;
    }

    private void loadChatHistory() {
        RetrofitClient.getApiService().getChatHistory(orderId).enqueue(new Callback<MessageListResponse>() {
            @Override
            public void onResponse(Call<MessageListResponse> call, Response<MessageListResponse> response) {
                if (isAdded() && response.isSuccessful() && response.body() != null) {
                    adapter.setMessages(response.body().getData());
                    rvChat.scrollToPosition(adapter.getItemCount() - 1);
                }
            }

            @Override
            public void onFailure(Call<MessageListResponse> call, Throwable t) {
                if (isAdded()) Log.e("Chat", "Failed to load history", t);
            }
        });
    }

    private void sendMessage() {
        String msg = etMessage.getText().toString().trim();
        if (msg.isEmpty()) return;

        Map<String, Object> body = new HashMap<>();
        body.put("order_id", orderId);
        body.put("message", msg);

        etMessage.setText("");

        RetrofitClient.getApiService().sendMessage(body).enqueue(new Callback<CommonResponse>() {
            @Override
            public void onResponse(Call<CommonResponse> call, Response<CommonResponse> response) {
                if (isAdded() && response.isSuccessful()) {
                    loadChatHistory(); // Refresh to show new message
                } else if (isAdded()) {
                    Toast.makeText(getContext(), "Message failed to send", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<CommonResponse> call, Throwable t) {
                if (isAdded()) Toast.makeText(getContext(), "Network error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
