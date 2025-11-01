package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.ConversationService;

public class ConversationRepository {
    public static ConversationService getConversationService(Context context) {
        return AuthApiClient.getClient(context).create(ConversationService.class);
    }
}
