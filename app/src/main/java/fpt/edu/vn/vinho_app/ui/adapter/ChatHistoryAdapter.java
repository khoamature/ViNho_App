package fpt.edu.vn.vinho_app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.PopupMenu;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.ConversationResponse;
import fpt.edu.vn.vinho_app.ui.fragments.ChatHistoryMenuBottomSheet;

public class ChatHistoryAdapter extends RecyclerView.Adapter<ChatHistoryAdapter.ViewHolder>{
    public interface OnHistoryActionsListener {
        void onHistoryItemSelected(ConversationResponse conversation);
        void onMenuClicked(ConversationResponse conversation);
    }

    private final List<ConversationResponse> conversations;
    private final OnHistoryActionsListener listener;

    public ChatHistoryAdapter(List<ConversationResponse> conversations, OnHistoryActionsListener listener) {
        this.conversations = conversations;
        this.listener = listener;
    }


    public void updateData(List<ConversationResponse> newConversations) {
        this.conversations.clear();
        this.conversations.addAll(newConversations);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chat_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(conversations.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return conversations.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvHistoryTitle;
        ImageView btnHistoryMenu;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvHistoryTitle = itemView.findViewById(R.id.tvHistoryTitle);
            btnHistoryMenu = itemView.findViewById(R.id.btnHistoryMenu);
        }

        public void bind(final ConversationResponse conversation, final OnHistoryActionsListener listener) {
            tvHistoryTitle.setText(conversation.getTitle());
            itemView.setOnClickListener(v -> listener.onHistoryItemSelected(conversation));
            btnHistoryMenu.setOnClickListener(view -> {
                if (listener != null) {
                    // Chỉ cần thông báo cho Fragment biết nút menu đã được nhấn
                    listener.onMenuClicked(conversation);
                }
            });
        }
    }
}
