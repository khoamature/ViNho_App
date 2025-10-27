package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.RagChatService;

public class RagChatRepository {
    public static RagChatService getRagChatService(Context context){
        return AuthApiClient.getClient(context).create(RagChatService.class);
    }
}
