package fpt.edu.vn.vinho_app.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;

public class BudgetAdapter extends RecyclerView.Adapter<BudgetAdapter.BudgetViewHolder> {

    private List<GetBudgetResponse> budgetList;

    public BudgetAdapter(List<GetBudgetResponse> budgetList) {
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
        GetBudgetResponse budget = budgetList.get(position);
        holder.tvCategoryName.setText(budget.getCategoryId()); // You might want to resolve this to a category name
        holder.tvLimitAmount.setText(String.valueOf(budget.getLimitAmount()));
        holder.tvSpentAmount.setText("0"); // You need to calculate this
    }

    @Override
    public int getItemCount() {
        return budgetList.size();
    }

    static class BudgetViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName, tvLimitAmount, tvSpentAmount;

        public BudgetViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvLimitAmount = itemView.findViewById(R.id.tvLimitAmount);
            tvSpentAmount = itemView.findViewById(R.id.tvSpentAmount);
        }
    }
    public void updateData(List<GetBudgetResponse> newBudgets) {
        this.budgetList.clear();
        this.budgetList.addAll(newBudgets);
        notifyDataSetChanged();
    }

}
