package fpt.edu.vn.vinho_app.data.remote.dto.request.transaction;

public class UpdateTransactionRequest {
    private String categoryId;
    private double amount;
    private String description;
    private String transactionDate;

    public UpdateTransactionRequest(String categoryId, double amount, String description, String transactionDate) {
        this.categoryId = categoryId;
        this.amount = amount;
        this.description = description;
        this.transactionDate = transactionDate;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(String transactionDate) {
        this.transactionDate = transactionDate;
    }
}
