package fpt.edu.vn.vinho_app.data.remote.dto.response.budget;

import java.util.List;

public class BudgetOverviewResponse {
    private double totalBudgetedAmount;
    private double totalSpentAmount;
    private double totalRemainingAmount;
    private List<CategoryOverview> categoryOverviews;

    // Getters and setters for all fields

    public double getTotalBudgetedAmount() {
        return totalBudgetedAmount;
    }

    public void setTotalBudgetedAmount(double totalBudgetedAmount) {
        this.totalBudgetedAmount = totalBudgetedAmount;
    }

    public double getTotalSpentAmount() {
        return totalSpentAmount;
    }

    public void setTotalSpentAmount(double totalSpentAmount) {
        this.totalSpentAmount = totalSpentAmount;
    }

    public double getTotalRemainingAmount() {
        return totalRemainingAmount;
    }

    public void setTotalRemainingAmount(double totalRemainingAmount) {
        this.totalRemainingAmount = totalRemainingAmount;
    }

    public List<CategoryOverview> getCategoryOverviews() {
        return categoryOverviews;
    }

    public void setCategoryOverviews(List<CategoryOverview> categoryOverviews) {
        this.categoryOverviews = categoryOverviews;
    }
}
