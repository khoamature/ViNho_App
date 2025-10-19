package fpt.edu.vn.vinho_app.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.adapter.OnboardingAdapter;
import fpt.edu.vn.vinho_app.models.OnboardingItem;

public class OnboardingActivity extends AppCompatActivity {
    //Khai bao
    private OnboardingAdapter onboardingAdapter;
    private LinearLayout layoutOnboardingIndicators;
    private Button btnNext;
    private ViewPager2 onboardingViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra nếu user đã xem onboarding rồi
        if (isOnboardingFinished()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_onboarding);

        layoutOnboardingIndicators = findViewById(R.id.layoutOnboardingIndicators);
        btnNext = findViewById(R.id.btnNext);
        onboardingViewPager = findViewById(R.id.onboardingViewPager);

        setupOnboardingItems();
        onboardingViewPager.setAdapter(onboardingAdapter);

        setupOnboardingIndicators();
        setCurrentOnboardingIndicator(0);

        onboardingViewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                setCurrentOnboardingIndicator(position);
                // đổi text nút ở slide cuối cùng
                if (position == onboardingAdapter.getItemCount() - 1) {
                    btnNext.setText("Get Started");
                } else {
                    btnNext.setText("Next");
                }
            }
        });

        btnNext.setOnClickListener(v -> {
            if (onboardingViewPager.getCurrentItem() + 1 < onboardingAdapter.getItemCount()) {
                onboardingViewPager.setCurrentItem(onboardingViewPager.getCurrentItem() + 1);
            } else {
                // Lưu trạng thái đã xem onboarding
                setOnboardingFinished();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
            }
        });

    }

    private void setupOnboardingItems() {
        List<OnboardingItem> onboardingItems = new ArrayList<>();

        onboardingItems.add(new OnboardingItem(R.drawable.transaction_history, "Record Transactions",
                "Log your daily income and expenses easily. Keep track of every dollar that comes in and goes out."));
        onboardingItems.add(new OnboardingItem(R.drawable.budget, "Set & Track Budgets",
                "Create budgets for different categories and monitor your spending with beautiful charts."));
        onboardingItems.add(new OnboardingItem(R.drawable.ai, "AI-Powered Insights",
                "Get personalized tips and recommendations to save more and spend smarter based on your habits."));

        onboardingAdapter = new OnboardingAdapter(onboardingItems);
    }

    private void setupOnboardingIndicators() {
        int itemCount = onboardingAdapter.getItemCount();
        ImageView[] indicators = new ImageView[itemCount];
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(8, 0, 8, 0);

        for (int i = 0; i < indicators.length; i++) {
            indicators[i] = new ImageView(getApplicationContext());
            indicators[i].setImageDrawable(
                    ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
            );
            indicators[i].setLayoutParams(layoutParams);
            layoutOnboardingIndicators.addView(indicators[i]);
        }
    }

    private void setCurrentOnboardingIndicator(int index) {
        int childCount = layoutOnboardingIndicators.getChildCount();
        for (int i = 0; i < childCount; i++) {
            ImageView imageView = (ImageView) layoutOnboardingIndicators.getChildAt(i);
            if (i == index) {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_active)
                );
            } else {
                imageView.setImageDrawable(
                        ContextCompat.getDrawable(getApplicationContext(), R.drawable.onboarding_indicator_inactive)
                );
            }
        }
    }

    private boolean isOnboardingFinished() {
        SharedPreferences preferences = getSharedPreferences("onboarding", Context.MODE_PRIVATE);
        return preferences.getBoolean("finished", false);
    }

    private void setOnboardingFinished() {
        SharedPreferences preferences = getSharedPreferences("onboarding", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("finished", true);
        editor.apply();
    }
}