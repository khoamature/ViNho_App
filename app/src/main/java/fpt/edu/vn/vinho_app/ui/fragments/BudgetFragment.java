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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
    private AutoCompleteTextView actvCategory, actvCategoryType;
    private EditText etMinAmount, etMaxAmount;
    private Button btnFromMonth, btnToMonth, btnApplyFilters, btnAddCategory;

    private SharedPreferences sharedPreferences;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();
    private String selectedMonth;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_budget, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext()));
        layoutEmptyState = view.findViewById(R.id.layoutEmptyState);

        actvCategory = view.findViewById(R.id.actvCategory);
        actvCategoryType = view.findViewById(R.id.actvCategoryType);
        etMinAmount = view.findViewById(R.id.etMinAmount);
        etMaxAmount = view.findViewById(R.id.etMaxAmount);
        btnFromMonth = view.findViewById(R.id.btnFromMonth);
        btnToMonth = view.findViewById(R.id.btnToMonth);
        btnApplyFilters = view.findViewById(R.id.btnApplyFilters);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        adapter = new BudgetAdapter(new ArrayList<>());
        recyclerCategories.setAdapter(adapter);

        setupFilters();
        fetchCategories();

        btnApplyFilters.setOnClickListener(v -> fetchBudgets());
        btnAddCategory.setOnClickListener(v -> showAddBudgetDialog());

        fetchBudgets();

        return view;
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
                    double limitAmount = Double.parseDouble(etDialogLimitAmount.getText().toString());

                    createBudget(categoryId, limitAmount, selectedMonth);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }

    private void createBudget(String categoryId, double limitAmount, String month) {
        String userId = sharedPreferences.getString("userId", "");
        CreateBudgetRequest request = new CreateBudgetRequest(userId, categoryId, limitAmount, month);
        BudgetRepository.getBudgetService(getContext()).createBudget(request).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Budget created successfully!", Toast.LENGTH_SHORT).show();
                    fetchBudgets(); // Refresh the list
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


    private void setupFilters() {
        // Category Type Filter
        List<String> categoryTypes = new ArrayList<>();
        categoryTypes.add("Expense");
        categoryTypes.add("Income");
        ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryTypes);
        actvCategoryType.setAdapter(categoryTypeAdapter);
        actvCategoryType.setText(categoryTypes.get(0), false); // Default to Expense

        actvCategoryType.setOnItemClickListener((parent, view, position, id) -> {
            fetchCategories(); // Reload categories when type changes
        });

        // Category Filter
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        actvCategory.setAdapter(categoryAdapter);
    }

    private void fetchCategories() {
        String userId = sharedPreferences.getString("userId", "");
        GetPagedCategoriesRequest request = new GetPagedCategoriesRequest(userId);

        String categoryTypeStr = actvCategoryType.getText().toString();
        if (categoryTypeStr.equals("Expense")) {
            request.setCategoryType(0);
        } else if (categoryTypeStr.equals("Income")) {
            request.setCategoryType(1);
        }

        CategoryRepository.getCategoryService(getContext()).getCategories(request).enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getPayload());

                    List<String> categoryNames = new ArrayList<>();
                    categoryNames.add("All Categories");
                    for (GetCategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                    actvCategory.setAdapter(categoryAdapter);
                    if (!categoryNames.isEmpty()) {
                        actvCategory.setText(categoryNames.get(0), false);
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void fetchBudgets() {
        GetPagedBudgetsRequest request = new GetPagedBudgetsRequest();
        String userId = sharedPreferences.getString("userId", "");
        request.setUserId(userId);

        // Populate request from filter views
        String categoryName = actvCategory.getText().toString();
        if (!categoryName.isEmpty() && !categoryName.equals("All Categories")) {
            for (GetCategoryResponse category : categoryList) {
                if (category.getName().equals(categoryName)) {
                    request.setCategoryId(category.getId());
                    break;
                }
            }
        }

        request.setCategoryType(actvCategoryType.getText().toString());

        try {
            if (!etMinAmount.getText().toString().isEmpty())
                request.setStartRangeAmount(Double.parseDouble(etMinAmount.getText().toString()));
        } catch (NumberFormatException e) {
            request.setStartRangeAmount(null);
        }
        try {
            if (!etMaxAmount.getText().toString().isEmpty())
                request.setEndRangeAmount(Double.parseDouble(etMaxAmount.getText().toString()));
        } catch (NumberFormatException e) {
            request.setEndRangeAmount(null);
        }

        BudgetRepository.getBudgetService(getContext()).getBudgets(request).enqueue(new Callback<PagedResponse<GetBudgetResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetBudgetResponse>> call, Response<PagedResponse<GetBudgetResponse>> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPayload() != null) {
                    if (response.body().getPayload().isEmpty()) {
                        layoutEmptyState.setVisibility(View.VISIBLE);
                        recyclerCategories.setVisibility(View.GONE);
                    } else {
                        layoutEmptyState.setVisibility(View.GONE);
                        recyclerCategories.setVisibility(View.VISIBLE);
                        adapter.updateData(response.body().getPayload());
                    }
                } else {
                    layoutEmptyState.setVisibility(View.VISIBLE);
                    recyclerCategories.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetBudgetResponse>> call, Throwable t) {
                layoutEmptyState.setVisibility(View.VISIBLE);
                recyclerCategories.setVisibility(View.GONE);
            }
        });
    }
}
