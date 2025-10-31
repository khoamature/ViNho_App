package fpt.edu.vn.vinho_app.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@Entity(tableName = "conversations",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId")})
public class Conversation {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @NonNull
    public String userId; // Mapped from Guid

    @ColumnInfo(defaultValue = "")
    public String title = "";

    // --- Audit, Soft Delete, Sync Fields ---
    @Nullable
    public Long deletedAt;

    @NonNull
    public Long createdAt;

    @NonNull
    public Long updatedAt;

    @ColumnInfo(defaultValue = "0")
    public boolean isSynced = false; // Needs sync when created locally

    @Nullable
    public Long syncedAt;

    public Conversation() {}

    // Add constructor, getters, setters...

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Nullable
    public Long getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(@Nullable Long deletedAt) {
        this.deletedAt = deletedAt;
    }

    @NonNull
    public Long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(@NonNull Long createdAt) {
        this.createdAt = createdAt;
    }

    @NonNull
    public Long getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(@NonNull Long updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isSynced() {
        return isSynced;
    }

    public void setSynced(boolean synced) {
        isSynced = synced;
    }

    @Nullable
    public Long getSyncedAt() {
        return syncedAt;
    }

    public void setSyncedAt(@Nullable Long syncedAt) {
        this.syncedAt = syncedAt;
    }

    public Conversation(@NonNull String id, @NonNull String userId, String title, @Nullable Long deletedAt, @NonNull Long createdAt, @NonNull Long updatedAt, boolean isSynced, @Nullable Long syncedAt) {
        this.id = id;
        this.userId = userId;
        this.title = title;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isSynced = isSynced;
        this.syncedAt = syncedAt;
    }
}
