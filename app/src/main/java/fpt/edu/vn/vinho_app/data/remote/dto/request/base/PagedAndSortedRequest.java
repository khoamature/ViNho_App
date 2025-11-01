package fpt.edu.vn.vinho_app.data.remote.dto.request.base;

import com.google.gson.annotations.SerializedName;

public class PagedAndSortedRequest {
    @SerializedName("PageNumber")
    protected int pageNumber = 1;
    @SerializedName("PageSize")
    protected int pageSize = 10;
    @SerializedName("SortBy")
    protected String sortBy;
    @SerializedName("SortDescending")
    protected boolean sortDescending = false;

    public PagedAndSortedRequest() {
    }

    public PagedAndSortedRequest(int pageNumber, int pageSize, String sortBy, boolean sortDescending) {
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.sortBy = sortBy;
        this.sortDescending = sortDescending;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public String getSortBy() {
        return sortBy;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public boolean isSortDescending() {
        return sortDescending;
    }

    public void setSortDescending(boolean sortDescending) {
        this.sortDescending = sortDescending;
    }
}
