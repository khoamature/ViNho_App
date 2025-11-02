package fpt.edu.vn.vinho_app.data.remote.dto.response.budget;

public class CategoryOverview {
    private String categoryId;
    private String categoryName;
    private double budgetedAmount;
    private double spentAmount;
    private double remainingAmount;

    // Getters and setters for all fields

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public double getBudgetedAmount() {
        return budgetedAmount;
    }

    public void setBudgetedAmount(double budgetedAmount) {
        this.budgetedAmount = budgetedAmount;
    }

    public double getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(double spentAmount) {
        this.spentAmount = spentAmount;
    }

    public double getRemainingAmount() {
        return remainingAmount;
    }

    public void setRemainingAmount(double remainingAmount) {
        this.remainingAmount = remainingAmount;
    }
}
