package fpt.edu.vn.vinho_app.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.time.Instant;

@Entity(tableName = "users")
public class User {

    @PrimaryKey
    @NonNull
    public String userId = ""; // Mapped from Guid

    @ColumnInfo(defaultValue = "") // Ensure default for non-null String
    public String fullName = "";

    @Nullable // Assuming email might be nullable, adjust if needed
    public String email; // Added for display/info

    // IsActive might be useful locally to disable a user temporarily
    @ColumnInfo(defaultValue = "1") // SQLite uses 1 for true
    public boolean isActive = true;

    // --- Audit, Soft Delete, Sync Fields ---
    @Nullable
    public Long deletedAt; // Nullable Long for DateTime?

    @NonNull
    public Long createdAt = System.currentTimeMillis(); // NonNull Long for DateTime

    @NonNull
    public Long updatedAt = System.currentTimeMillis(); // NonNull Long for DateTime

    @ColumnInfo(defaultValue = "0") // SQLite uses 0 for false
    public boolean isSynced = false;

    @Nullable
    public Long syncedAt; // Nullable Long for DateTime?

    // --- Room requires a no-arg constructor ---
    public User() {}

    // --- Constructor for convenience ---
    public User(@NonNull String userId, String fullName, @Nullable String email) {
        this.userId = userId;
        this.fullName = fullName != null ? fullName : "";
        this.email = email;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.isActive = true;
        this.isSynced = false;
    }

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    @Nullable
    public String getEmail() {
        return email;
    }

    public void setEmail(@Nullable String email) {
        this.email = email;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
