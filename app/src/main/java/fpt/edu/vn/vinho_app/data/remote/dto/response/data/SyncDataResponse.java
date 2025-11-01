package fpt.edu.vn.vinho_app.data.remote.dto.response.data;

import com.google.gson.annotations.SerializedName;

import java.util.List;

import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.GetConversationResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.message.GetMessageResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.GetReportResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.user.GetProfileResponse;

public class SyncDataResponse {
    @SerializedName("lastSyncDate")
    private String lastSyncDate;

    @SerializedName("users")
    private List<GetProfileResponse> users;

    @SerializedName("transactions")
    private List<GetTransactionResponse> transactions;

    @SerializedName("categories")
    private List<GetCategoryResponse> categories;

    @SerializedName("budgets")
    private List<GetBudgetResponse> budgets;

    @SerializedName("reports")
    private List<GetReportResponse> reports;

    @SerializedName("conversations")
    private List<GetConversationResponse> conversations;

    @SerializedName("messages")
    private List<GetMessageResponse> messages;

    public String getLastSyncDate() {
        return lastSyncDate;
    }

    public List<GetProfileResponse> getUsers() {
        return users;
    }

    public List<GetTransactionResponse> getTransactions() {
        return transactions;
    }

    public List<GetCategoryResponse> getCategories() {
        return categories;
    }

    public List<GetBudgetResponse> getBudgets() {
        return budgets;
    }

    public List<GetReportResponse> getReports() {
        return reports;
    }

    public List<GetConversationResponse> getConversations() {
        return conversations;
    }

    public List<GetMessageResponse> getMessages() {
        return messages;
    }
}
