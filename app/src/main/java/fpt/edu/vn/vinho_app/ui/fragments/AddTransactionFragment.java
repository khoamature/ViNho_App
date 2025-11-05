package fpt.edu.vn.vinho_app.ui.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.textfield.TextInputEditText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.CreateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.repository.CategoryRepository;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import fpt.edu.vn.vinho_app.ui.viewmodel.SharedViewModel;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddTransactionFragment extends BottomSheetDialogFragment {
    private static final String TAG = "AddTransactionFragment";

    // Listener để báo cho Fragment/Activity cha biết khi nào cần làm mới
    private SharedViewModel sharedViewModel;
    // Views
    private MaterialButtonToggleGroup toggleGroupType;
    private AutoCompleteTextView autoCompleteCategory;
    private TextInputEditText editAmount, editNote;
    private TextView tvSelectedDateTime;
    private Button btnSave;

    // Data
    private SharedPreferences sharedPreferences;
    private Calendar selectedDateTime;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();
    private String selectedCategoryId = null;
    private String selectedType = "Expense"; // Mặc định



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_add_transaction, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        mapViews(view);
        initData();
        setupListeners();
    }

    private void mapViews(View view) {
        toggleGroupType = view.findViewById(R.id.toggleGroupType);
        autoCompleteCategory = view.findViewById(R.id.autoCompleteCategory);
        editAmount = view.findViewById(R.id.editAmount);
        editNote = view.findViewById(R.id.editNote);
        tvSelectedDateTime = view.findViewById(R.id.tvSelectedDateTime);
        btnSave = view.findViewById(R.id.btnSave);
    }

    private void initData() {
        // Khởi tạo ngày và giờ hiện tại
        selectedDateTime = Calendar.getInstance();
        updateDateTimeText();

        // Mặc định chọn Expense và tải category
        toggleGroupType.check(R.id.btnExpense);
        fetchCategories(selectedType);
    }

    private void setupListeners() {
        // Listener cho việc chọn ngày và giờ
        tvSelectedDateTime.setOnClickListener(v -> showDateTimePickerDialog());

        // Listener khi đổi Type (Expense/Income)
        toggleGroupType.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.btnExpense) {
                    selectedType = "Expense";
                } else if (checkedId == R.id.btnIncome) {
                    selectedType = "Income";
                } else if(checkedId == R.id.btnNeutral){
                    selectedType = "Neutral";
                }
                fetchCategories(selectedType);
            }
        });

        // Listener khi chọn một category
        autoCompleteCategory.setOnItemClickListener((parent, view, position, id) -> {
            String selectedName = (String) parent.getItemAtPosition(position);
            for (GetCategoryResponse category : categoryList) {
                if (category.getName().equals(selectedName)) {
                    selectedCategoryId = category.getId();
                    break;
                }
            }
        });

        // Listener cho nút Save
        btnSave.setOnClickListener(v -> saveTransaction());
    }

    private void fetchCategories(String type) {
        String userId = sharedPreferences.getString("userId", "");
        if (userId.isEmpty()) return;

        CategoryRepository.getCategoryService(getContext()).getCategories(type, userId)
                .enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
                    @Override
                    public void onResponse(Call<PagedResponse<GetCategoryResponse>> call, Response<PagedResponse<GetCategoryResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            categoryList.clear();
                            categoryList.addAll(response.body().getPayload());

                            List<String> categoryNames = new ArrayList<>();
                            for (GetCategoryResponse category : categoryList) {
                                categoryNames.add(category.getName());
                            }

                            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_dropdown_item_1line, categoryNames);
                            autoCompleteCategory.setAdapter(adapter);
                            autoCompleteCategory.setText("", false); // Xóa lựa chọn cũ
                            selectedCategoryId = null;
                        }
                    }

                    @Override
                    public void onFailure(Call<PagedResponse<GetCategoryResponse>> call, Throwable t) {
                        Log.e(TAG, "Failed to fetch categories", t);
                    }
                });
    }

    private void showDateTimePickerDialog() {
        // Lấy ngày tháng năm hiện tại
        int year = selectedDateTime.get(Calendar.YEAR);
        int month = selectedDateTime.get(Calendar.MONTH);
        int day = selectedDateTime.get(Calendar.DAY_OF_MONTH);

        new DatePickerDialog(getContext(), (dateView, selectedYear, selectedMonth, selectedDay) -> {
            // Sau khi chọn ngày, cập nhật vào calendar
            selectedDateTime.set(selectedYear, selectedMonth, selectedDay);

            // Tiếp tục hiển thị TimePickerDialog
            int hour = selectedDateTime.get(Calendar.HOUR_OF_DAY);
            int minute = selectedDateTime.get(Calendar.MINUTE);
            new TimePickerDialog(getContext(), (timeView, selectedHour, selectedMinute) -> {
                // Sau khi chọn giờ, cập nhật vào calendar
                selectedDateTime.set(Calendar.HOUR_OF_DAY, selectedHour);
                selectedDateTime.set(Calendar.MINUTE, selectedMinute);

                // Cập nhật text hiển thị
                updateDateTimeText();
            }, hour, minute, true).show(); // `true` để dùng định dạng 24h

        }, year, month, day).show();
    }

    private void updateDateTimeText() {
        SimpleDateFormat displayFormat = new SimpleDateFormat("EEEE, dd/MM/yyyy HH:mm", new Locale("vi", "VN"));
        tvSelectedDateTime.setText(displayFormat.format(selectedDateTime.getTime()));
    }

    private void saveTransaction() {
        String amountStr = editAmount.getText().toString();
        String description = editNote.getText().toString();
        String userId = sharedPreferences.getString("userId", "");

        if (amountStr.isEmpty()) {
            Toast.makeText(getContext(), "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }
        if (selectedCategoryId == null) {
            Toast.makeText(getContext(), "Please select a category", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount = Double.parseDouble(amountStr);

        // --- Định dạng ngày giờ sang ISO 8601 UTC ---
        SimpleDateFormat apiDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
        String transactionDate = apiDateFormat.format(selectedDateTime.getTime());
        Log.d(TAG, "Creating transaction with date: " + transactionDate); // Thêm log để kiểm tra
        // Tạo request object
        CreateTransactionRequest request = new CreateTransactionRequest(userId, selectedCategoryId, amount, description, transactionDate);

        // Gọi API
        TransactionRepository.getTransactionService(getContext()).createTransaction(request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(Call<Void> call, Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(getContext(), "Transaction added successfully!", Toast.LENGTH_SHORT).show();
                            // Báo cho Fragment cha biết để làm mới
                            sharedViewModel.notifyTransactionAdded();
                            dismiss(); // Đóng BottomSheet
                        } else {
                            Toast.makeText(getContext(), "Failed to add transaction", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Void> call, Throwable t) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
