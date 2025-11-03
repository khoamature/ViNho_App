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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionItem;
import fpt.edu.vn.vinho_app.ui.model.DateHeaderItem;
import fpt.edu.vn.vinho_app.ui.model.DisplayableItem;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TRANSACTION = 1;

    private List<DisplayableItem> items;

    public TransactionAdapter(List<DisplayableItem> items) {
        this.items = items;
    }

    // Phương thức để Fragment cập nhật filter type cho Adapter


    public void updateData(List<DisplayableItem> newItems) {
        this.items.clear();
        this.items.addAll(newItems);
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        if (items.get(position) instanceof DateHeaderItem) {
            return TYPE_HEADER;
        }
        return TYPE_TRANSACTION;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == TYPE_HEADER) {
            View view = inflater.inflate(R.layout.item_transaction_header, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_transaction, parent, false);
            return new TransactionViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        // Lấy ra kiểu của view tại vị trí này
        int viewType = getItemViewType(position);

        if (viewType == TYPE_HEADER) {
            // Nếu là header, ép kiểu và bind dữ liệu cho HeaderViewHolder
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            DateHeaderItem headerItem = (DateHeaderItem) items.get(position);
            headerHolder.bind(headerItem);
        } else if (viewType == TYPE_TRANSACTION) {
            // Nếu là transaction, ép kiểu và bind dữ liệu cho TransactionViewHolder
            TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
            TransactionItem transactionItem = (TransactionItem) items.get(position);
            transactionHolder.bind(transactionItem);
        }
    }

    @Override
    public int getItemCount() {
        return items != null ? items.size() : 0;
    }

    // ViewHolder cho Header ngày
    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView tvDateHeader;

        public HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDateHeader = itemView.findViewById(R.id.tvDateHeader);
        }

        public void bind(DateHeaderItem header) {
            tvDateHeader.setText(header.getDate());
        }
    }

    // ViewHolder cho Giao dịch
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon;
        TextView tvCategoryName, tvDescription, tvAmount, tvTransactionTime;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTransactionTime = itemView.findViewById(R.id.tvTransactionTime);
        }

        public void bind(TransactionItem transaction) {
            tvCategoryName.setText(transaction.getCategoryName());
            tvDescription.setText(transaction.getDescription());
            ivCategoryIcon.setImageResource(R.drawable.ic_money);

            // SỬA LẠI LOGIC: Dựa vào categoryType của chính transaction
            if ("Income".equalsIgnoreCase(transaction.getCategoryType())) {
                tvAmount.setText(String.format(Locale.US, "+%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#2ECC71"));
            } else { // Mặc định là Expense
                tvAmount.setText(String.format(Locale.US, "-%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#E74C3C"));
            }

            // Xử lý thời gian (không đổi)
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                Date date = apiFormat.parse(transaction.getTransactionDate());
                if (date != null) {
                    tvTransactionTime.setText(timeFormat.format(date));
                }
            } catch (ParseException e) {
                tvTransactionTime.setText("");
            }
        }
    }

    private static String formatCurrency(double amount) {
        // Lấy giá trị tuyệt đối để format, dấu +/- sẽ được thêm ở ngoài
        DecimalFormat formatter = new DecimalFormat("#,###.##đ");
        return formatter.format(Math.abs(amount));
    }
}
