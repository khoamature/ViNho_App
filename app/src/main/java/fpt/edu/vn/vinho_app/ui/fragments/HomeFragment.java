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
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        sharedPreferences = requireActivity().getSharedPreferences("myPrefs", Context.MODE_PRIVATE);
        return view;
    }

}
