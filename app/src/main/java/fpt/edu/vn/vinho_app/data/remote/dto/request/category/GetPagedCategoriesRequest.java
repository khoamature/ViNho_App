package fpt.edu.vn.vinho_app.data.remote.dto.request.category;

import com.google.gson.annotations.SerializedName;

import fpt.edu.vn.vinho_app.data.remote.dto.request.base.PagedAndSortedRequest;

public class GetPagedCategoriesRequest extends PagedAndSortedRequest {
    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String Name;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private Integer categoryType;

    public GetPagedCategoriesRequest(String userId) {
        this.userId = userId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(Integer categoryType) {
        this.categoryType = categoryType;
    }
}
