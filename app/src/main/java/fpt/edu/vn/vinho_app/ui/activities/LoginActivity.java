package fpt.edu.vn.vinho_app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.ForgotPasswordRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.GoogleLoginRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.LoginRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.RegisterRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.ResetPasswordByPinRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.auth.TokenResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.repository.AuthRepository;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> mSignInLauncher;
    private final CompositeDisposable compositeDisposable = new CompositeDisposable();

    TextView tvTitle, tvSubtitle, tvForgotPassword, tvOr, tvSignUp;
    EditText edtFullName, edtEmail, edtPassword, edtConfirmPassword;
    TextInputLayout layoutFullName, layoutConfirmPassword;
    Button btnSignIn, btnGoogle;

    boolean isSignUp = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initWidget();

        tvSignUp.setOnClickListener(v -> {
            if (!isSignUp) {
                isSignUp = true;
                fadeInView(layoutFullName);
                fadeInView(layoutConfirmPassword);
                tvForgotPassword.setVisibility(View.GONE);
                btnSignIn.setText("Create Account");
                tvTitle.setText("Create Account");
                tvSubtitle.setText("Start your journey to better finances");
                tvOr.setText("or Sign Up with");
                tvSignUp.setText("Already have an account? Sign In");
            } else {
                isSignUp = false;
                fadeOutView(layoutFullName);
                fadeOutView(layoutConfirmPassword);
                fadeInView(tvForgotPassword);
                btnSignIn.setText("Sign In");
                tvTitle.setText("Welcome Back!");
                tvSubtitle.setText("Sign in to continue to Ví Nhỏ");
                tvOr.setText("or continue with");
                tvSignUp.setText("Don't have an account? Sign Up");
            }
        });

        btnSignIn.setOnClickListener(v -> {
            if (isSignUp) {
                handleSignUp();
            } else {
                handleLogin();
            }
        });

        btnGoogle.setOnClickListener(v -> signIn());

        tvForgotPassword.setOnClickListener(v -> showForgotPasswordDialog());

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            googleLogin(account.getIdToken());
                        } catch (ApiException e) {
                            Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
    }

    private void handleSignUp(){
        String fullName = edtFullName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();
        String confirmPassword = edtConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(fullName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void handleLogin() {
        String email = edtEmail.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        LoginRequest loginRequest = new LoginRequest(email, password);
        Call<BaseResponse<TokenResponse>> call = AuthRepository.getAuthService(getApplicationContext()).login(loginRequest);

        call.enqueue(new Callback<BaseResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TokenResponse>> call, Response<BaseResponse<TokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String accessToken = response.body().getPayload().getAccessToken();
                    String userId = "";
                    try {
                        JSONObject payload = new JSONObject(new String(Base64.decode(accessToken.split("\\.")[1], Base64.URL_SAFE), StandardCharsets.UTF_8));
                        userId = payload.getString("id");
                    } catch (JSONException e) {
                        Log.e("JWT_DECODE_ERROR", "Failed to decode JWT", e);
                    }

                    saveAuthData(accessToken, userId);

                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    String errorMessage = "Login failed.";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<TokenResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAuthData(String accessToken, String userId) {
        SharedPreferences sharedPref = getSharedPreferences("myPrefs", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", accessToken);
        editor.putString("userId", userId);
        editor.apply();
    }

    private void initWidget() {
        tvTitle = findViewById(R.id.tvTitle);
        tvSubtitle = findViewById(R.id.tvSubtitle);
        tvForgotPassword = findViewById(R.id.tvForgotPassword);
        tvOr = findViewById(R.id.tvOr);
        tvSignUp = findViewById(R.id.tvSignUp);
        edtFullName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        edtConfirmPassword = findViewById(R.id.edtConfirmPassword);
        btnSignIn = findViewById(R.id.btnSignIn);
        btnGoogle = findViewById(R.id.btnGoogle);
        layoutFullName = findViewById(R.id.layoutFullName);
        layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mSignInLauncher.launch(signInIntent);
    }

    private void googleLogin(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<BaseResponse<TokenResponse>> call = AuthRepository.getAuthService(getApplicationContext()).googleLogin(request);

        call.enqueue(new Callback<BaseResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TokenResponse>> call, Response<BaseResponse<TokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String accessToken = response.body().getPayload().getAccessToken();
                    String userId = "";
                    try {
                        JSONObject payload = new JSONObject(new String(Base64.decode(accessToken.split("\\.")[1], Base64.URL_SAFE), StandardCharsets.UTF_8));
                        userId = payload.getString("id");
                    } catch (JSONException e) {
                        Log.e("JWT_DECODE_ERROR", "Failed to decode JWT", e);
                    }

                    saveAuthData(accessToken, userId);

                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    navigateToMain();
                } else {
                    String errorMessage = "Login failed.";
                    if (response.body() != null) {
                        errorMessage = response.body().getMessage();
                    }
                    Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<TokenResponse>> call, Throwable t) {
                Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void navigateToMain() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void fadeInView(View view) {
        if (view != null) {
            view.setAlpha(0f);
            view.setVisibility(View.VISIBLE);
            view.animate().alpha(1f).setDuration(400).setListener(null);
        }
    }

    private void fadeOutView(View view) {
        if (view != null) {
            view.animate().alpha(0f).setDuration(300).withEndAction(() -> view.setVisibility(View.GONE));
        }
    }
    private void showForgotPasswordDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_forgot_password, null);
        builder.setView(dialogView);

        final EditText edtEmail = dialogView.findViewById(R.id.edtEmail);
        Button btnSend = dialogView.findViewById(R.id.btnSend);

        final AlertDialog dialog = builder.create();

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        btnSend.setOnClickListener(v -> {
            String email = edtEmail.getText().toString().trim();
            if (TextUtils.isEmpty(email)) {
                Toast.makeText(this, "Please enter your email", Toast.LENGTH_SHORT).show();
                return;
            }

            AuthRepository.getAuthService(this).forgotPasswordByPin(new ForgotPasswordRequest(email)).enqueue(new Callback<BaseResponse<String>>() {
                @Override
                public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(LoginActivity.this, "A PIN has been sent to your email", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(LoginActivity.this, ResetPasswordActivity.class);
//                        intent.putExtra("email", email);
//                        startActivity(intent);
                        dialog.dismiss();
                        showResetPasswordDialog(email);
                    } else {
                        String errorMessage = "Failed to send PIN.";
                        if (response.body() != null) {
                            errorMessage = response.body().getMessage();
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        dialog.show();
    }

    private void showResetPasswordDialog(String email) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.activity_reset_password, null);
        builder.setView(dialogView);

        EditText edtPin = dialogView.findViewById(R.id.edtPin);
        EditText edtNewPassword = dialogView.findViewById(R.id.edtNewPassword);
        EditText edtConfirmPassword = dialogView.findViewById(R.id.edtConfirmNewPassword);
        Button btnReset = dialogView.findViewById(R.id.btnResetPassword);

        AlertDialog resetDialog = builder.create();
        // Giữ bo góc đẹp, tránh wrap vuông
        if (resetDialog.getWindow() != null) {
            resetDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        btnReset.setOnClickListener(v -> {
            String pin = edtPin.getText().toString().trim();
            String newPassword = edtNewPassword.getText().toString().trim();
            String confirmPassword = edtConfirmPassword.getText().toString().trim();

            if (TextUtils.isEmpty(pin) || TextUtils.isEmpty(newPassword) || TextUtils.isEmpty(confirmPassword)) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            ResetPasswordByPinRequest request = new ResetPasswordByPinRequest(email, pin, newPassword, confirmPassword);
            AuthRepository.getAuthService(this).resetPasswordByPin(request).enqueue(new Callback<BaseResponse<String>>() {
                @Override
                public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                        Toast.makeText(LoginActivity.this, "Password reset successfully!", Toast.LENGTH_SHORT).show();
                        resetDialog.dismiss();
                    } else {
                        String errorMessage = response.body() != null ? response.body().getMessage() : "Failed to reset password.";
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                    Toast.makeText(LoginActivity.this, "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });

        resetDialog.show();
    }
}
