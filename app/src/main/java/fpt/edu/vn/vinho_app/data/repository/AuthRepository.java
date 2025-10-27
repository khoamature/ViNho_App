package fpt.edu.vn.vinho_app.data.repository;

import fpt.edu.vn.vinho_app.data.remote.retrofit.ApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.AuthService;

public class AuthRepository {
    public static AuthService getAuthService(){
        return ApiClient.getClient().create(AuthService.class);
    }
}
