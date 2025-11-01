package fpt.edu.vn.vinho_app.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "categories",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE), // Adjust onDelete as needed
        indices = {@Index("userId")})
public class Category {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @Nullable // For global categories vs user-specific
    public String userId; // Mapped from Guid?

    @ColumnInfo(defaultValue = "")
    public String name = "";

    @ColumnInfo(defaultValue = "")
    public String description = "";

    @NonNull
    public String type; // Mapped from enum CategoryType (store as String)

    // --- Audit, Soft Delete, Sync Fields ---
    @Nullable
    public Long deletedAt;

    @NonNull
    public Long createdAt;

    @NonNull
    public Long updatedAt;

    @ColumnInfo(defaultValue = "0")
    public boolean isSynced = false;

    @Nullable
    public Long syncedAt;

    // --- Room requires a no-arg constructor ---
    public Category() {
        // Default constructor for Room
    }

    // --- Constructor for convenience ---
    public Category(@Nullable String userId, @NonNull String name, @NonNull String type, String description) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.name = name;
        this.type = type;
        this.description = description;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.isSynced = false;
    }

    // --- Getters and Setters ---
    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Nullable
    public String getUserId() {
        return userId;
    }

    public void setUserId(@Nullable String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public String getType() {
        return type;
    }

    public void setType(@NonNull String type) {
        this.type = type;
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
}
