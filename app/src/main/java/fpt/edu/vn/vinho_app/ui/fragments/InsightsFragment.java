package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.IOException;
import java.util.ArrayList;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.AIInsightResponse;
import fpt.edu.vn.vinho_app.data.repository.ReportRepository;
import fpt.edu.vn.vinho_app.ui.adapter.RecommendedActionsAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InsightsFragment extends Fragment {
    private static final String TAG = "InsightsFragment";

    // Loading progressbar
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;

    // Views for Insight Cards
    private View cardBudgetAlert, cardSpendingPattern, cardSavingsRate, cardMonthlyGoal;
    private TextView tvBudgetAlertDesc, tvSpendingPatternDesc, tvSavingsRateDesc, tvMonthlyGoalDesc;

    // RecyclerView for Recommended Actions
    private RecyclerView recyclerRecommendedActions;
    private RecommendedActionsAdapter actionsAdapter;

    // Views for Chatbot
    private FloatingActionButton fabChatbot;
    private FrameLayout chatbotContainer;
    private boolean isChatbotOpen = false;

    private SharedPreferences sharedPreferences;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_insights, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        mapViews(view);
        setupRecyclerView();
        setupListeners();

        // Tải dữ liệu từ API
        fetchInsightData();
    }

    private void mapViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Ánh xạ các thẻ
        cardBudgetAlert = view.findViewById(R.id.cardBudgetAlert);
        cardSpendingPattern = view.findViewById(R.id.cardSpendingPattern);
        cardSavingsRate = view.findViewById(R.id.cardSavingsRate);
        cardMonthlyGoal = view.findViewById(R.id.cardMonthlyGoal);

        // Thẻ Budget Alert
        cardBudgetAlert = view.findViewById(R.id.cardBudgetAlert);
        ImageView ivBudgetAlert = cardBudgetAlert.findViewById(R.id.ivCardIcon);
        tvBudgetAlertDesc = cardBudgetAlert.findViewById(R.id.tvCardDescription);
        ((TextView) cardBudgetAlert.findViewById(R.id.tvCardTitle)).setText("Budget Alert");
        ivBudgetAlert.setImageResource(R.drawable.ic_alert);
        ivBudgetAlert.setBackgroundResource(R.drawable.bg_icon_circle_red);
        ivBudgetAlert.setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_red), android.graphics.PorterDuff.Mode.SRC_IN);

        // Thẻ Spending Pattern
        cardSpendingPattern = view.findViewById(R.id.cardSpendingPattern);
        ImageView ivSpendingPattern = cardSpendingPattern.findViewById(R.id.ivCardIcon);
        tvSpendingPatternDesc = cardSpendingPattern.findViewById(R.id.tvCardDescription);
        ((TextView) cardSpendingPattern.findViewById(R.id.tvCardTitle)).setText("Spending Pattern");
        ivSpendingPattern.setImageResource(R.drawable.ic_spending);
        ivSpendingPattern.setBackgroundResource(R.drawable.bg_icon_circle_orange);
        ivSpendingPattern.setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_orange), android.graphics.PorterDuff.Mode.SRC_IN);

        // Thẻ Savings Rate
        cardSavingsRate = view.findViewById(R.id.cardSavingsRate);
        ImageView ivSavingsRate = cardSavingsRate.findViewById(R.id.ivCardIcon);
        tvSavingsRateDesc = cardSavingsRate.findViewById(R.id.tvCardDescription);
        ((TextView) cardSavingsRate.findViewById(R.id.tvCardTitle)).setText("Savings Rate");
        ivSavingsRate.setImageResource(R.drawable.ic_savings);
        ivSavingsRate.setBackgroundResource(R.drawable.bg_icon_circle_green);
        ivSavingsRate.setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_green), android.graphics.PorterDuff.Mode.SRC_IN);

        // Thẻ Monthly Goal
        cardMonthlyGoal = view.findViewById(R.id.cardMonthlyGoal);
        ImageView ivMonthlyGoal = cardMonthlyGoal.findViewById(R.id.ivCardIcon);
        tvMonthlyGoalDesc = cardMonthlyGoal.findViewById(R.id.tvCardDescription);
        ((TextView) cardMonthlyGoal.findViewById(R.id.tvCardTitle)).setText("Monthly Goal");
        ivMonthlyGoal.setImageResource(R.drawable.ic_goal);
        ivMonthlyGoal.setBackgroundResource(R.drawable.bg_icon_circle_blue);
        ivMonthlyGoal.setColorFilter(ContextCompat.getColor(requireContext(), R.color.icon_blue), android.graphics.PorterDuff.Mode.SRC_IN);

        // Ánh xạ các thành phần khác
        recyclerRecommendedActions = view.findViewById(R.id.recyclerRecommendedActions);
        fabChatbot = view.findViewById(R.id.fab_chatbot);
        chatbotContainer = view.findViewById(R.id.chatbot_container);
    }

    private void setupRecyclerView() {
        recyclerRecommendedActions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRecommendedActions.setNestedScrollingEnabled(false);
        actionsAdapter = new RecommendedActionsAdapter(getContext(), new ArrayList<>());
        recyclerRecommendedActions.setAdapter(actionsAdapter);
    }

    private void setupListeners() {
        fabChatbot.setOnClickListener(v -> toggleChatbot());
    }

    private void fetchInsightData() {
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) {
            Toast.makeText(getContext(), "User ID not found.", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, "Fetching AI Insight for UserID: " + userId);
        showLoading(true);

        ReportRepository.getReportService(getContext()).getAIInsight(userId)
                .enqueue(new Callback<BaseResponse<AIInsightResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<AIInsightResponse>> call, Response<BaseResponse<AIInsightResponse>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            AIInsightResponse data = response.body().getPayload();
                            if (data != null) {
                                Log.d(TAG, "AI Insight fetched successfully.");
                                updateUI(data);
                            } else {
                                Log.w(TAG, "API Success but payload is null.");
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown API error";
                                Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<AIInsightResponse>> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Network Failure", t);
                        Toast.makeText(getContext(), "Network Error. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(AIInsightResponse data) {
        // Cập nhật các thẻ insight
        tvBudgetAlertDesc.setText(data.getBudgetAlert());
        tvSpendingPatternDesc.setText(data.getSpendingPattern());
        tvSavingsRateDesc.setText(data.getSavingsRate());
        tvMonthlyGoalDesc.setText(data.getMonthlyGoal());

        // Cập nhật danh sách Recommended Actions
        if (data.getFinancialTips() != null) {
            actionsAdapter.updateData(data.getFinancialTips());
        }
    }
    private void showLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            nestedScrollView.setVisibility(View.GONE);
        } else {
            progressBar.setVisibility(View.GONE);
            nestedScrollView.setVisibility(View.VISIBLE);
        }
    }
    private void toggleChatbot() {
        // ... (logic để mở/đóng fragment chatbot)
    }

}
