package fpt.edu.vn.vinho_app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.RecentTransaction;

public class RecentTransactionAdapter extends RecyclerView.Adapter<RecentTransactionAdapter.ViewHolder> {

    private List<RecentTransaction> transactions;

    public RecentTransactionAdapter(List<RecentTransaction> transactions) {
        this.transactions = transactions;
    }

    public void updateData(List<RecentTransaction> newTransactions) {
        this.transactions.clear();
        this.transactions.addAll(newTransactions);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recent_transaction, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(transactions.get(position));
    }

    @Override
    public int getItemCount() {
        return transactions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvDescription, tvAmount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        public void bind(RecentTransaction transaction) {
            tvCategoryName.setText(transaction.getCategory());
            tvDescription.setText(transaction.getDescription());

            if ("Income".equalsIgnoreCase(transaction.getCategoryType())) {
                ivCategoryIcon.setImageResource(R.drawable.ic_money); // Icon thu nhập
                tvAmount.setText(String.format(Locale.US, "+%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#2ECC71"));
            } else {
                ivCategoryIcon.setImageResource(R.drawable.ic_money); // Icon chi tiêu
                tvAmount.setText(String.format(Locale.US, "-%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#E74C3C"));
            }
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###đ");
            return formatter.format(Math.abs(amount));
        }
    }
}
