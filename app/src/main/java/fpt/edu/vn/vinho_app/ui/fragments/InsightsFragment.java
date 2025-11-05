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

    public interface OnInsightFragmentInteractionListener {
        void onChatbotButtonClicked();
    }

    private OnInsightFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnInsightFragmentInteractionListener) {
            mListener = (OnInsightFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement OnInsightFragmentInteractionListener");
        }
    }


    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private TextView tvBudgetAlertDesc, tvSpendingPatternDesc, tvSavingsRateDesc, tvMonthlyGoalDesc;
    private RecyclerView recyclerRecommendedActions;
    private RecommendedActionsAdapter actionsAdapter;
    private FloatingActionButton fabChatbot;
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
        fetchInsightData();
    }

    private void mapViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);

        // Ánh xạ các thẻ và các view con bên trong chúng
        View cardBudgetAlert = view.findViewById(R.id.cardBudgetAlert);
        setupInsightCard(cardBudgetAlert, "Budget Alert", R.drawable.ic_alert, R.color.icon_red, R.drawable.bg_icon_circle_red);
        tvBudgetAlertDesc = cardBudgetAlert.findViewById(R.id.tvCardDescription);

        View cardSpendingPattern = view.findViewById(R.id.cardSpendingPattern);
        setupInsightCard(cardSpendingPattern, "Spending Pattern", R.drawable.ic_spending, R.color.icon_orange, R.drawable.bg_icon_circle_orange);
        tvSpendingPatternDesc = cardSpendingPattern.findViewById(R.id.tvCardDescription);

        View cardSavingsRate = view.findViewById(R.id.cardSavingsRate);
        setupInsightCard(cardSavingsRate, "Savings Rate", R.drawable.ic_savings, R.color.icon_green, R.drawable.bg_icon_circle_green);
        tvSavingsRateDesc = cardSavingsRate.findViewById(R.id.tvCardDescription);

        View cardMonthlyGoal = view.findViewById(R.id.cardMonthlyGoal);
        setupInsightCard(cardMonthlyGoal, "Monthly Goal", R.drawable.ic_goal, R.color.icon_blue, R.drawable.bg_icon_circle_blue);
        tvMonthlyGoalDesc = cardMonthlyGoal.findViewById(R.id.tvCardDescription);

        recyclerRecommendedActions = view.findViewById(R.id.recyclerRecommendedActions);
        fabChatbot = view.findViewById(R.id.fab_chatbot);
    }

    // Helper method để tránh lặp code
    private void setupInsightCard(View cardView, String title, int iconRes, int iconTintRes, int backgroundRes) {
        if (cardView != null) {
            ImageView icon = cardView.findViewById(R.id.ivCardIcon);
            TextView titleView = cardView.findViewById(R.id.tvCardTitle);

            titleView.setText(title);
            icon.setImageResource(iconRes);
            icon.setBackgroundResource(backgroundRes);
            icon.setColorFilter(ContextCompat.getColor(requireContext(), iconTintRes), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }


    private void setupRecyclerView() {
        recyclerRecommendedActions.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerRecommendedActions.setNestedScrollingEnabled(false);
        actionsAdapter = new RecommendedActionsAdapter(getContext(), new ArrayList<>());
        recyclerRecommendedActions.setAdapter(actionsAdapter);
    }

    private void setupListeners() {
        fabChatbot.setOnClickListener(v -> {
            if (mListener != null) {
                mListener.onChatbotButtonClicked();
            }
        });
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
        tvBudgetAlertDesc.setText(data.getBudgetAlert());
        tvSpendingPatternDesc.setText(data.getSpendingPattern());
        tvSavingsRateDesc.setText(data.getSavingsRate());
        tvMonthlyGoalDesc.setText(data.getMonthlyGoal());

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
}
