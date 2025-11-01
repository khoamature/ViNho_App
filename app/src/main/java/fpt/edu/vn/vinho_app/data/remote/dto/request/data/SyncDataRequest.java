package fpt.edu.vn.vinho_app.data.remote.dto.request.data;

import com.google.gson.annotations.SerializedName;

public class SyncDataRequest {
    @SerializedName("UserId")
    private String userId;

    @SerializedName("LastSyncedAt")
    private String lastSyncedAt;

    public SyncDataRequest(String userId, String lastSyncedAt) {
        this.userId = userId;
        this.lastSyncedAt = lastSyncedAt;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getLastSyncedAt() {
        return lastSyncedAt;
    }

    public void setLastSyncedAt(String lastSyncedAt) {
        this.lastSyncedAt = lastSyncedAt;
    }
}
