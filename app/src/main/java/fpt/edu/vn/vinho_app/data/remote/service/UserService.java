package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.user.UpdateProfileRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.user.GetProfileResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {
    @GET("users/{userId}/profile")
    Call<BaseResponse<GetProfileResponse>> getProfile(@Path("userId") String userId);

    @PUT("users/{userId}/profile")
    Call<BaseResponse<String>> updateProfile(@Path("userId") String userId, @Body UpdateProfileRequest request);
}
