package fpt.edu.vn.vinho_app.ui.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.adapter.ChatAdapter;
import fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat.ChatRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.ragchat.ChatResponse;
import fpt.edu.vn.vinho_app.data.repository.RagChatRepository;
import fpt.edu.vn.vinho_app.domain.model.Message;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private RecyclerView recyclerView;
    private ChatAdapter adapter;
    private List<Message> messageList;
    private EditText editTextMessage;
    private Button buttonSend;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_chatbot, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        recyclerView = view.findViewById(R.id.recyclerViewChat);
        editTextMessage = view.findViewById(R.id.editTextMessage);
        buttonSend = view.findViewById(R.id.buttonSend);

        messageList = new ArrayList<>();
        adapter = new ChatAdapter(messageList);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        buttonSend.setOnClickListener(v -> sendMessage());
    }

    private void sendMessage() {
        String messageText = editTextMessage.getText().toString().trim();
        if (!messageText.isEmpty()) {
            messageList.add(new Message(messageText, true));
            adapter.notifyItemInserted(messageList.size() - 1);
            editTextMessage.setText("");

            final Message thinkingMessage = new Message("Thinking...", false);
            messageList.add(thinkingMessage);
            adapter.notifyItemInserted(messageList.size() - 1);
            recyclerView.scrollToPosition(messageList.size() - 1);

            ChatRequest request = new ChatRequest(messageText);
            Call<ChatResponse> call = RagChatRepository.getRagChatService(getContext()).chat(request);

            call.enqueue(new Callback<ChatResponse>() {
                @Override
                public void onResponse(Call<ChatResponse> call, Response<ChatResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        thinkingMessage.setText(response.body().getPayload().getAnswer());
                    } else {
                        String errorMessage = "Error: Could not get a response.";
                        if (response.body() != null) {
                            errorMessage = response.body().getMessage();
                        }
                        Log.e("API_ERROR", "Response not successful or body is null");
                        thinkingMessage.setText(errorMessage);
                    }
                    adapter.notifyItemChanged(messageList.size() - 1);
                }

                @Override
                public void onFailure(Call<ChatResponse> call, Throwable t) {
                    Log.e("API_ERROR", "API call failed", t);
                    thinkingMessage.setText("Error: API call failed.");
                    adapter.notifyItemChanged(messageList.size() - 1);
                }
            });
        }
    }
}
