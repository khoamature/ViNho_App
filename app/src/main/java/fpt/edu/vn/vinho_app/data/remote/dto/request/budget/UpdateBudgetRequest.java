package fpt.edu.vn.vinho_app.data.remote.dto.request.budget;

public class UpdateBudgetRequest {
    private String categoryId;private double limitAmount;

    public UpdateBudgetRequest(String categoryId, double limitAmount) {
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }
}
