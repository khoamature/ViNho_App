package fpt.edu.vn.vinho_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
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
    private final OnBudgetActionsListener listener;

    public interface OnBudgetActionsListener {
        void onEditBudget(CategoryOverview budget);
        void onDeleteBudget(CategoryOverview budget);
    }

    // 2. Cập nhật constructor
    public BudgetAdapter(List<CategoryOverview> budgetList, OnBudgetActionsListener listener) {
        this.budgetList = budgetList;
        this.listener = listener;
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
        holder.bind(budget, listener);
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

        ImageView btnMenu;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            // Ánh xạ view
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName); // Thay R.id cho đúng
            tvSpentAmount = itemView.findViewById(R.id.tvSpentAmount);
            tvBudgetedAmount = itemView.findViewById(R.id.tvLimitAmount);
            tvRemainingAmount = itemView.findViewById(R.id.tvRemainingAmount);
            progressBar = itemView.findViewById(R.id.progressBar);
            tvUsedPercentage = itemView.findViewById(R.id.tvUsedPercentage);
            btnMenu = itemView.findViewById(R.id.btnMenu);
        }

        // Phương thức bind dữ liệu
        public void bind(CategoryOverview budget, final OnBudgetActionsListener listener) {
            // 1. Bind dữ liệu cơ bản
            tvCategoryName.setText(budget.getCategoryName());

            // Lấy các giá trị số
            double spentAmount = budget.getSpentAmount();
            double budgetedAmount = budget.getBudgetedAmount();
            double remainingAmount = budget.getRemainingAmount();

            // Format và hiển thị tiền tệ
            // Sử dụng Math.abs() để luôn hiển thị số dương cho spentAmount và budgetedAmount
            tvSpentAmount.setText(String.format(Locale.US, "%s", formatCurrency(Math.abs(spentAmount))));
            tvBudgetedAmount.setText(String.format(Locale.US, " / %s", formatCurrency(budgetedAmount)));
            tvRemainingAmount.setText(formatCurrency(remainingAmount));
            if (remainingAmount < 0) {
                tvRemainingAmount.setTextColor(itemView.getContext().getResources().getColor(R.color.budget_red));
            } else {
                tvRemainingAmount.setTextColor(itemView.getContext().getResources().getColor(R.color.budget_blue));
            }
            // 2. Tính toán phần trăm và progress bar
            double progressValue = 0;
            if (budgetedAmount > 0) {
                progressValue = (Math.abs(spentAmount) / budgetedAmount) * 100.0;
            }

            // 3. Cập nhật ProgressBar và TextView phần trăm
            progressBar.setProgress((int) progressValue);
            if (progressValue > 100) {
                // Nếu vượt quá 100%, hiển thị "Over budget"
                int overPercentage = (int) (progressValue - 100);
                tvUsedPercentage.setText(String.format(Locale.US, "Over budget %d%%", overPercentage));
                tvUsedPercentage.setTextColor(itemView.getContext().getResources().getColor(R.color.budget_red));
            } else {
                // Nếu chưa vượt, hiển thị phần trăm bình thường
                tvUsedPercentage.setText(String.format(Locale.US, "%d%%", (int) progressValue));
                // Trả về màu mặc định
                tvUsedPercentage.setTextColor(itemView.getContext().getResources().getColor(R.color.budget_blue));
            }

            // 4. Xử lý sự kiện cho nút menu (không đổi)
            btnMenu.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), btnMenu);
                popup.getMenuInflater().inflate(R.menu.budget_item_menu, popup.getMenu());

                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit) {
                        listener.onEditBudget(budget);
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        listener.onDeleteBudget(budget);
                        return true;
                    }
                    return false;
                });

                popup.show();
            });
        }

        // Helper method để format tiền tệ (không đổi)
        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###đ");
            return formatter.format(amount);
        }
    }
}
