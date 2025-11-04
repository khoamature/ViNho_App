package fpt.edu.vn.vinho_app.data.remote.dto.response.report;

import java.util.List;
import java.util.Map;

public class HomePageReportResponse {
    private double totalIncome;
    private double totalExpense;
    private double balance;
    private List<RecentTransaction> recentTransactions;
    private Map<String, Double> expenseByCategory;
    private Map<String, Double> expenseByCategoryPercentage;

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

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public List<RecentTransaction> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<RecentTransaction> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }

    public Map<String, Double> getExpenseByCategory() {
        return expenseByCategory;
    }

    public void setExpenseByCategory(Map<String, Double> expenseByCategory) {
        this.expenseByCategory = expenseByCategory;
    }

    public Map<String, Double> getExpenseByCategoryPercentage() {
        return expenseByCategoryPercentage;
    }

    public void setExpenseByCategoryPercentage(Map<String, Double> expenseByCategoryPercentage) {
        this.expenseByCategoryPercentage = expenseByCategoryPercentage;
    }
}
