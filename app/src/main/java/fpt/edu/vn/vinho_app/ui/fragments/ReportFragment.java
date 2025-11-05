package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.CategoryDetail;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.StatisticsReportResponse;
import fpt.edu.vn.vinho_app.data.repository.ReportRepository;
import fpt.edu.vn.vinho_app.ui.adapter.CategoryDetailAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportFragment extends Fragment {
    private static final String TAG = "ReportFragment";

    private SharedPreferences sharedPreferences;
    private ProgressBar progressBar;
    private NestedScrollView nestedScrollView;
    private MaterialButtonToggleGroup toggleGroup;
    private TextView tvTotalIncome, tvTotalExpense, tvSavings;
    private LineChart lineChart;
    private BarChart barChart;
    private View emptyStateLineChart, emptyStateBarChart, emptyStateCategoryDetails;
    private RecyclerView recyclerCategoryDetails;
    private CategoryDetailAdapter categoryDetailAdapter;
    private String currentRange = "Monthly"; // Mặc định

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // BƯỚC 1: Ánh xạ tất cả các view trước
        mapViews(view);

        // BƯỚC 2: Cấu hình cho các view
        setupCharts();
        setupRecyclerView();

        // BƯỚC 3: Thiết lập các listener và trạng thái ban đầu
        toggleGroup.check(R.id.btnMonthly);
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnDaily) currentRange = "Daily";
                else if (checkedId == R.id.btnWeekly) currentRange = "Weekly";
                else if (checkedId == R.id.btnMonthly) currentRange = "Monthly";
                fetchStatistics();
            }
        });

        // BƯỚC 4: Tải dữ liệu lần đầu
        fetchStatistics();

        return view;
    }

    private void mapViews(View view) {
        progressBar = view.findViewById(R.id.progressBar);
        nestedScrollView = view.findViewById(R.id.nestedScrollView);
        toggleGroup = view.findViewById(R.id.toggle_button_group);
        tvTotalIncome = view.findViewById(R.id.tvIncome);
        tvTotalExpense = view.findViewById(R.id.tvExpense);
        tvSavings = view.findViewById(R.id.tvSaving);
        lineChart = view.findViewById(R.id.lineChart);
        barChart = view.findViewById(R.id.barChart);
        recyclerCategoryDetails = view.findViewById(R.id.recyclerCategoryDetails);
        emptyStateLineChart = view.findViewById(R.id.emptyStateLineChart);
        emptyStateBarChart = view.findViewById(R.id.emptyStateBarChart);
        emptyStateCategoryDetails = view.findViewById(R.id.emptyStateCategoryDetails);
    }

    private void setupRecyclerView() {
        recyclerCategoryDetails.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerCategoryDetails.setNestedScrollingEnabled(false);
        categoryDetailAdapter = new CategoryDetailAdapter(new ArrayList<>());
        recyclerCategoryDetails.setAdapter(categoryDetailAdapter);
    }

    private void setupCharts() {
        // Cấu hình chung cho LineChart
        lineChart.getDescription().setEnabled(false);
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getAxisRight().setEnabled(false);
        lineChart.getAxisLeft().setAxisMinimum(0f); // Bắt đầu trục Y từ 0
        lineChart.getXAxis().setGranularity(1f); // Đảm bảo các nhãn không bị dính vào nhau

        // Cấu hình chung cho BarChart
        barChart.getDescription().setEnabled(false);
        barChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        barChart.getAxisRight().setEnabled(false);
        barChart.getAxisLeft().setAxisMinimum(0f);
        barChart.getXAxis().setGranularity(1f);
        barChart.setFitBars(true); // Làm cho các cột vừa vặn hơn
    }

    private void fetchStatistics() {
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) {
            Log.e(TAG, "UserID is empty. Cannot fetch statistics.");
            return;
        }

        Log.d(TAG, "Fetching statistics for UserID: " + userId + " with Range: " + currentRange);
        showLoading(true);


        ReportRepository.getReportService(getContext()).getStatisticsReport(userId, currentRange)
                .enqueue(new Callback<BaseResponse<StatisticsReportResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<StatisticsReportResponse>> call, Response<BaseResponse<StatisticsReportResponse>> response) {
                        showLoading(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            StatisticsReportResponse data = response.body().getPayload();
                            if (data != null) {
                                Log.d(TAG, "API Success. Savings: " + data.getSavings());
                                updateUI(data);
                            } else {
                                Log.w(TAG, "API Success but payload is null.");
                                // Có thể hiển thị trạng thái lỗi/rỗng ở đây
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
                    public void onFailure(Call<BaseResponse<StatisticsReportResponse>> call, Throwable t) {
                        showLoading(false);
                        Log.e(TAG, "Network Failure", t);
                        Toast.makeText(getContext(), "Network Failure. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateUI(StatisticsReportResponse data) {
        // Update summary cards
        tvTotalIncome.setText(formatCurrency(data.getTotalIncome()));
        tvTotalExpense.setText(formatCurrency(data.getTotalExpense()));
        tvSavings.setText(formatCurrency(data.getSavings()));

        // Update Line Chart (Expense & Income Trend)
        updateLineChart(data.getLabels(), data.getIncomeSeries(), data.getExpenseSeries());

        // Update Bar Chart (Spending by Category)
        updateBarChart(data.getSpendingByCategory());

        // Update Category Details RecyclerView
        if (data.getCategoryDetails() != null && !data.getCategoryDetails().isEmpty()) {
            recyclerCategoryDetails.setVisibility(View.VISIBLE);
            emptyStateCategoryDetails.setVisibility(View.GONE);
            categoryDetailAdapter.updateData(data.getCategoryDetails());
        } else {
            recyclerCategoryDetails.setVisibility(View.GONE);
            emptyStateCategoryDetails.setVisibility(View.VISIBLE);
            categoryDetailAdapter.updateData(new ArrayList<>());
        }
    }

    private void updateLineChart(List<String> labels, List<Double> incomeData, List<Double> expenseData) {
        if (labels == null || labels.isEmpty()) {
            lineChart.setVisibility(View.GONE);
            emptyStateLineChart.setVisibility(View.VISIBLE);
            return;
        }
        lineChart.setVisibility(View.VISIBLE);
        emptyStateLineChart.setVisibility(View.GONE);
        ArrayList<Entry> incomeEntries = new ArrayList<>();
        ArrayList<Entry> expenseEntries = new ArrayList<>();

        for (int i = 0; i < labels.size(); i++) {
            if (incomeData != null && i < incomeData.size()) {
                incomeEntries.add(new Entry(i, incomeData.get(i).floatValue()));
            }
            if (expenseData != null && i < expenseData.size()) {
                // Lấy giá trị tuyệt đối vì đây là biểu đồ chi tiêu
                expenseEntries.add(new Entry(i, Math.abs(expenseData.get(i).floatValue())));
            }
        }

        LineDataSet incomeDataSet = new LineDataSet(incomeEntries, "Income");
        incomeDataSet.setColor(Color.parseColor("#4CAF50")); // Xanh lá
        incomeDataSet.setCircleColor(Color.parseColor("#4CAF50"));
        incomeDataSet.setLineWidth(2f);
        incomeDataSet.setCircleRadius(4f);
        incomeDataSet.setDrawCircleHole(false);

        LineDataSet expenseDataSet = new LineDataSet(expenseEntries, "Expense");
        expenseDataSet.setColor(Color.parseColor("#F44336")); // Đỏ
        expenseDataSet.setCircleColor(Color.parseColor("#F44336"));
        expenseDataSet.setLineWidth(2f);
        expenseDataSet.setCircleRadius(4f);
        expenseDataSet.setDrawCircleHole(false);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(incomeDataSet);
        dataSets.add(expenseDataSet);

        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        lineChart.setData(new LineData(dataSets));
        lineChart.invalidate(); // Refresh biểu đồ
    }

    private void updateBarChart(Map<String, Double> spendingData) {
        if (spendingData == null || spendingData.isEmpty()) {
            barChart.setVisibility(View.GONE);
            emptyStateBarChart.setVisibility(View.VISIBLE);
            return;
        }
        barChart.setVisibility(View.VISIBLE);
        emptyStateBarChart.setVisibility(View.GONE);

        ArrayList<BarEntry> entries = new ArrayList<>();
        ArrayList<String> labels = new ArrayList<>(spendingData.keySet());
        for (int i = 0; i < labels.size(); i++) {
            String category = labels.get(i);
            // Lấy giá trị tuyệt đối vì đây là biểu đồ chi tiêu
            float amount = Math.abs(spendingData.get(category).floatValue());
            entries.add(new BarEntry(i, amount));
        }

        BarDataSet dataSet = new BarDataSet(entries, "Spending");
        dataSet.setColors(ColorTemplate.MATERIAL_COLORS); // Sử dụng bộ màu có sẵn
        dataSet.setValueTextColor(Color.BLACK);
        dataSet.setValueTextSize(12f);

        BarData barData = new BarData(dataSet);
        barData.setBarWidth(0.6f); // Chỉnh độ rộng của cột

        barChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        barChart.setData(barData);
        barChart.invalidate(); // Refresh biểu đồ
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
    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        return formatter.format(amount);
    }
}
