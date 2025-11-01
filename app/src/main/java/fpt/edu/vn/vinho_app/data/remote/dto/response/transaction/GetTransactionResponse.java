package fpt.edu.vn.vinho_app.data.remote.dto.response.transaction;

import com.google.gson.annotations.SerializedName;

public class GetTransactionResponse {

    @SerializedName("id")
    private String id;
    @SerializedName("userId")
    private String userId;
    @SerializedName("categoryId")
    private String categoryId;
    @SerializedName("amount")
    private double amount;
    @SerializedName("description")
    private String description;
    @SerializedName("transactionDate")
    private String transactionDate;
    @SerializedName("isNotificationEnabled")
    private boolean isNotificationEnabled;
    @SerializedName("notificationSentAt")
    private String notificationSentAt;
    @SerializedName("isAddedToReports")
    private boolean isAddedToReports;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
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

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }

    public String getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(String notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }

    public boolean isAddedToReports() {
        return isAddedToReports;
    }

    public void setAddedToReports(boolean addedToReports) {
        isAddedToReports = addedToReports;
    }
}
