package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log; // Import Log để debug
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionApiResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionItem;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionSummaryResponse;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import fpt.edu.vn.vinho_app.ui.adapter.SummaryCardAdapter;
import fpt.edu.vn.vinho_app.ui.adapter.TransactionAdapter;
import fpt.edu.vn.vinho_app.ui.model.DateHeaderItem;
import fpt.edu.vn.vinho_app.ui.model.DisplayableItem;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "TransactionFragment"; // Thêm TAG để debug

    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText etSearch;
    private Button btnAll, btnIncome, btnExpense;
    private RecyclerView recyclerViewTransactions;
    private TransactionAdapter transactionListAdapter;
    private SummaryCardAdapter summaryCardAdapter;
    private ConcatAdapter concatAdapter;
    private SharedPreferences sharedPreferences;

    private String currentFilterType = null;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // Ánh xạ views
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        etSearch = view.findViewById(R.id.etSearch);
        btnAll = view.findViewById(R.id.btnAll);
        btnIncome = view.findViewById(R.id.btnIncome);
        btnExpense = view.findViewById(R.id.btnExpense);
        recyclerViewTransactions = view.findViewById(R.id.recyclerViewTransactions);

        // Thiết lập RecyclerView
        setupRecyclerView();
        setupListeners();

        // Tải dữ liệu lần đầu
        updateButtonStyles(btnAll);
        fetchTransactions();

        return view;
    }

    private void setupRecyclerView() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        summaryCardAdapter = new SummaryCardAdapter();
        transactionListAdapter = new TransactionAdapter(new ArrayList<>());
        concatAdapter = new ConcatAdapter(summaryCardAdapter, transactionListAdapter);
        recyclerViewTransactions.setAdapter(concatAdapter);
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchTransactions);
        btnAll.setOnClickListener(this);
        btnIncome.setOnClickListener(this);
        btnExpense.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAll) {
            currentFilterType = null;
            updateButtonStyles(btnAll);
        } else if (id == R.id.btnIncome) {
            currentFilterType = "Income";
            updateButtonStyles(btnIncome);
        } else if (id == R.id.btnExpense) {
            currentFilterType = "Expense";
            updateButtonStyles(btnExpense);
        }
        fetchTransactions();
    }

    private void updateButtonStyles(Button selectedButton) {
        btnAll.setBackgroundResource(R.drawable.bg_filter_button_unselected);
        btnIncome.setBackgroundResource(R.drawable.bg_filter_button_unselected);
        btnExpense.setBackgroundResource(R.drawable.bg_filter_button_unselected);
        btnAll.setTextColor(Color.WHITE);
        btnIncome.setTextColor(Color.WHITE);
        btnExpense.setTextColor(Color.WHITE);
        selectedButton.setBackgroundResource(R.drawable.bg_filter_button_selected);
    }

    private void fetchTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        String userId = sharedPreferences.getString("userId", "");

        if (userId.isEmpty()) {
            swipeRefreshLayout.setRefreshing(false);
            Toast.makeText(getContext(), "User ID not found. Please log in again.", Toast.LENGTH_SHORT).show();
            return;
        }

        TransactionRepository.getTransactionService(getContext())
                .getTransactions(userId, currentFilterType)
                .enqueue(new Callback<TransactionApiResponse>() {
                    @Override
                    public void onResponse(Call<TransactionApiResponse> call, Response<TransactionApiResponse> response) {
                        swipeRefreshLayout.setRefreshing(false);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<TransactionSummaryResponse> payload = response.body().getPayload();

                            if (payload != null && !payload.isEmpty()) {
                                TransactionSummaryResponse summary = payload.get(0);
                                summaryCardAdapter.setSummary(summary); // Cập nhật card tổng quan

                                List<TransactionItem> transactions = summary.getTransactions();
                                Log.d(TAG, "Transactions fetched: " + (transactions != null ? transactions.size() : "null"));

                                List<DisplayableItem> displayableItems = processAndGroupTransactions(transactions);
                                transactionListAdapter.updateData(displayableItems);
                                Log.d(TAG, "Adapter updated with " + displayableItems.size() + " items.");

                            } else {
                                handleEmptyOrError();
                                Log.d(TAG, "Payload is empty or null.");
                            }
                        } else {
                            handleEmptyOrError();
                            try {
                                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Unknown error";
                                Log.e(TAG, "API Error: " + response.code() + " - " + errorBody);
                            } catch (IOException e) {
                                Log.e(TAG, "Error parsing error body", e);
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<TransactionApiResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        handleEmptyOrError();
                        Log.e(TAG, "Network Failure: " + t.getMessage(), t);
                        Toast.makeText(getContext(), "Network Error. Please check your connection.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleEmptyOrError() {
        summaryCardAdapter.setSummary(null);
        transactionListAdapter.updateData(new ArrayList<>());
    }

    private List<DisplayableItem> processAndGroupTransactions(List<TransactionItem> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }

        Collections.sort(transactions, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        Map<String, List<TransactionItem>> groupedMap = new LinkedHashMap<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
        SimpleDateFormat groupKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        for (TransactionItem transaction : transactions) {
            try {
                Date date = apiFormat.parse(transaction.getTransactionDate());
                if (date != null) {
                    String dayKey = groupKeyFormat.format(date);
                    groupedMap.computeIfAbsent(dayKey, k -> new ArrayList<>()).add(transaction);
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing transaction date: " + transaction.getTransactionDate(), e);
            }
        }

        List<DisplayableItem> displayableItems = new ArrayList<>();
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, MMM dd", Locale.getDefault());

        for (Map.Entry<String, List<TransactionItem>> entry : groupedMap.entrySet()) {
            try {
                Date date = groupKeyFormat.parse(entry.getKey());
                if (date != null) {
                    String displayDate = displayDateFormat.format(date);
                    displayableItems.add(new DateHeaderItem(displayDate, 0)); // Bỏ qua tính dailyTotal
                    displayableItems.addAll(entry.getValue());
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing group key date: " + entry.getKey(), e);
            }
        }
        return displayableItems;
    }

    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###.##đ");
        return formatter.format(amount);
    }
}
