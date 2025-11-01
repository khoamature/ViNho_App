package fpt.edu.vn.vinho_app.data.remote.dto.response.category;

import com.google.gson.annotations.SerializedName;

public class GetCategoryResponse {
    @SerializedName("id")
    private String id;

    @SerializedName("userId")
    private String userId;

    @SerializedName("name")
    private String Name;

    @SerializedName("description")
    private String description;

    @SerializedName("type")
    private int categoryType;

    public GetCategoryResponse(String id, String userId, String name, String description, int categoryType) {
        this.id = id;
        this.userId = userId;
        Name = name;
        this.description = description;
        this.categoryType = categoryType;
    }

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

    public int getCategoryType() {
        return categoryType;
    }

    public void setCategoryType(int categoryType) {
        this.categoryType = categoryType;
    }
}
