package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.HomePageReportResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.RecentTransaction;
import fpt.edu.vn.vinho_app.data.repository.ReportRepository;
import fpt.edu.vn.vinho_app.ui.adapter.RecentTransactionAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String TAG = "HomeFragment";
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView tvCurrentBalanceAmount, tvOverviewIncome, tvOverviewExpense;
    private MaterialCardView btnInsight, btnReports;
    private PieChart pieChart;
    private View emptyStatePieChart, emptyStateRecent;
    private RecyclerView recyclerRecentTransactions;
    private RecentTransactionAdapter recentTransactionAdapter;
    private SharedPreferences sharedPreferences;

    public interface OnHomeFragmentInteractionListener {
        void onReportsButtonClicked();
    }

    private OnHomeFragmentInteractionListener mListener;

    @Override
    public void onAttach(@NonNull  Context context) {
        super.onAttach(context);
        if (context instanceof OnHomeFragmentInteractionListener) {
            mListener = (OnHomeFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString() + " must implement  OnHomeFragmentInteractionListener");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        mapViews(view);
        setupRecyclerView();
        setupPieChart();
        setupListeners();

        swipeRefreshLayout.setOnRefreshListener(this::fetchHomeData);
        fetchHomeData();

        return view;
    }

    // ...
    private void setupListeners() {
        btnReports.setOnClickListener(v -> {
            if (mListener  != null) {
                // 2. Nhờ Activity cha xử lý việc chuyển fragment
                mListener.onReportsButtonClicked();
            }
        });
    }

    private void mapViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        tvCurrentBalanceAmount = view.findViewById(R.id.tvCurrentBalanceAmount);
        tvOverviewIncome = view.findViewById(R.id.tvOverviewIncome);
        tvOverviewExpense = view.findViewById(R.id.tvOverviewExpense);
        pieChart = view.findViewById(R.id.pieChart);
        recyclerRecentTransactions = view.findViewById(R.id.recyclerRecentTransactions);
        btnInsight = view.findViewById(R.id.btnInsight);
        btnReports = view.findViewById(R.id.btnReports);
        emptyStatePieChart = view.findViewById(R.id.emptyStatePieChart);
        emptyStateRecent = view.findViewById(R.id.emptyStateRecent);
    }

    private void setupRecyclerView() {
        // SỬA LẠI: Ghi đè LayoutManager để RecyclerView không tự cuộn
        recyclerRecentTransactions.setLayoutManager(new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                // Trả về false để vô hiệu hóa việc cuộn của RecyclerView
                return false;
            }
        });
        // Thuộc tính này vẫn cần thiết để phối hợp với ScrollView cha
        recyclerRecentTransactions.setNestedScrollingEnabled(false);
        recentTransactionAdapter = new RecentTransactionAdapter(new ArrayList<>());
        recyclerRecentTransactions.setAdapter(recentTransactionAdapter);
    }

    // ... (Toàn bộ các phương thức còn lại của HomeFragment.java không thay đổi)

    private void setupPieChart() {
        pieChart.setUsePercentValues(true);
        pieChart.getDescription().setEnabled(false);
        pieChart.setExtraOffsets(5, 10, 5, 5);
        pieChart.setDragDecelerationFrictionCoef(0.95f);

        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleColor(Color.TRANSPARENT);
        pieChart.setHoleRadius(58f);
        pieChart.setTransparentCircleRadius(61f);

        pieChart.setEntryLabelColor(Color.BLACK);

        pieChart.setRotationAngle(0);
        pieChart.setRotationEnabled(true);
        pieChart.setHighlightPerTapEnabled(true);

        Legend l = pieChart.getLegend();
        l.setEnabled(false);
    }

    private void fetchHomeData() {
        swipeRefreshLayout.setRefreshing(true);
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            Log.e(TAG, "UserID is empty. Cannot fetch data.");
            return;
        }

        Log.d(TAG, "Fetching home data for UserID: " + userId);

        ReportRepository.getReportService(getContext()).getHomePageReport(userId)
                .enqueue(new Callback<BaseResponse<HomePageReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<HomePageReportResponse>> call, Response<BaseResponse<HomePageReportResponse>> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            HomePageReportResponse data = response.body().getPayload();
                            if (data != null) {
                                Log.d(TAG, "API Success. Balance: " + data.getBalance());
                                updateHeader(data);
                                updatePieChart(data.getExpenseByCategoryPercentage());

                                if (data.getRecentTransactions() != null && !data.getRecentTransactions().isEmpty()) {
                                    recyclerRecentTransactions.setVisibility(View.VISIBLE);
                                    emptyStateRecent.setVisibility(View.GONE);
                                    recentTransactionAdapter.updateData(data.getRecentTransactions());
                                } else {
                                    recyclerRecentTransactions.setVisibility(View.GONE);
                                    emptyStateRecent.setVisibility(View.VISIBLE);
                                    recentTransactionAdapter.updateData(new ArrayList<>()); // Xóa dữ liệu cũ
                                }
                            } else {
                                Log.w(TAG, "Payload is null.");
                            }
                        } else {
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown API error";
                                Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<HomePageReportResponse>> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "Network Failure", t);
                        Toast.makeText(getContext(), "Network Error. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateHeader(HomePageReportResponse data) {
        tvCurrentBalanceAmount.setText(formatCurrency(data.getBalance()));
        tvOverviewIncome.setText(formatCurrency(data.getTotalIncome()));
        tvOverviewExpense.setText(formatCurrency(data.getTotalExpense()));
    }

    private void updatePieChart(Map<String, Double> expenseData) {
        if (expenseData == null || expenseData.isEmpty()) {
            pieChart.setVisibility(View.GONE);
            emptyStatePieChart.setVisibility(View.VISIBLE);
            return;
        }
        pieChart.setVisibility(View.VISIBLE);
        emptyStatePieChart.setVisibility(View.GONE);

        ArrayList<PieEntry> entries = new ArrayList<>();
        for (Map.Entry<String, Double> entry : expenseData.entrySet()) {
            float percentage = (float) Math.abs(entry.getValue());
            entries.add(new PieEntry(percentage, entry.getKey()));
        }

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);

        dataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(pieChart));
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.BLACK);


        pieChart.setData(data);
        pieChart.invalidate(); // refresh chart
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        return formatter.format(amount);
    }
}
