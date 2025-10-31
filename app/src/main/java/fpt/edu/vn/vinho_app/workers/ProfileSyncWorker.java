package fpt.edu.vn.vinho_app.workers;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import java.io.IOException;

import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.user.GetProfileResponse;
import fpt.edu.vn.vinho_app.data.repository.UserRepository;
import retrofit2.Response;

public class ProfileSyncWorker extends Worker {

    public ProfileSyncWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        String userId = sharedPreferences.getString("userId", "");

        if (userId.isEmpty()) {
            return Result.failure();
        }

        try {
            Response<BaseResponse<GetProfileResponse>> response = UserRepository.getUserService(getApplicationContext())
                    .getProfile(userId)
                    .execute();

            if (response.isSuccessful()) {
                // You might want to save the updated profile to a local database here
                return Result.success();
            } else {
                return Result.retry();
            }
        } catch (IOException e) {
            return Result.retry();
        }
    }
}
