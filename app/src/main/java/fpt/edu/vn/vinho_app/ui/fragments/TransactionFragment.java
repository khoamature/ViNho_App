package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.adapter.TransactionAdapter;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.GetPagedTransactionsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TransactionFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<GetTransactionResponse> transactionList = new ArrayList<>();
    private EditText editTextDescription, editTextCategoryType, editTextStartAmount, editTextEndAmount, editTextFromDate, editTextToDate;
    private Button buttonFilter, buttonClear;
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction, container, false);
        recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);

        editTextDescription = view.findViewById(R.id.editTextDescription);
        editTextCategoryType = view.findViewById(R.id.editTextCategoryType);
        editTextStartAmount = view.findViewById(R.id.editTextStartAmount);
        editTextEndAmount = view.findViewById(R.id.editTextEndAmount);
        editTextFromDate = view.findViewById(R.id.editTextFromDate);
        editTextToDate = view.findViewById(R.id.editTextToDate);
        buttonFilter = view.findViewById(R.id.buttonFilter);
        buttonClear = view.findViewById(R.id.buttonClear);

        buttonFilter.setOnClickListener(v -> fetchTransactions());
        buttonClear.setOnClickListener(v -> {
            editTextDescription.setText("");
            editTextCategoryType.setText("");
            editTextStartAmount.setText("");
            editTextEndAmount.setText("");
            editTextFromDate.setText("");
            editTextToDate.setText("");
            fetchTransactions();
        });
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        fetchTransactions();

        return view;
    }

    private void fetchTransactions() {
        String userId = sharedPreferences.getString("userId", "");
        GetPagedTransactionsRequest request = new GetPagedTransactionsRequest(userId);
        request.setDescription(editTextDescription.getText().toString());

        String text = editTextCategoryType.getText().toString();
        int categoryType = 0;

        if (text != null && !text.trim().isEmpty()) {
            try {
                categoryType = Integer.parseInt(text.trim());
            } catch (NumberFormatException e) {
                categoryType = 0;
            }
        }
        request.setCategoryType(categoryType);

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


        Call<PagedResponse<GetTransactionResponse>> call = TransactionRepository
                .getTransactionService(getContext())
                .getTransactions(request);
        call.enqueue(new Callback<PagedResponse<GetTransactionResponse>>() {
            @Override
            public void onResponse(Call<PagedResponse<GetTransactionResponse>> call, Response<PagedResponse<GetTransactionResponse>> response) {
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
                Toast.makeText(getContext(), "An error occurred: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
