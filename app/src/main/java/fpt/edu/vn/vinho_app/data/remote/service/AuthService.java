package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.GoogleLoginRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.LoginRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.auth.TokenResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface AuthService {
    @POST("auths/google-login")
    Call<BaseResponse<TokenResponse>> googleLogin(@Body GoogleLoginRequest request);
    @POST("auths/login")
    Call<BaseResponse<TokenResponse>> login(@Body LoginRequest request);
}
