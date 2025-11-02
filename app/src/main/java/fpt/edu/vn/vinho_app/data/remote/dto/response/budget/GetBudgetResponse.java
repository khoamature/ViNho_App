package fpt.edu.vn.vinho_app.data.remote.dto.response.budget;

import com.google.gson.annotations.SerializedName;

public class GetBudgetResponse {
    private String id;
    private String categoryId;
    private double limitAmount;
    private String month;
    private String categoryType;
    private String categoryName;

    public GetBudgetResponse(String id, String categoryId, double limitAmount, String month, String categoryType, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.month = month;
        this.categoryType = categoryType;
        this.categoryName = categoryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
