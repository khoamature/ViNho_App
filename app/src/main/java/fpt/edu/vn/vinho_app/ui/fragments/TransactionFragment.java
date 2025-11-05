package fpt.edu.vn.vinho_app.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ConcatAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.UpdateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionApiResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionItem;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionSummaryResponse;
import fpt.edu.vn.vinho_app.data.repository.CategoryRepository;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import fpt.edu.vn.vinho_app.ui.adapter.SummaryCardAdapter;
import fpt.edu.vn.vinho_app.ui.adapter.TransactionAdapter;
import fpt.edu.vn.vinho_app.ui.model.DateHeaderItem;
import fpt.edu.vn.vinho_app.ui.model.DisplayableItem;
import fpt.edu.vn.vinho_app.ui.viewmodel.SharedViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment implements View.OnClickListener, TransactionAdapter.OnTransactionActionsListener {
    private static final String TAG = "TransactionFragment";

    // Views
    private SharedViewModel sharedViewModel;
    private SwipeRefreshLayout swipeRefreshLayout;
    private LinearLayout layoutEmptyState;
    private ImageView btnPreviousMonth, btnNextMonth;
    private TextView tvCurrentMonth;
    private EditText etSearch;
    private Button btnAll, btnIncome, btnExpense, btnFilter;
    private LinearLayout layoutCategoryFilter;
    private AutoCompleteTextView autoCompleteCategory;
    private RecyclerView recyclerViewTransactions;

    // Adapters
    private TransactionAdapter transactionListAdapter;
    private SummaryCardAdapter summaryCardAdapter;
    private ConcatAdapter concatAdapter;

    // Data & State
    private SharedPreferences sharedPreferences;
    private String currentFilterType = null;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();
    private String selectedCategoryId = null;

    private Calendar currentCalendar;

    // Debouncing for search
    private final Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getTransactionAddedEvent().observe(getViewLifecycleOwner(), isAdded -> {
            // isAdded là giá trị boolean từ LiveData (true)
            if (isAdded != null && isAdded) {
                Log.d(TAG, "Received transaction added event. Refreshing...");
                fetchTransactions();
                // Reset lại sự kiện để không bị gọi lại khi quay lại Fragment
                sharedViewModel.onTransactionEventHandled();
            }
        });
        currentCalendar = Calendar.getInstance();

        mapViews(view);
        setupRecyclerView();
        setupListeners();
        setupCategoryFilterListener();

        // Initial data load
        updateButtonStyles(btnAll);
        updateMonthDisplay();
        fetchCategoriesAndThenTransactions(null);

        return view;
    }

    private void mapViews(View view) {
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
        etSearch = view.findViewById(R.id.etSearch);
        btnAll = view.findViewById(R.id.btnAll);
        btnIncome = view.findViewById(R.id.btnIncome);
        btnExpense = view.findViewById(R.id.btnExpense);
        recyclerViewTransactions = view.findViewById(R.id.recyclerViewTransactions);
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        btnFilter = view.findViewById(R.id.btnFilter);
        layoutCategoryFilter = view.findViewById(R.id.layoutCategoryFilter);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        btnPreviousMonth = view.findViewById(R.id.btnPreviousMonth);
        btnNextMonth = view.findViewById(R.id.btnNextMonth);
        tvCurrentMonth = view.findViewById(R.id.tvCurrentMonth);
    }

    private void setupRecyclerView() {
        recyclerViewTransactions.setLayoutManager(new LinearLayoutManager(getContext()));
        summaryCardAdapter = new SummaryCardAdapter();
        // Truyền "this" vào constructor để đăng ký listener
        transactionListAdapter = new TransactionAdapter(new ArrayList<>(), this);
        concatAdapter = new ConcatAdapter(summaryCardAdapter, transactionListAdapter);
        recyclerViewTransactions.setAdapter(concatAdapter);
    }

    private void setupListeners() {
        swipeRefreshLayout.setOnRefreshListener(this::fetchTransactions);
        btnAll.setOnClickListener(this);
        btnIncome.setOnClickListener(this);
        btnExpense.setOnClickListener(this);
        btnFilter.setOnClickListener(this);
        btnPreviousMonth.setOnClickListener(this);
        btnNextMonth.setOnClickListener(this);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (searchRunnable != null) searchHandler.removeCallbacks(searchRunnable);
            }
            @Override
            public void afterTextChanged(Editable s) {
                searchRunnable = () -> fetchTransactions();
                searchHandler.postDelayed(searchRunnable, 500); // 500ms delay
            }
        });
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAll || id == R.id.btnIncome || id == R.id.btnExpense) {
            if (id == R.id.btnAll) currentFilterType = null;
            else if (id == R.id.btnIncome) currentFilterType = "Income";
            else if (id == R.id.btnExpense) currentFilterType = "Expense";
            updateButtonStyles((Button) v);
            fetchCategoriesAndThenTransactions(currentFilterType);
        } else if (id == R.id.btnFilter) {
            layoutCategoryFilter.setVisibility(layoutCategoryFilter.getVisibility() == View.GONE ? View.VISIBLE : View.GONE);
        } else if (id == R.id.btnPreviousMonth) {
            currentCalendar.add(Calendar.MONTH, -1); // Lùi 1 tháng
            updateMonthDisplay();
            fetchTransactions();
        } else if (id == R.id.btnNextMonth) {
            currentCalendar.add(Calendar.MONTH, 1); // Tới 1 tháng
            updateMonthDisplay();
            fetchTransactions();
        }
    }
    private void updateMonthDisplay() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.ENGLISH);
        tvCurrentMonth.setText(dateFormat.format(currentCalendar.getTime()));
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

    private void fetchCategoriesAndThenTransactions(String type) {
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) return;

        Log.d(TAG, "Step 1: Fetching categories for type: " + type);
        handleEmptyOrError();

        CategoryRepository.getCategoryService(getContext()).getCategories(type, userId)
                .enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryList.clear();
                            categoryList.addAll(response.body().getPayload());

                            List<String> categoryNames = new ArrayList<>();
                            categoryNames.add("All");
                            for (GetCategoryResponse category : categoryList) {
                                categoryNames.add(category.getName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                            autoCompleteCategory.setAdapter(adapter);

                            autoCompleteCategory.setText("All", false);
                            selectedCategoryId = null;

                            Log.d(TAG, "Step 2: Categories fetched. Now fetching transactions...");
                            fetchTransactions();
                        } else {
                            Log.e(TAG, "Failed to fetch categories. Code: " + response.code());
                            fetchTransactions();
                        }
                    }
                    @Override
                    public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {
                        Log.e(TAG, "Failure fetching categories", t);
                        fetchTransactions();
                    }
                });
    }

    private void setupCategoryFilterListener() {
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            if ("All".equals(selectedName)) {
                selectedCategoryId = null;
            } else {
                for (GetCategoryResponse category : categoryList) {
                    if (category.getName().equals(selectedName)) {
                        selectedCategoryId = category.getId();
                        break;
                    }
                }
            }
            fetchTransactions();
        });
    }

    public void fetchTransactionsPublic() {
        Log.d(TAG, "Refreshing transactions from MainActivity...");
        fetchTransactions();
    }

    private void fetchTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) { swipeRefreshLayout.setRefreshing(false); return; }

        String searchQuery = etSearch.getText().toString().trim();
        if (searchQuery.isEmpty()) searchQuery = null;
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Calendar tempCal = (Calendar) currentCalendar.clone();
        tempCal.set(Calendar.DAY_OF_MONTH, 1);
        String fromDate = apiDateFormat.format(tempCal.getTime());

        tempCal.set(Calendar.DAY_OF_MONTH, tempCal.getActualMaximum(Calendar.DAY_OF_MONTH));
        String toDate = apiDateFormat.format(tempCal.getTime());

        // Đặt PageSize lớn để lấy tất cả giao dịch trong tháng
        Integer pageSize = 1000;

        Log.d(TAG, "Step 3: Fetching transactions with Type: " + currentFilterType + ", CategoryID: " + selectedCategoryId + ", SearchQuery: " + searchQuery);
        TransactionRepository.getTransactionService(getContext())
                .getTransactions(userId, currentFilterType, selectedCategoryId, searchQuery, fromDate, toDate, pageSize)
                .enqueue(new Callback<TransactionApiResponse>() {
                    @Override
                    public void onResponse(Call<TransactionApiResponse> call, Response<TransactionApiResponse> response) {
                        swipeRefreshLayout.setRefreshing(false);
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            List<TransactionSummaryResponse> payload = response.body().getPayload();
                            if (payload != null && !payload.isEmpty() && payload.get(0).getTransactions() != null && !payload.get(0).getTransactions().isEmpty()) {
                                recyclerViewTransactions.setVisibility(View.VISIBLE);
                                layoutEmptyState.setVisibility(View.GONE);
                                TransactionSummaryResponse summary = payload.get(0);
                                summaryCardAdapter.setSummary(summary);
                                List<DisplayableItem> displayableItems = processAndGroupTransactions(summary.getTransactions());
                                transactionListAdapter.updateData(displayableItems);
                                Log.d(TAG, "Step 4: Success! Adapter updated with " + displayableItems.size() + " items.");
                            } else {
                                Log.d(TAG, "Step 4: API Success but no transactions found.");
                                handleEmptyOrError();
                            }
                        } else {
                            Log.e(TAG, "Step 4: API Error. Code: " + response.code());
                            handleEmptyOrError();
                        }
                    }
                    @Override
                    public void onFailure(Call<TransactionApiResponse> call, Throwable t) {
                        swipeRefreshLayout.setRefreshing(false);
                        Log.e(TAG, "Step 4: Network Failure.", t);
                        handleEmptyOrError();
                    }
                });
    }

    private void handleEmptyOrError() {
        summaryCardAdapter.setSummary(null);
        transactionListAdapter.updateData(new ArrayList<>());
        recyclerViewTransactions.setVisibility(View.GONE);
        layoutEmptyState.setVisibility(View.VISIBLE);
    }

    private List<DisplayableItem> processAndGroupTransactions(List<TransactionItem> transactions) {
        if (transactions == null || transactions.isEmpty()) {
            return new ArrayList<>();
        }
        Collections.sort(transactions, (t1, t2) -> t2.getTransactionDate().compareTo(t1.getTransactionDate()));

        Map<String, List<TransactionItem>> groupedMap = new LinkedHashMap<>();
        SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        SimpleDateFormat groupKeyFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        groupKeyFormat.setTimeZone(Calendar.getInstance().getTimeZone());

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
        SimpleDateFormat displayDateFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy", Locale.ENGLISH);
        displayDateFormat.setTimeZone(Calendar.getInstance().getTimeZone());

        for (Map.Entry<String, List<TransactionItem>> entry : groupedMap.entrySet()) {
            try {
                Date date = groupKeyFormat.parse(entry.getKey());
                if (date != null) {
                    displayableItems.add(new DateHeaderItem(displayDateFormat.format(date), 0));
                    displayableItems.addAll(entry.getValue());
                }
            } catch (ParseException e) {
                Log.e(TAG, "Error parsing group key date: " + entry.getKey(), e);
            }
        }
        return displayableItems;
    }

    // --- LOGIC SỬA/XÓA ---
    @Override
    public void onEditTransaction(TransactionItem item) {
        showEditTransactionDialog(item);
    }
    @Override
    public void onDeleteTransaction(TransactionItem item) {
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Transaction")
                .setMessage("Are you sure you want to delete this transaction?")
                .setPositiveButton("Delete", (dialog, which) -> deleteTransaction(item.getId()))
                .setNegativeButton("Cancel", null)
                .show();
    }
    private void deleteTransaction(String transactionId) {
        TransactionRepository.getTransactionService(getContext()).deleteTransaction(transactionId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Transaction deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchTransactions();
                } else { Toast.makeText(getContext(), "Failed to delete transaction", Toast.LENGTH_SHORT).show(); }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }
    private void updateTransaction(String transactionId, UpdateTransactionRequest request) {
        TransactionRepository.getTransactionService(getContext()).updateTransaction(transactionId, request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Transaction updated successfully", Toast.LENGTH_SHORT).show();
                    fetchTransactions();
                } else { Toast.makeText(getContext(), "Failed to update transaction", Toast.LENGTH_SHORT).show(); }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) { Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show(); }
        });
    }

    private void showEditTransactionDialog(TransactionItem item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_transaction, null);
        builder.setView(dialogView).setTitle("Edit Transaction");

        MaterialButtonToggleGroup toggleGroup = dialogView.findViewById(R.id.toggleGroupType);
        AutoCompleteTextView autoCompleteCategory = dialogView.findViewById(R.id.autoCompleteCategory);
        EditText editAmount = dialogView.findViewById(R.id.editAmount);
        EditText editDescription = dialogView.findViewById(R.id.editDescription);
        Button btnDateTimePicker = dialogView.findViewById(R.id.btnDateTimePicker);

        final Calendar calendar = Calendar.getInstance();
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            calendar.setTime(sdf.parse(item.getTransactionDate()));
            btnDateTimePicker.setText(new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.getTime()));
        } catch (Exception e) { e.printStackTrace(); }

        editAmount.setText(String.valueOf(item.getAmount()));
        editDescription.setText(item.getDescription());

        btnDateTimePicker.setOnClickListener(v -> {
            new DatePickerDialog(getContext(), (dateView, year, month, day) -> {
                calendar.set(year, month, day);
                new TimePickerDialog(getContext(), (timeView, hour, minute) -> {
                    calendar.set(Calendar.HOUR_OF_DAY, hour);
                    calendar.set(Calendar.MINUTE, minute);
                    btnDateTimePicker.setText(new SimpleDateFormat("EEE, dd/MM/yyyy HH:mm", Locale.getDefault()).format(calendar.getTime()));
                }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        autoCompleteCategory.setAdapter(categoryAdapter);
        autoCompleteCategory.setText(item.getCategoryName());

        final List<GetCategoryResponse> dialogCategoryList = new ArrayList<>();
        final String[] selectedDialogCategoryId = {item.getCategoryId()};

        // Logic fetch category cho dialog
        Runnable fetchDialogCategories = () -> {
            String type = toggleGroup.getCheckedButtonId() == R.id.btnIncome ? "Income" : "Expense";
            CategoryRepository.getCategoryService(getContext()).getCategories(type, sharedPreferences.getString("userId", ""))
                    .enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
                        @Override
                        public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                dialogCategoryList.clear();
                                dialogCategoryList.addAll(response.body().getPayload());
                                List<String> names = new ArrayList<>();
                                for(GetCategoryResponse cat : dialogCategoryList) names.add(cat.getName());
                                ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, names);
                                autoCompleteCategory.setAdapter(adapter);
                            }
                        }
                        @Override public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {}
                    });
        };

        if ("Income".equalsIgnoreCase(item.getCategoryType())) toggleGroup.check(R.id.btnIncome);
        else toggleGroup.check(R.id.btnExpense);
        fetchDialogCategories.run();

        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                autoCompleteCategory.setText("");
                selectedDialogCategoryId[0] = null;
                fetchDialogCategories.run();
            }
        });

        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            String name = (String) parent.getItemAtPosition(position);
            for(GetCategoryResponse cat : dialogCategoryList) {
                if(cat.getName().equals(name)) {
                    selectedDialogCategoryId[0] = cat.getId();
                    break;
                }
            }
        });

        builder.setPositiveButton("Save", (dialog, which) -> {
            if (selectedDialogCategoryId[0] == null) {
                Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                return;
            }
            double newAmount = Double.parseDouble(editAmount.getText().toString());
            String newDescription = editDescription.getText().toString();
            SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
            String newDate = apiDateFormat.format(calendar.getTime());
            Log.d(TAG, "Updating transaction with date: " + newDate);

            UpdateTransactionRequest request = new UpdateTransactionRequest(selectedDialogCategoryId[0], newAmount, newDescription, newDate);
            updateTransaction(item.getId(), request);
        });

        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }
}
