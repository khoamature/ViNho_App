package fpt.edu.vn.vinho_app.ui.activities;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.ResetPasswordByPinRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResetPasswordActivity extends AppCompatActivity {

    private EditText edtPin, edtNewPassword, edtConfirmNewPassword;
    private Button btnResetPassword;
    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        edtPin = findViewById(R.id.edtPin);
        edtNewPassword = findViewById(R.id.edtNewPassword);
        edtConfirmNewPassword = findViewById(R.id.edtConfirmNewPassword);
        btnResetPassword = findViewById(R.id.btnResetPassword);

        email = getIntent().getStringExtra("email");

        btnResetPassword.setOnClickListener(v -> {
            handleResetPassword();
        });
    }

    private void handleResetPassword() {
        String pin = edtPin.getText().toString().trim();
        String newPassword = edtNewPassword.getText().toString().trim();
        String confirmNewPassword = edtConfirmNewPassword.getText().toString().trim();

        if (TextUtils.isEmpty(pin) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmNewPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPassword.equals(confirmNewPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        ResetPasswordByPinRequest request = new ResetPasswordByPinRequest(email, pin, newPassword, confirmNewPassword);
        AuthRepository.getAuthService(this).resetPasswordByPin(request).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(ResetPasswordActivity.this, "Password reset successfully", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    String errorMessage = "Reset password failed.";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(ResetPasswordActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Toast.makeText(ResetPasswordActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
