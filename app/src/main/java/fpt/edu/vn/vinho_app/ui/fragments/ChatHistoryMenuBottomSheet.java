package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
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

    public interface OnMenuOptionClickListener {
        void onRenameClicked(ConversationResponse conversation);
        void onDeleteClicked(ConversationResponse conversation);
    }
    private OnMenuOptionClickListener mListener;
    private ChatbotViewModel viewModel;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Gán listener từ Fragment cha (ChatbotFragment)
        if (getParentFragment() instanceof OnMenuOptionClickListener) {
            mListener = (OnMenuOptionClickListener) getParentFragment();
        } else {
            // Hoặc có thể lấy từ Activity nếu cần
            if (context instanceof OnMenuOptionClickListener) {
                mListener = (OnMenuOptionClickListener) context;
            } else {
                throw new ClassCastException(context.toString() + " must implement OnMenuOptionClickListener");
            }
        }
    }
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

        // Lấy conversation từ ViewModel
        final ConversationResponse conversation = viewModel.getSelectedConversation().getValue();
        if (conversation == null) {
            dismiss(); // Nếu không có conversation nào được chọn, đóng bottom sheet
            return;
        }

        view.findViewById(R.id.option_rename).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onRenameClicked(conversation);
            }
            dismiss();
        });

        view.findViewById(R.id.option_delete).setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onDeleteClicked(conversation);
            }
            dismiss();
        });
    }
}
