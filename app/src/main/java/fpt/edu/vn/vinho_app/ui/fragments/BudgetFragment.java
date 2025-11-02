package fpt.edu.vn.vinho_app.ui.fragments;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.CreateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.GetPagedBudgetsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.category.GetPagedCategoriesRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.BudgetOverviewResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.repository.BudgetRepository;
import fpt.edu.vn.vinho_app.data.repository.CategoryRepository;
import fpt.edu.vn.vinho_app.ui.adapter.BudgetAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment {
    private RecyclerView recyclerCategories;
    private BudgetAdapter adapter;
    private LinearLayout layoutEmptyState;
    private TextView tvLimitAmount, tvSpentAmount; // Add TextViews for overview

    private Button btnAddCategory;
    private SharedPreferences sharedPreferences;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();
    private String selectedMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        // Initialize views
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);
        tvLimitAmount = view.findViewById(R.id.tvLimitAmount);
        tvSpentAmount = view.findViewById(R.id.tvSpentAmount);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        // You will need to adjust your BudgetAdapter to accept a List<CategoryOverview>
        // Or create a new adapter. For this example, I'll assume it can be adapted.
        adapter = new BudgetAdapter(new ArrayList<>());
        recyclerCategories.setAdapter(adapter);

        // Fetch data
        fetchCategoriesForDialog(); // Renamed to avoid confusion
        fetchBudgetOverview();

        btnAddCategory.setOnClickListener(v -> showAddBudgetDialog());

        return view;
    }

    // This is a helper method to format currency
    private String formatCurrency(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###đ");
        return formatter.format(amount);
    }

    private void fetchBudgetOverview() {
        GetPagedBudgetsRequest request = new GetPagedBudgetsRequest();
        String userId = sharedPreferences.getString("userId", "");
        request.setUserId(userId);

        BudgetRepository.getBudgetService(getContext()).getBudgetOverview(request).enqueue(new Callback<BaseResponse<BudgetOverviewResponse>>() {
            @Override
            public void onResponse(Call<BaseResponse<BudgetOverviewResponse>> call, Response<BaseResponse<BudgetOverviewResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    BudgetOverviewResponse overview = response.body().getPayload();

                    if (overview != null && overview.getCategoryOverviews() != null && !overview.getCategoryOverviews().isEmpty()) {
                        // Update overview card
                        tvLimitAmount.setText(formatCurrency(overview.getTotalBudgetedAmount()));
                        tvSpentAmount.setText(formatCurrency(overview.getTotalSpentAmount()));

                        // Update RecyclerView
                        layoutEmptyState.setVisibility(View.GONE);
                        recyclerCategories.setVisibility(View.VISIBLE);
                        adapter.updateData(overview.getCategoryOverviews()); // You need to implement this method in your adapter

                    } else {
                        // Show empty state
                        tvLimitAmount.setText("0đ");
                        tvSpentAmount.setText("0đ");
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        recyclerCategories.setVisibility(View.GONE);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch budget overview", Toast.LENGTH_SHORT).show();
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerCategories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<BudgetOverviewResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerCategories.setVisibility(View.GONE);
            }
        });
    }

    private void createBudget(String categoryId, double limitAmount, String month) {
        String userId = sharedPreferences.getString("userId", "");
        CreateBudgetRequest request = new CreateBudgetRequest(userId, categoryId, limitAmount, month);
        BudgetRepository.getBudgetService(getContext()).createBudget(request).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Budget created successfully!", Toast.LENGTH_SHORT).show();
                    fetchBudgetOverview(); // Refresh the overview and list
                } else {
                    Toast.makeText(getContext(), "Failed to create budget", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Renamed this method
    private void fetchCategoriesForDialog() {
        String userId = sharedPreferences.getString("userId", "");
        GetPagedCategoriesRequest request = new GetPagedCategoriesRequest(userId);

        CategoryRepository.getCategoryService(getContext()).getCategories(request).enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                // Your logic to handle a successful response goes here.
                // You will now work with a 'response' object containing a 'PagedResponse<GetCategoryResponse>>'.
                if (response.isSuccessful()) {
                    PagedResponse<GetCategoryResponse> pagedResponse = response.body();
                    if (pagedResponse != null) {
                        // Access your data from the pagedResponse object
                    }
                } else {
                    // Handle API error (e.g., 404, 500)
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {
                // Your logic to handle a network failure (e.g., no internet) goes here.
            }
        });
    }

    private void showMonthPickerDialog() {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    selectedMonth = year1 + "-" + String.format(Locale.getDefault(), "%02d", monthOfYear + 1) + "-01";
                }, year, month, 1);
        datePickerDialog.show();
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_budget, null);
        builder.setView(dialogView);

        AutoCompleteTextView actvDialogCategory = dialogView.findViewById(R.id.actvCategory);
        EditText etDialogLimitAmount = dialogView.findViewById(R.id.etLimitAmount);
        Button btnDialogMonthPicker = dialogView.findViewById(R.id.btnMonthPicker);

        // Populate category dropdown
        List<String> categoryNames = new ArrayList<>();
        for (GetCategoryResponse category : categoryList) {
            categoryNames.add(category.getName());
        }
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        actvDialogCategory.setAdapter(categoryAdapter);

        btnDialogMonthPicker.setOnClickListener(v -> showMonthPickerDialog());

        builder.setTitle("Add Budget")
                .setPositiveButton("Add", (dialog, which) -> {
                    String categoryName = actvDialogCategory.getText().toString();
                    String categoryId = null;
                    for (GetCategoryResponse category : categoryList) {
                        if (category.getName().equals(categoryName)) {
                            categoryId = category.getId();
                            break;
                        }
                    }

                    if (categoryId == null) {
                        Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedMonth == null || selectedMonth.isEmpty()) {
                        Toast.makeText(getContext(), "Please select a month", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    double limitAmount = Double.parseDouble(etDialogLimitAmount.getText().toString());

                    createBudget(categoryId, limitAmount, selectedMonth);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
}
