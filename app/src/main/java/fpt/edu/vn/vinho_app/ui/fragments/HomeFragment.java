package fpt.edu.vn.vinho_app.ui.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.GetPagedTransactionsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.repository.TransactionRepository;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private TransactionAdapter adapter;
    private List<GetTransactionResponse> transactionList = new ArrayList<>();
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);

        initUI(view);
        setupUI();

        fetchTransactions();

        return view;
    }

    private void initUI(View view) {
        recyclerView = view.findViewById(R.id.recyclerViewTransactions);
        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
    }

    private void setupUI() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new TransactionAdapter(transactionList);
        recyclerView.setAdapter(adapter);
        swipeRefreshLayout.setOnRefreshListener(this::fetchTransactions);
    }

    private void fetchTransactions() {
        swipeRefreshLayout.setRefreshing(true);
        String userId = sharedPreferences.getString("userId", "");
        GetPagedTransactionsRequest request = new GetPagedTransactionsRequest(userId);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        request.setToDate(sdf.format(calendar.getTime()));

        calendar.set(Calendar.DAY_OF_MONTH, 1);
        request.setFromDate(sdf.format(calendar.getTime()));

        request.setSortBy("CreatedAt");
        request.setSortDescending(true);


        TransactionRepository.getTransactionService(getContext()).getTransactions(request).enqueue(new Callback<PagedResponse<GetTransactionResponse>>() {
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
