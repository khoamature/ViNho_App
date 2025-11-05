package fpt.edu.vn.vinho_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.CategoryDetail;

public class CategoryDetailAdapter extends RecyclerView.Adapter<CategoryDetailAdapter.ViewHolder> {

    private List<CategoryDetail> categoryDetails;

    public CategoryDetailAdapter(List<CategoryDetail> categoryDetails) {
        this.categoryDetails = categoryDetails;
    }

    public void updateData(List<CategoryDetail> newCategoryDetails) {
        this.categoryDetails.clear();
        this.categoryDetails.addAll(newCategoryDetails);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_detail, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(categoryDetails.get(position));
    }

    @Override
    public int getItemCount() {
        return categoryDetails != null ? categoryDetails.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvCategoryPercentage, tvCategoryAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryPercentage = itemView.findViewById(R.id.tvCategoryPercentage);
            tvCategoryAmount = itemView.findViewById(R.id.tvCategoryAmount);
        }

        public void bind(CategoryDetail detail) {
            tvCategoryName.setText(detail.getCategory());
            // Format chuỗi phần trăm, ví dụ: "18.42% of total"
            String percentageText = String.format(Locale.US, "%.2f%% of total", detail.getPercentage());
            tvCategoryPercentage.setText(percentageText);
            // Format tiền tệ
            tvCategoryAmount.setText(formatCurrency(detail.getAmount()));
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###đ");
            // Lấy giá trị tuyệt đối để hiển thị, vì đây là danh sách chi tiêu
            return formatter.format(Math.abs(amount));
        }
    }
}
    