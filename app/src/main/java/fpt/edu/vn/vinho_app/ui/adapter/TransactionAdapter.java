package fpt.edu.vn.vinho_app.ui.adapter;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionItem;
import fpt.edu.vn.vinho_app.ui.model.DateHeaderItem;
import fpt.edu.vn.vinho_app.ui.model.DisplayableItem;

public class TransactionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_TRANSACTION = 1;

    private List<DisplayableItem> items;
    private final OnTransactionActionsListener listener;

    // Interface để giao tiếp từ Adapter về Fragment
    public interface OnTransactionActionsListener {
        void onEditTransaction(TransactionItem item);
        void onDeleteTransaction(TransactionItem item);
    }

    public TransactionAdapter(List<DisplayableItem> items, OnTransactionActionsListener listener) {
        this.items = items;
        this.listener = listener;
    }

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
        int viewType = getItemViewType(position);

        if (viewType == TYPE_HEADER) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            DateHeaderItem headerItem = (DateHeaderItem) items.get(position);
            headerHolder.bind(headerItem);
        } else if (viewType == TYPE_TRANSACTION) {
            TransactionViewHolder transactionHolder = (TransactionViewHolder) holder;
            TransactionItem transactionItem = (TransactionItem) items.get(position);
            // Truyền cả transaction và listener vào để xử lý sự kiện
            transactionHolder.bind(transactionItem, listener);
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

    // ViewHolder cho một Giao dịch
    static class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCategoryIcon, btnMenu;
        TextView tvCategoryName, tvDescription, tvAmount, tvTransactionTime;

        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCategoryIcon = itemView.findViewById(R.id.ivCategoryIcon);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvTransactionTime = itemView.findViewById(R.id.tvTransactionTime);
            btnMenu = itemView.findViewById(R.id.btnMenu); // Ánh xạ nút menu
        }

        public void bind(final TransactionItem transaction, final OnTransactionActionsListener listener) {
            // Bind dữ liệu cơ bản
            tvCategoryName.setText(transaction.getCategoryName());
            tvDescription.setText(transaction.getDescription());
            ivCategoryIcon.setImageResource(R.drawable.ic_money); // Set icon cứng

            // Hiển thị số tiền dựa trên categoryType
            if ("Income".equalsIgnoreCase(transaction.getCategoryType())) {
                tvAmount.setText(String.format(Locale.US, "+%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#2ECC71"));
            } else { // Expense hoặc các loại khác
                tvAmount.setText(String.format(Locale.US, "-%s", formatCurrency(transaction.getAmount())));
                tvAmount.setTextColor(Color.parseColor("#E74C3C"));
            }

            // Hiển thị thời gian
            try {
                SimpleDateFormat apiFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US);
                // Format để hiển thị giờ cho người dùng (theo múi giờ của thiết bị)
                SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
                timeFormat.setTimeZone(Calendar.getInstance().getTimeZone());

                // 1. Parse chuỗi UTC từ API thành đối tượng Date
                Date date = apiFormat.parse(transaction.getTransactionDate());

                // 2. Format đối tượng Date đó ra chuỗi hiển thị theo múi giờ địa phương
                if (date != null) {
                    tvTransactionTime.setText(timeFormat.format(date));
                }
            } catch (ParseException e) {
                tvTransactionTime.setText("");
                e.printStackTrace();
            }

            // Thêm sự kiện click cho nút menu (3 chấm)
            btnMenu.setOnClickListener(view -> {
                PopupMenu popup = new PopupMenu(view.getContext(), btnMenu);
                popup.getMenuInflater().inflate(R.menu.budget_item_menu, popup.getMenu()); // Tái sử dụng menu cũ

                popup.setOnMenuItemClickListener(item -> {
                    int itemId = item.getItemId();
                    if (itemId == R.id.menu_edit) {
                        listener.onEditTransaction(transaction);
                        return true;
                    } else if (itemId == R.id.menu_delete) {
                        listener.onDeleteTransaction(transaction);
                        return true;
                    }
                    return false;
                });
                popup.show();
            });
        }

        private String formatCurrency(double amount) {
            DecimalFormat formatter = new DecimalFormat("#,###.##đ");
            return formatter.format(Math.abs(amount));
        }
    }
}
