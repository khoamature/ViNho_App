package fpt.edu.vn.vinho_app.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import fpt.edu.vn.vinho_app.R;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.FinancialTip;

public class RecommendedActionsAdapter extends RecyclerView.Adapter<RecommendedActionsAdapter.ViewHolder> {

    private final List<FinancialTip> tips;
    private final Context context;

    public RecommendedActionsAdapter(Context context, List<FinancialTip> tips) {
        this.context = context;
        this.tips = tips;
    }

    public void updateData(List<FinancialTip> newTips) {
        this.tips.clear();
        this.tips.addAll(newTips);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_recommended_action, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(tips.get(position));
    }

    @Override
    public int getItemCount() {
        return tips.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvActionTitle, tvActionDescription, tvActionImpact;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvActionTitle = itemView.findViewById(R.id.tvActionTitle);
            tvActionDescription = itemView.findViewById(R.id.tvActionDescription);
            tvActionImpact = itemView.findViewById(R.id.tvActionImpact);
        }

        void bind(FinancialTip tip) {
            tvActionTitle.setText(tip.getTitle());
            tvActionDescription.setText(tip.getDescription());

            // Set text và background cho tag "Impact"
            if ("HighImpact".equalsIgnoreCase(tip.getImpact())) {
                tvActionImpact.setText("High Impact");
                tvActionImpact.setBackgroundResource(R.drawable.bg_impact_high);
            } else { // Giả sử còn lại là Medium Impact
                tvActionImpact.setText("Medium Impact");
                tvActionImpact.setBackgroundResource(R.drawable.bg_impact_medium);
            }
        }
    }
}
