package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.ApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.AuthService;

public class AuthRepository {
    public static AuthService getAuthService(){
        return ApiClient.getClient().create(AuthService.class);
    }

    public static AuthService getAuthService(Context context){
        return ApiClient.getClient().create(AuthService.class);
    }
}
