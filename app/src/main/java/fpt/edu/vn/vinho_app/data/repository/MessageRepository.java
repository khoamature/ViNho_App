package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;

public class MessageRepository {
    public static MessageRepository getMessageService(Context context){
        return AuthApiClient.getClient(context).create(MessageRepository.class);
    }
}
