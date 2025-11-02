package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.ui.adapter.TransactionAdapter;
import fpt.edu.vn.vinho_app.data.remote.dto.request.category.GetPagedCategoriesRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.GetPagedTransactionsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.repository.CategoryRepository;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {
    private static final String TAG = "TransactionFragment";
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<GetTransactionResponse> transactionList = new ArrayList<>();
    private EditText editTextDescription, editTextStartAmount, editTextEndAmount, editTextFromDate, editTextToDate, editTextSearchCategory;
    private Spinner spinnerCategoryType, spinnerCategory;
    private Button buttonFilter, buttonClear;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    private List<GetCategoryResponse> categoryList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        initUI(view);
        setupUI();

        fetchCategories();
        fetchTransactions();

        return view;
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        editTextDescription = view.findViewById(R.id.editTextDescription);
        spinnerCategoryType = view.findViewById(R.id.spinnerCategoryType);
        editTextSearchCategory = view.findViewById(R.id.editTextSearchCategory);
        spinnerCategory = view.findViewById(R.id.spinnerCategory);
        editTextStartAmount = view.findViewById(R.id.editTextStartAmount);
        editTextEndAmount = view.findViewById(R.id.editTextEndAmount);
        editTextFromDate = view.findViewById(R.id.editTextFromDate);
        editTextToDate = view.findViewById(R.id.editTextToDate);
        buttonFilter = view.findViewById(R.id.buttonFilter);
        buttonClear = view.findViewById(R.id.buttonClear);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupUI() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        setupCategoryTypeSpinner();
        setupCategorySpinner();
        setupSearchCategory();
        setDefaultDates();

        buttonFilter.setOnClickListener(v -> fetchTransactions());
        buttonClear.setOnClickListener(v -> clearFilters());

        swipeRefreshLayout.setOnRefreshListener(this::fetchTransactions);
    }

    private void setupCategoryTypeSpinner() {
        List<String> categoryTypes = new ArrayList<>();
        categoryTypes.add("All Types");
        categoryTypes.add("Expense");
        categoryTypes.add("Income");
        categoryTypes.add("Neutral");

        ArrayAdapter<String> categoryTypeAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_with_hint, categoryTypes);
        categoryTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategoryType.setAdapter(categoryTypeAdapter);

        spinnerCategoryType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                fetchCategories();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_with_hint, new ArrayList<>());
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(categoryAdapter);
    }

    private void setupSearchCategory() {
        editTextSearchCategory.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                fetchCategories();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setDefaultDates() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        editTextToDate.setText(sdf.format(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        editTextFromDate.setText(sdf.format(calendar.getTime()));
    }

    private void clearFilters() {
        editTextDescription.setText("");
        spinnerCategoryType.setSelection(0);
        editTextSearchCategory.setText("");
        spinnerCategory.setSelection(0);
        editTextStartAmount.setText("");
        editTextEndAmount.setText("");
        setDefaultDates();
        fetchTransactions();
    }

    private void fetchCategories() {
        String userId = sharedPreferences.getString("userId", "");
        GetPagedCategoriesRequest request = new GetPagedCategoriesRequest(userId);

        int selectedCategoryTypePos = spinnerCategoryType.getSelectedItemPosition();
        if (selectedCategoryTypePos > 0) {
            request.setCategoryType(selectedCategoryTypePos - 1);
        }
        request.setName(editTextSearchCategory.getText().toString());

        Call<PagedResponse<GetCategoryResponse>> call = CategoryRepository.getCategoryService(getContext()).getCategories(request);
        call.enqueue(new Callback<PagedResponse<GetCategoryResponse>>() {
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

                    ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.spinner_item_with_hint, categoryNames);
                    categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCategory.setAdapter(categoryAdapter);
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

    private void fetchTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        String userId = sharedPreferences.getString("userId", "");
        GetPagedTransactionsRequest request = new GetPagedTransactionsRequest(userId);

        request.setDescription(editTextDescription.getText().toString());

        int selectedCategoryTypePos = spinnerCategoryType.getSelectedItemPosition();
        if (selectedCategoryTypePos > 0) {
            request.setCategoryType(selectedCategoryTypePos - 1);
        }

        int selectedCategoryPos = spinnerCategory.getSelectedItemPosition();
        if (selectedCategoryPos > 0) {
            request.setCategoryId(categoryList.get(selectedCategoryPos - 1).getId());
        }

        try {
            request.setStartRangeAmount(Double.parseDouble(editTextStartAmount.getText().toString()));
        } catch (NumberFormatException e) {
            request.setStartRangeAmount(null);
        }
        try {
            request.setEndRangeAmount(Double.parseDouble(editTextEndAmount.getText().toString()));
        } catch (NumberFormatException e) {
            request.setEndRangeAmount(null);
        }

        request.setFromDate(editTextFromDate.getText().toString());
        request.setToDate(editTextToDate.getText().toString());

        Call<PagedResponse<GetTransactionResponse>> call = TransactionRepository.getTransactionService(getContext()).getTransactions(request);
        call.enqueue(new Callback<PagedResponse<GetTransactionResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetTransactionResponse>> call, Response<PagedResponse<GetTransactionResponse>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                    transactionList.clear();
                    transactionList.addAll(response.body().getPayload());
                    adapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getContext(), "Failed to fetch transactions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PagedResponse<GetTransactionResponse>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
