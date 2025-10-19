package fpt.edu.vn.vinho_app.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.textfield.TextInputLayout;

import fpt.edu.vn.vinho_app.R;

public class LoginActivity extends AppCompatActivity {
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
        //Gan gia tri
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

    }

    private void fadeInView(View view) {
        view.setAlpha(0f);
        view.setVisibility(View.VISIBLE);
        view.animate()
                .alpha(1f)
                .setDuration(400)
                .setListener(null);
    }

    private void fadeOutView(View view) {
        view.animate()
                .alpha(0f)
                .setDuration(300)
                .withEndAction(() -> view.setVisibility(View.GONE));
    }
}