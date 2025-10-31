package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.UserService;

public class UserRepository {
    public static UserService getUserService(Context context) {
        return AuthApiClient.getClient(context).create(UserService.class);
    }
}
