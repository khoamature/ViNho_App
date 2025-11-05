package fpt.edu.vn.vinho_app.data.remote.dto.response.report;

import java.util.List;

public class AIInsightResponse {
    private String id;
    private String userId;
    private double totalIncome;
    private double totalExpense;
    private double savings;
    private String budgetAlert;
    private String spendingPattern;
    private String savingsRate;
    private String monthlyGoal;
    private List<FinancialTip> financialTips;

    // Getters and Setters cho tất cả các trường
    // Ví dụ:
    public String getBudgetAlert() { return budgetAlert; }
    public String getSpendingPattern() { return spendingPattern; }
    public String getSavingsRate() { return savingsRate; }
    public String getMonthlyGoal() { return monthlyGoal; }
    public List<FinancialTip> getFinancialTips() { return financialTips; }
}
