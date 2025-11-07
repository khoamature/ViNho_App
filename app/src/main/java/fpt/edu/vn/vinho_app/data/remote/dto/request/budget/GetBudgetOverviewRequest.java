package fpt.edu.vn.vinho_app.data.remote.dto.request.budget;

public class GetBudgetOverviewRequest {
    private String userId;
    private String month;

    public GetBudgetOverviewRequest(String userId, String month) {
        this.userId = userId;
        this.month = month;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }
}
