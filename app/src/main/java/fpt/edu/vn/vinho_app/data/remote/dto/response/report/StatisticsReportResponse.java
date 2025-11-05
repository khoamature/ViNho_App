package fpt.edu.vn.vinho_app.data.remote.dto.response.report;

import java.util.List;
import java.util.Map;

public class StatisticsReportResponse {
    private double totalIncome;
    private double totalExpense;
    private double savings;
    private List<String> labels;
    private List<Double> incomeSeries;
    private List<Double> expenseSeries;
    private Map<String, Double> spendingByCategory;
    private List<CategoryDetail> categoryDetails;

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
    }

    public double getSavings() {
        return savings;
    }

    public void setSavings(double savings) {
        this.savings = savings;
    }

    public List<String> getLabels() {
        return labels;
    }

    public void setLabels(List<String> labels) {
        this.labels = labels;
    }

    public List<Double> getIncomeSeries() {
        return incomeSeries;
    }

    public void setIncomeSeries(List<Double> incomeSeries) {
        this.incomeSeries = incomeSeries;
    }

    public List<Double> getExpenseSeries() {
        return expenseSeries;
    }

    public void setExpenseSeries(List<Double> expenseSeries) {
        this.expenseSeries = expenseSeries;
    }

    public Map<String, Double> getSpendingByCategory() {
        return spendingByCategory;
    }

    public void setSpendingByCategory(Map<String, Double> spendingByCategory) {
        this.spendingByCategory = spendingByCategory;
    }

    public List<CategoryDetail> getCategoryDetails() {
        return categoryDetails;
    }

    public void setCategoryDetails(List<CategoryDetail> categoryDetails) {
        this.categoryDetails = categoryDetails;
    }
}
