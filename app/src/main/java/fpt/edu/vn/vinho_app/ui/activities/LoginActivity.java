package fpt.edu.vn.vinho_app.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.auth.GoogleLoginRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.auth.TokenResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.repository.AuthRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private GoogleSignInClient mGoogleSignInClient;
    private ActivityResultLauncher<Intent> mSignInLauncher;

    //Khai bao
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
                // Đổi sang chế độ Sign Up
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
                // Quay lại chế độ Sign In
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

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setClass(LoginActivity.this, OnboardingActivity.class);
                startActivity(intent);
            }
        });

        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        // 2. Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                // You need to get the web client ID from your google-services.json file
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // 3. Set up the sign-in launcher
        mSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                        try {
                            GoogleSignInAccount account = task.getResult(ApiException.class);
                            Log.d("GOOGLE_SIGN_IN", "Google sign in successful for: " + account.getEmail());
                            googleLogin(account.getIdToken());
                        } catch (ApiException e) {
                            Log.w("GOOGLE_SIGN_IN", "Google sign in failed", e);
                            Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
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

        TextInputLayout layoutFullName = findViewById(R.id.layoutFullName);
        TextInputLayout layoutConfirmPassword = findViewById(R.id.layoutConfirmPassword);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        mSignInLauncher.launch(signInIntent);
    }

    private void googleLogin(String idToken) {
        GoogleLoginRequest request = new GoogleLoginRequest(idToken);
        Call<BaseResponse<TokenResponse>> call = AuthRepository.getAuthService().googleLogin(request);

        call.enqueue(new Callback<BaseResponse<TokenResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<TokenResponse>> call, Response<BaseResponse<TokenResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    String accessToken = response.body().getPayload().getAccessToken();

                    SharedPreferences sharedPref = getSharedPreferences("auth", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("token", accessToken);
                    editor.apply();

                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_SHORT).show();
                    navigateToInsight();
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
                t.printStackTrace();
            }
        });
    }

    private void navigateToInsight() {
        Intent intent = new Intent(LoginActivity.this, OnboardingActivity.class);
        startActivity(intent);
        finish(); // Prevents user from going back to the login screen
    }

    private void fadeInView(View view) {
        if (view != null) {
            view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(400)
                .setListener(null);
        }
    }

    private void fadeOutView(View view) {
        if (view != null) {
        view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> view.setVisibility(View.GONE));
        }
    }
}