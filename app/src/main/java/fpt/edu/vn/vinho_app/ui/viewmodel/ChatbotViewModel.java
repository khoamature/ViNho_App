package fpt.edu.vn.vinho_app.ui.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.ConversationResponse;

public class ChatbotViewModel extends ViewModel {
    private final MutableLiveData<ConversationResponse> selectedConversation = new MutableLiveData<>();

    public void selectConversation(ConversationResponse conversation) {
        selectedConversation.setValue(conversation);
    }

    public LiveData<ConversationResponse> getSelectedConversation() {
        return selectedConversation;
    }
}
    