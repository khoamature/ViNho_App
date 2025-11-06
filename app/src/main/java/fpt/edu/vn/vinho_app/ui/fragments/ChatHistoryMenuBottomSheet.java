package fpt.edu.vn.vinho_app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider; // Import mới

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.ConversationResponse;
import fpt.edu.vn.vinho_app.ui.viewmodel.ChatbotViewModel; // Import mới

public class ChatHistoryMenuBottomSheet extends BottomSheetDialogFragment {

    private ChatbotViewModel viewModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Lấy ViewModel được chia sẻ từ Fragment cha
        viewModel = new ViewModelProvider(requireParentFragment()).get(ChatbotViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.bottom_sheet_chat_history_menu, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        view.findViewById(R.id.option_rename).setOnClickListener(v -> {
            // Lấy conversation từ ViewModel và xử lý
            ConversationResponse conversation = viewModel.getSelectedConversation().getValue();
            if (conversation != null) {
                Toast.makeText(getContext(), "Rename: " + conversation.getTitle(), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            // Lấy conversation từ ViewModel và xử lý
            ConversationResponse conversation = viewModel.getSelectedConversation().getValue();
            if (conversation != null) {
                Toast.makeText(getContext(), "Delete: " + conversation.getId(), Toast.LENGTH_SHORT).show();
            }
            dismiss();
        });
    }
}
