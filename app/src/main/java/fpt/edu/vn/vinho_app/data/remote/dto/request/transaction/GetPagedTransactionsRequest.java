package fpt.edu.vn.vinho_app.data.remote.dto.request.transaction;

import com.google.gson.annotations.SerializedName;

import fpt.edu.vn.vinho_app.data.remote.dto.request.base.PagedAndSortedRequest;

public class GetPagedTransactionsRequest extends PagedAndSortedRequest {
    @SerializedName("UserId")
    private String userId;
    @SerializedName("CategoryId")
    private String categoryId;
    @SerializedName("CategoryType")
    private Integer categoryType;
    @SerializedName("StartRangeAmount")
    private Double startRangeAmount;
    @SerializedName("EndRangeAmount")
    private Double endRangeAmount;
    @SerializedName("Description")
    private String description = "";
    @SerializedName("Date")
    private String date;
    @SerializedName("FromDate")
    private String fromDate;
    @SerializedName("ToDate")
    private String toDate;

    public GetPagedTransactionsRequest(String userId) {
        this.userId = userId;
    }

    public GetPagedTransactionsRequest(int pageNumber, int pageSize, String sortBy, boolean sortDescending, String userId, String categoryId, Integer categoryType, Double startRangeAmount, Double endRangeAmount, String description, String date, String fromDate, String toDate) {
        super(pageNumber, pageSize, sortBy, sortDescending);
        this.userId = userId;
        this.categoryId = categoryId;
        this.categoryType = categoryType;
        this.startRangeAmount = startRangeAmount;
        this.endRangeAmount = endRangeAmount;
        this.description = description;
        this.date = date;
        this.fromDate = fromDate;
        this.toDate = toDate;
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

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getFromDate() {
        return fromDate;
    }

    public void setFromDate(String fromDate) {
        this.fromDate = fromDate;
    }

    public String getToDate() {
        return toDate;
    }

    public void setToDate(String toDate) {
        this.toDate = toDate;
    }
}
