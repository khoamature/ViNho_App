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

import com.google.android.material.button.MaterialButtonToggleGroup;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.CreateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.GetPagedBudgetsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.UpdateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.category.GetPagedCategoriesRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.BudgetOverviewResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.CategoryOverview;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.repository.BudgetRepository;
import fpt.edu.vn.vinho_app.data.repository.CategoryRepository;
import fpt.edu.vn.vinho_app.ui.adapter.BudgetAdapter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BudgetFragment extends Fragment implements BudgetAdapter.OnBudgetActionsListener{
    private RecyclerView recyclerCategories;
    private BudgetAdapter adapter;
    private LinearLayout layoutEmptyState;
    private TextView tvLimitAmount, tvSpentAmount; // Add TextViews for overview

    private Button btnAddCategory;
    private SharedPreferences sharedPreferences;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();
    private String selectedMonth;

    private String selectedMonthValue; // Dùng để lưu giá trị yyyy-MM-dd
    private String selectedCategoryType = "Expense"; // Mặc định là Expens

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
        adapter = new BudgetAdapter(new ArrayList<>(), this);
        recyclerCategories.setAdapter(adapter);

        // Fetch data
        fetchBudgetOverview();

        btnAddCategory.setOnClickListener(v -> showAddBudgetDialog());

        return view;
    }

    @Override
    public void onEditBudget(CategoryOverview budget) {
        if (budget.getBudgetId() == null) {
            Toast.makeText(getContext(), "Budget ID is missing", Toast.LENGTH_SHORT).show();
            return;
        }

        // 1. Gọi API để lấy thông tin chi tiết nhất của budget
        BudgetRepository.getBudgetService(getContext()).getBudgetById(budget.getBudgetId())
                .enqueue(new Callback<BaseResponse<GetBudgetResponse>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<GetBudgetResponse>> call, Response<BaseResponse<GetBudgetResponse>> response) {
                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            // 2. Sau khi có dữ liệu, mở dialog edit
                            showEditBudgetDialog(response.body().getPayload());
                        } else {
                            Toast.makeText(getContext(), "Failed to fetch budget details", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<GetBudgetResponse>> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
    @Override
    public void onDeleteBudget(CategoryOverview budget) {
        // Hiển thị dialog xác nhận xóa
        new AlertDialog.Builder(getContext())
                .setTitle("Delete Budget")
                .setMessage("Are you sure you want to delete the budget for '" + budget.getCategoryName() + "'?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    // SỬA LỖI Ở ĐÂY: Truyền budgetId thay vì categoryId
                    if (budget.getBudgetId() != null) {
                        deleteBudget(budget.getBudgetId());
                    } else {
                        Toast.makeText(getContext(), "Budget ID is missing for deletion", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    // SỬA LẠI: showEditBudgetDialog giờ sẽ nhận vào đối tượng GetBudgetResponse
    private void showEditBudgetDialog(GetBudgetResponse budgetDetails) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_budget, null);
        builder.setView(dialogView);
        builder.setTitle("Edit Budget");

        // Ánh xạ các view trong dialog
        MaterialButtonToggleGroup toggleGroup = dialogView.findViewById(R.id.toggleGroupType);
        AutoCompleteTextView actvDialogCategory = dialogView.findViewById(R.id.actvCategory);
        EditText etDialogLimitAmount = dialogView.findViewById(R.id.etLimitAmount);
        Button btnDialogMonthPicker = dialogView.findViewById(R.id.btnMonthPicker);

        // 3. Ẩn nút chọn tháng
        btnDialogMonthPicker.setVisibility(View.GONE);

        // 4. Điền dữ liệu có sẵn
        etDialogLimitAmount.setText(String.valueOf(budgetDetails.getLimitAmount()));
        actvDialogCategory.setText(budgetDetails.getCategoryName()); // Lấy từ API, chính xác hơn

        // 5. Logic xử lý chọn Type và Category động
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, new ArrayList<>());
        actvDialogCategory.setAdapter(categoryAdapter);

        // 6. Tự động chọn đúng nút Type dựa trên dữ liệu từ API
        String initialType = budgetDetails.getCategoryType() != null ? budgetDetails.getCategoryType() : "Expense";
        if ("Income".equalsIgnoreCase(initialType)) {
            toggleGroup.check(R.id.btnIncome);
        } else if ("Neutral".equalsIgnoreCase(initialType)) {
            toggleGroup.check(R.id.btnNeutral);
        } else {
            toggleGroup.check(R.id.btnExpense);
        }
        selectedCategoryType = initialType;
        // Tải danh sách category cho lần đầu tiên
        fetchCategoriesForDialog(selectedCategoryType, actvDialogCategory, categoryAdapter, true);

        // Thêm listener để tải lại category khi người dùng đổi Type
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnExpense) selectedCategoryType = "Expense";
                else if (checkedId == R.id.btnIncome) selectedCategoryType = "Income";
                else if (checkedId == R.id.btnNeutral) selectedCategoryType = "Neutral";
                fetchCategoriesForDialog(selectedCategoryType, actvDialogCategory, categoryAdapter, true);
            }
        });

        // Xử lý khi nhấn nút "Save"
        builder.setPositiveButton("Save", (dialog, which) -> {
            String newCategoryName = actvDialogCategory.getText().toString();
            String newCategoryId = null;

            // Tìm ID của category được chọn
            for (GetCategoryResponse category : categoryList) {
                if (category.getName().equals(newCategoryName)) {
                    newCategoryId = category.getId();
                    break;
                }
            }

            // Nếu người dùng không thay đổi category, giữ lại ID cũ
            if (newCategoryId == null) {
                newCategoryId = budgetDetails.getCategoryId();
            }

            double newLimit = Double.parseDouble(etDialogLimitAmount.getText().toString());

            // Gọi API cập nhật với ID của budget
            updateBudget(budgetDetails.getId(), newCategoryId, newLimit);
        });
        builder.setNegativeButton("Cancel", null);
        builder.create().show();
    }


    // 3. Các phương thức gọi API
    private void deleteBudget(String budgetId) {
        BudgetRepository.getBudgetService(getContext()).deleteBudget(budgetId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(getContext(), "Budget deleted successfully", Toast.LENGTH_SHORT).show();
                    fetchBudgetOverview(); // Làm mới lại danh sách
                } else {
                    Toast.makeText(getContext(), "Failed to delete budget", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateBudget(String budgetId, String newCategoryId, double newLimitAmount) {
        // Tạo một DTO mới cho request body của việc update
        UpdateBudgetRequest request = new UpdateBudgetRequest(newCategoryId, newLimitAmount);
        BudgetRepository.getBudgetService(getContext()).updateBudget(budgetId, request).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                if (response.isSuccessful() && response.body().isSuccess()) {
                    Toast.makeText(getContext(), "Budget updated successfully", Toast.LENGTH_SHORT).show();
                    fetchBudgetOverview(); // Làm mới lại danh sách
                } else {
                    Toast.makeText(getContext(), "Failed to update budget", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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

    private void fetchCategoriesForDialog(String type, AutoCompleteTextView actv, ArrayAdapter<String> adapter, boolean clearCurrentSelection) {
        String userId = sharedPreferences.getString("userId", "");
        CategoryRepository.getCategoryService(getContext()).getCategories(type, userId).enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    categoryList.clear();
                    categoryList.addAll(response.body().getPayload());

                    List<String> categoryNames = new ArrayList<>();
                    for (GetCategoryResponse category : categoryList) {
                        categoryNames.add(category.getName());
                    }
                    adapter.clear();
                    adapter.addAll(categoryNames);
                    adapter.notifyDataSetChanged();

                    // SỬA LỖI Ở ĐÂY: Chỉ xóa text khi được yêu cầu
                    if (clearCurrentSelection) {
                        actv.setText("", false); // Xóa lựa chọn cũ
                    }
                } else {
                    Toast.makeText(getContext(), "Failed to fetch categories", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {
                Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showMonthPickerDialog(Button btnMonthPicker) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // Định dạng để hiển thị (Tên tháng, năm)
                    Calendar selectedCal = Calendar.getInstance();
                    selectedCal.set(year1, monthOfYear, 1);
                    SimpleDateFormat displayFormat = new SimpleDateFormat("MMMM, yyyy", Locale.getDefault());
                    String displayMonth = displayFormat.format(selectedCal.getTime());
                    btnMonthPicker.setText(displayMonth);

                    // Định dạng để gửi đi API (yyyy-MM-dd)
                    selectedMonthValue = year1 + "-" + String.format(Locale.US, "%02d", monthOfYear + 1) + "-01";
                }, year, month, 1);

        // --- SỬA LỖI Ở ĐÂY ---
        // Thêm một bước kiểm tra null để tránh crash
        try {
            View dayPicker = datePickerDialog.getDatePicker().findViewById(getResources().getIdentifier("day", "id", "android"));
            if (dayPicker != null) {
                dayPicker.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            // Ghi lại log lỗi nếu cần, nhưng không làm crash app
            e.printStackTrace();
        }
        // --- KẾT THÚC PHẦN SỬA LỖI ---

        datePickerDialog.show();
    }

    private void showAddBudgetDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_budget, null);
        builder.setView(dialogView);

        // Ánh xạ các view trong dialog
        MaterialButtonToggleGroup toggleGroup = dialogView.findViewById(R.id.toggleGroupType);
        AutoCompleteTextView actvDialogCategory = dialogView.findViewById(R.id.actvCategory);
        EditText etDialogLimitAmount = dialogView.findViewById(R.id.etLimitAmount);
        Button btnDialogMonthPicker = dialogView.findViewById(R.id.btnMonthPicker);

        // -- LOGIC MỚI --

        // 1. Thiết lập adapter cho Category (ban đầu rỗng)
        List<String> categoryNames = new ArrayList<>();
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
        actvDialogCategory.setAdapter(categoryAdapter);

        // 2. Mặc định chọn Expense và fetch category cho nó
        toggleGroup.check(R.id.btnExpense);
        selectedCategoryType = "Expense";
        fetchCategoriesForDialog(selectedCategoryType, actvDialogCategory, categoryAdapter, true);

        // 3. Thêm Listener cho nhóm nút Type
        toggleGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnExpense) {
                    selectedCategoryType = "Expense";
                } else if (checkedId == R.id.btnIncome) {
                    selectedCategoryType = "Income";
                } else if (checkedId == R.id.btnNeutral) {
                    selectedCategoryType = "Neutral";
                }
                // Fetch lại category mỗi khi đổi type
                fetchCategoriesForDialog(selectedCategoryType, actvDialogCategory, categoryAdapter, true);
            }
        });

        // 4. Listener cho nút chọn tháng
        btnDialogMonthPicker.setOnClickListener(v -> showMonthPickerDialog(btnDialogMonthPicker));

        builder.setTitle("Add Budget")
                .setPositiveButton("Add", (dialog, which) -> {
                    String categoryName = actvDialogCategory.getText().toString();
                    String categoryId = null;
                    // Tìm ID của category được chọn
                    for (GetCategoryResponse category : categoryList) {
                        if (category.getName().equals(categoryName)) {
                            categoryId = category.getId();
                            break;
                        }
                    }

                    // Lấy số tiền
                    String limitAmountStr = etDialogLimitAmount.getText().toString();

                    // --- Kiểm tra dữ liệu đầu vào ---
                    if (categoryId == null || categoryId.isEmpty()) {
                        Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (limitAmountStr.isEmpty()) {
                        Toast.makeText(getContext(), "Please enter a limit amount", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (selectedMonthValue == null || selectedMonthValue.isEmpty()) {
                        Toast.makeText(getContext(), "Please select a month", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double limitAmount = Double.parseDouble(limitAmountStr);

                    // Gọi hàm tạo budget
                    createBudget(categoryId, limitAmount, selectedMonthValue);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        builder.create().show();
    }
}
