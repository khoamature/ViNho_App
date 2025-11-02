package fpt.edu.vn.vinho_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
// Import lớp DTO mới
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.CategoryOverview;

// Thay đổi kiểu dữ liệu mà Adapter quản lý từ GetBudgetResponse thành CategoryOverview
public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    // 1. Thay đổi kiểu dữ liệu của list
    private List<CategoryOverview> budgetList;

    // 2. Cập nhật constructor
    public BudgetAdapter(List<CategoryOverview> budgetList) {
        this.budgetList = budgetList;
    }

    @NonNull
    @Override
    public BudgetViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_budget_category, parent, false);
        return new BudgetViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BudgetViewHolder holder, int position) {
        // 3. Lấy đối tượng CategoryOverview
        CategoryOverview budget = budgetList.get(position);
        holder.bind(budget);
    }

    @Override
    public int getItemCount() {
        // Đảm bảo list không null để tránh NullPointerException
        return budgetList != null ? budgetList.size() : 0;
    }

    // 4. Tạo phương thức updateData để nhận dữ liệu mới
    public void updateData(List<CategoryOverview> newBudgetList) {
        this.budgetList.clear();
        this.budgetList.addAll(newBudgetList);
        notifyDataSetChanged(); // Báo cho RecyclerView biết dữ liệu đã thay đổi
    }

    // Lớp ViewHolder
    public static class BudgetViewHolder extends RecyclerView.ViewHolder {
        // Khai báo các view trong item_budget.xml (tên có thể khác)
        TextView tvCategoryName, tvSpentAmount, tvBudgetedAmount, tvRemainingAmount, tvUsedPercentage;
        ProgressBar progressBar;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ view
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName); // Thay R.id cho đúng
            tvSpentAmount = itemView.findViewById(R.id.tvSpentAmount);
            tvBudgetedAmount = itemView.findViewById(R.id.tvLimitAmount);
            tvRemainingAmount = itemView.findViewById(R.id.tvRemainingAmount);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvUsedPercentage = itemView.findViewById(R.id.tvUsedPercentage);
        }

        // Phương thức bind dữ liệu
        public void bind(CategoryOverview budget) {
            tvCategoryName.setText(budget.getCategoryName());
            tvSpentAmount.setText(formatCurrency(budget.getSpentAmount()));
            tvBudgetedAmount.setText(String.format("/ %s", formatCurrency(budget.getBudgetedAmount())));
            tvRemainingAmount.setText(String.format("Còn lại %s", formatCurrency(budget.getRemainingAmount())));

            // Tính toán và cập nhật progress bar
            if (budget.getBudgetedAmount() > 0) {
                // Đảm bảo phép chia là số thực để có kết quả chính xác
                double progressValue = (budget.getSpentAmount() / budget.getBudgetedAmount()) * 100.0;
                progressBar.setProgress((int) progressValue);
            } else {
                progressBar.setProgress(0);
            }
            double progressValue = 0;
            if (budget.getBudgetedAmount() > 0) {
                progressValue = (budget.getSpentAmount() / budget.getBudgetedAmount()) * 100.0;
            }
            tvUsedPercentage.setText(String.format(Locale.US, "%d%%", (int) progressValue));
        }

        // Helper method để format tiền tệ
        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###đ");
            return formatter.format(amount);
        }
    }
}
