package fpt.edu.vn.vinho_app.data.remote.dto.request.budget;

import com.google.gson.annotations.SerializedName;

import fpt.edu.vn.vinho_app.data.remote.dto.request.base.PagedAndSortedRequest;

public class GetPagedBudgetsRequest extends PagedAndSortedRequest {

    @SerializedName("userId")
    private String userId;

    @SerializedName("categoryId")
    private String categoryId;

    @SerializedName("categoryType")
    private String categoryType;

    @SerializedName("startRangeAmount")
    private Double startRangeAmount;

    @SerializedName("endRangeAmount")
    private Double endRangeAmount;

    @SerializedName("month")
    private String month;

    @SerializedName("fromMonth")
    private String fromMonth;

    @SerializedName("toMonth")
    private String toMonth;

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

    public String getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(String categoryType) {
        this.categoryType = categoryType;
    }

    public Double getStartRangeAmount() {
        return startRangeAmount;
    }

    public void setStartRangeAmount(Double startRangeAmount) {
        this.startRangeAmount = startRangeAmount;
    }

    public Double getEndRangeAmount() {
        return endRangeAmount;
    }

    public void setEndRangeAmount(Double endRangeAmount) {
        this.endRangeAmount = endRangeAmount;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public String getFromMonth() {
        return fromMonth;
    }

    public void setFromMonth(String fromMonth) {
        this.fromMonth = fromMonth;
    }

    public String getToMonth() {
        return toMonth;
    }

    public void setToMonth(String toMonth) {
        this.toMonth = toMonth;
    }
}
