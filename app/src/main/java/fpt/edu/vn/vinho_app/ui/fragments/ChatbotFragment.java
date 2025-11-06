package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.conversation.RenameConversationRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.ConversationResponse;
import fpt.edu.vn.vinho_app.data.repository.ConversationRepository;
import fpt.edu.vn.vinho_app.ui.adapter.ChatHistoryAdapter;
import fpt.edu.vn.vinho_app.ui.viewmodel.ChatbotViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment implements ChatHistoryAdapter.OnHistoryActionsListener, ChatHistoryMenuBottomSheet.OnMenuOptionClickListener{
    private static final String TAG = "ChatbotFragment";

    private DrawerLayout drawerLayout;
    private ImageView btnOpenDrawer;
    private NavigationView navView;
    private RecyclerView recyclerChatMessages;
    private TextView tvWelcomeMessage;
    private EditText editTextMessage;
    private FloatingActionButton btnSendMessage;
    private RecyclerView recyclerChatHistory;
    private ChatHistoryAdapter chatHistoryAdapter;
    private TextView tvNoHistory;
    private SharedPreferences sharedPreferences;

    // private ConversationResponse selectedConversation;
    private ChatbotViewModel viewModel;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (getActivity() != null && getActivity().getWindow() != null) {
            getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ChatbotViewModel.class);

        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        mapViews(view);
        setupRecyclerViews();
        setupListeners();
        fetchChatHistory();
    }

    private void mapViews(View view) {
        drawerLayout = view.findViewById(R.id.drawer_layout);
        btnOpenDrawer = view.findViewById(R.id.btnOpenDrawer);
        navView = view.findViewById(R.id.nav_view);
        recyclerChatMessages = view.findViewById(R.id.recycler_chat_messages);
        tvWelcomeMessage = view.findViewById(R.id.tv_welcome_message);
        editTextMessage = view.findViewById(R.id.edit_text_message);
        btnSendMessage = view.findViewById(R.id.btn_send_message);
        recyclerChatHistory = view.findViewById(R.id.recycler_chat_history);
        tvNoHistory = view.findViewById(R.id.tv_no_history);
    }

    private void setupRecyclerViews() {
        // Setup cho recycler tin nhắn
        // TODO: ...

        // Setup cho recycler lịch sử chat
        recyclerChatHistory.setLayoutManager(new LinearLayoutManager(getContext()));
        chatHistoryAdapter = new ChatHistoryAdapter(new ArrayList<>(), this);
        recyclerChatHistory.setAdapter(chatHistoryAdapter);
    }

    private void fetchChatHistory() {
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) {
            Log.e(TAG, "UserID is empty. Cannot fetch history.");
            return;
        }

        Log.d(TAG, "Fetching chat history for UserID: " + userId);

        ConversationRepository.getConversationService(getContext())
                .getConversations(userId, 1000, "createdAt", true)
                .enqueue(new Callback<PagedResponse<ConversationResponse>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<ConversationResponse>> call, Response<PagedResponse<ConversationResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<ConversationResponse> history = response.body().getPayload();
                            if (history != null && !history.isEmpty()) {
                                recyclerChatHistory.setVisibility(View.VISIBLE);
                                tvNoHistory.setVisibility(View.GONE);
                                chatHistoryAdapter.updateData(history);
                            } else {
                                recyclerChatHistory.setVisibility(View.GONE);
                                tvNoHistory.setVisibility(View.VISIBLE);
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown API error";
                                Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body", e);
                            }
                            recyclerChatHistory.setVisibility(View.GONE);
                            tvNoHistory.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<ConversationResponse>> call, Throwable t) {
                        Log.e(TAG, "Network Failure", t);
                        recyclerChatHistory.setVisibility(View.GONE);
                        tvNoHistory.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void setupListeners() {
        btnOpenDrawer.setOnClickListener(v -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        btnSendMessage.setOnClickListener(v -> {
            String message = editTextMessage.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessage(message);
            }
        });

        View headerView = navView.getHeaderView(0);
        if (headerView != null) {
            headerView.findViewById(R.id.btnNewChat).setOnClickListener(v -> {
                newChat();
                drawerLayout.closeDrawer(GravityCompat.START);
            });
            ImageView btnCloseDrawer = headerView.findViewById(R.id.btnCloseDrawer);
            if (btnCloseDrawer != null) {
                btnCloseDrawer.setOnClickListener(v -> {
                    if (getActivity() != null) {
                        getActivity().onBackPressed();
                    }
                });
            }
        }
    }

    private void sendMessage(String message) {
        Toast.makeText(getContext(), "Sending: " + message, Toast.LENGTH_SHORT).show();
        editTextMessage.setText("");
        if (tvWelcomeMessage.getVisibility() == View.VISIBLE) {
            tvWelcomeMessage.setVisibility(View.GONE);
        }
    }

    private void newChat() {
        Toast.makeText(getContext(), "New chat started!", Toast.LENGTH_SHORT).show();
        tvWelcomeMessage.setVisibility(View.VISIBLE);
    }

    // --- Implement các phương thức từ interface của Adapter ---

    @Override
    public void onHistoryItemSelected(ConversationResponse conversation) {
        // TODO: Load nội dung của cuộc trò chuyện này
        Toast.makeText(getContext(), "Loading: " + conversation.getTitle(), Toast.LENGTH_SHORT).show();
        drawerLayout.closeDrawer(GravityCompat.START);
    }

    @Override
    public void onMenuClicked(ConversationResponse conversation) {
        // 1. Lưu conversation được chọn vào ViewModel
        viewModel.selectConversation(conversation);
        // 2. Mở BottomSheet
        ChatHistoryMenuBottomSheet bottomSheet = new ChatHistoryMenuBottomSheet();
        bottomSheet.show(getChildFragmentManager(), bottomSheet.getTag());
    }

    @Override
    public void onRenameClicked(ConversationResponse conversation) {
        showRenameDialog(conversation);
    }

    @Override
    public void onDeleteClicked(ConversationResponse conversation) {
        showDeleteConfirmationDialog(conversation);
    }
    private void showRenameDialog(final ConversationResponse conversation) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Rename Conversation");

        // Set up the input
        final EditText input = new EditText(requireContext());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(conversation.getTitle()); // Điền sẵn tên cũ
        input.setSelection(input.getText().length()); // Đưa con trỏ về cuối
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty() && !newTitle.equals(conversation.getTitle())) {
                renameConversation(conversation.getId(), newTitle);
            }
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void showDeleteConfirmationDialog(final ConversationResponse conversation) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Delete Conversation")
                .setMessage("Are you sure you want to delete this chat history?\n\"" + conversation.getTitle() + "\"")
                .setPositiveButton("Delete", (dialog, which) -> deleteConversation(conversation.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void renameConversation(String conversationId, String newTitle) {
        RenameConversationRequest request = new RenameConversationRequest(newTitle);
        ConversationRepository.getConversationService(getContext()).renameConversation(conversationId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Renamed successfully", Toast.LENGTH_SHORT).show();
                            fetchChatHistory(); // Làm mới lại danh sách
                        } else {
                            Toast.makeText(getContext(), "Failed to rename", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void deleteConversation(String conversationId) {
        ConversationRepository.getConversationService(getContext()).deleteConversation(conversationId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Deleted successfully", Toast.LENGTH_SHORT).show();
                            fetchChatHistory(); // Làm mới lại danh sách
                        } else {
                            Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
