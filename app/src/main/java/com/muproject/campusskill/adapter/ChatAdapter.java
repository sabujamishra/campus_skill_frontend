package com.muproject.campusskill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.muproject.campusskill.R;
import com.muproject.campusskill.model.ChatMessage;
import java.util.List;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<ChatMessage> messages;
    private int currentUserId;

    private static final int TYPE_ME = 1;
    private static final int TYPE_OTHER = 2;

    public ChatAdapter(List<ChatMessage> messages, int currentUserId) {
        this.messages = messages;
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<ChatMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    public void addMessage(ChatMessage message) {
        this.messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        return messages.get(position).isMe(currentUserId) ? TYPE_ME : TYPE_OTHER;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ME) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_right, parent, false);
            return new MyMessageViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_left, parent, false);
            return new OtherMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = messages.get(position);
        if (holder instanceof MyMessageViewHolder) {
            ((MyMessageViewHolder) holder).bind(message);
        } else {
            ((OtherMessageViewHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MyMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMsg, tvTime;
        MyMessageViewHolder(View v) {
            super(v);
            tvMsg = v.findViewById(R.id.tvChatMessage);
            tvTime = v.findViewById(R.id.tvChatTime);
        }
        void bind(ChatMessage m) {
            tvMsg.setText(m.getMessage());
            tvTime.setText(formatTime(m.getCreatedAt()));
        }
    }

    static class OtherMessageViewHolder extends RecyclerView.ViewHolder {
        TextView tvMsg, tvTime;
        OtherMessageViewHolder(View v) {
            super(v);
            tvMsg = v.findViewById(R.id.tvChatMessage);
            tvTime = v.findViewById(R.id.tvChatTime);
        }
        void bind(ChatMessage m) {
            tvMsg.setText(m.getMessage());
            tvTime.setText(formatTime(m.getCreatedAt()));
        }
    }

    private static String formatTime(String raw) {
        if (raw == null) return "";
        try {
            // "2026-04-10 18:15:00" -> "18:15"
            return raw.split(" ")[1].substring(0, 5);
        } catch (Exception e) {
            return raw;
        }
    }
}
