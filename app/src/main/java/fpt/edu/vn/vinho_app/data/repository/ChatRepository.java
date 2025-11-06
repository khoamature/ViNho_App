package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.ChatService;

public class ChatRepository {
    public static ChatService getChatService(Context context){
        return AuthApiClient.getClient(context).create(ChatService.class);
    }
}
