package fpt.edu.vn.vinho_app.data.remote.dto.response.transaction;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class TransactionApiResponse {
    @SerializedName("totalCount")
    private int totalCount;

    @SerializedName("page")
    private int page;

    @SerializedName("pageSize")
    private int pageSize;

    @SerializedName("totalPages")
    private int totalPages;

    // Quan trọng: Payload giờ là một List<TransactionSummaryResponse>
    @SerializedName("payload")
    private List<TransactionSummaryResponse> payload;

    @SerializedName("isSuccess")
    private boolean isSuccess;

    @SerializedName("message")
    private String message;

    // Getters and setters
    public int getTotalCount() {
        return totalCount;
    }

    public List<TransactionSummaryResponse> getPayload() {
        return payload;
    }

    public boolean isSuccess() {
        return isSuccess;
    }

    public String getMessage() {
        return message;
    }
}
