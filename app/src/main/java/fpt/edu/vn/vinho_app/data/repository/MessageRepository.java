package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.MessageService;

public class MessageRepository {
    public static MessageService getMessageService(Context context){
        return AuthApiClient.getClient(context).create(MessageService.class);
    }
}
