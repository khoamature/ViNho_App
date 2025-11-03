package fpt.edu.vn.vinho_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionSummaryResponse;

public class SummaryCardAdapter extends RecyclerView.Adapter<SummaryCardAdapter.SummaryViewHolder> {

    private TransactionSummaryResponse summary;

    public void setSummary(TransactionSummaryResponse summary) {
        this.summary = summary;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SummaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_transaction_summary_card, parent, false);
        return new SummaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SummaryViewHolder holder, int position) {
        if (summary != null) {
            holder.bind(summary);
        }
    }

    @Override
    public int getItemCount() {
        // Luôn chỉ trả về 1 (hoặc 0 nếu chưa có dữ liệu)
        return summary == null ? 0 : 1;
    }

    static class SummaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvTotalIncome, tvTotalExpense, tvNetBalance;

        public SummaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTotalIncome = itemView.findViewById(R.id.tvTotalIncome);
            tvTotalExpense = itemView.findViewById(R.id.tvTotalExpense);
            tvNetBalance = itemView.findViewById(R.id.tvNetBalance);
        }

        void bind(TransactionSummaryResponse summary) {
            tvTotalIncome.setText(formatCurrency(summary.getTotalIncome()));
            tvTotalExpense.setText(formatCurrency(summary.getTotalExpense()));
            tvNetBalance.setText(formatCurrency(summary.getNetBalance()));
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###.##đ");
            return formatter.format(amount);
        }
    }
}
