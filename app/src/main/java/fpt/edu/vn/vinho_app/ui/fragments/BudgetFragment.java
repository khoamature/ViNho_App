package fpt.edu.vn.vinho_app.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import fpt.edu.vn.vinho_app.R;

public class BudgetFragment extends Fragment {
    private RecyclerView recyclerCategories;
//    private CategoryBudgetAdapter adapter;
    private LinearLayout layoutEmptyState;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate layout chính
        View view = inflater.inflate(R.layout.fragment_budget, container, false);

        // Ánh xạ RecyclerView
        recyclerCategories = view.findViewById(R.id.recyclerCategories);
        recyclerCategories.setLayoutManager(new LinearLayoutManager(getContext()));

        // TODO: Lấy dữ liệu (hiện tại dùng dữ liệu giả)
        // List<Category> categoryList = ...;

        // adapter = new CategoryBudgetAdapter(categoryList);
        // recyclerCategories.setAdapter(adapter);

        return view;
    }

//    private void checkEmptyState() {
//        if (categoryList.isEmpty()) {
//            // Danh sách rỗng -> Hiện Trạng thái rỗng, Ẩn RecyclerView
//            recyclerCategories.setVisibility(View.GONE);
//            layoutEmptyState.setVisibility(View.VISIBLE);
//        } else {
//            // Danh sách có data -> Ẩn Trạng thái rỗng, Hiện RecyclerView
//            recyclerCategories.setVisibility(View.VISIBLE);
//            layoutEmptyState.setVisibility(View.GONE);
//        }
//    }
//
//    private void onDataAdded() {
//        // ... code thêm data vào categoryList ...
//        adapter.notifyDataSetChanged();
//        checkEmptyState(); // Kiểm tra lại sau khi cập nhật data
//    }
}
