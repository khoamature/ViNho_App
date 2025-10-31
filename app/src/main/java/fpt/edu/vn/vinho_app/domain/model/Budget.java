package fpt.edu.vn.vinho_app.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "budgets",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.CASCADE) // Delete budget if category is deleted
        },
        indices = {@Index("userId"), @Index("categoryId"), @Index("month")})
public class Budget {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @NonNull
    public String userId; // Mapped from Guid

    @NonNull
    public String categoryId; // Mapped from Guid

    public double limitAmount; // Mapped from decimal

    @NonNull
    public Long month; // Mapped from DateOnly (store as timestamp of first day of month)

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
    public Budget() {
        // Default constructor for Room
    }

    // --- Constructor for convenience ---
    public Budget(@NonNull String userId, @NonNull String categoryId, double limitAmount, @NonNull Long month) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.categoryId = categoryId;
        this.limitAmount = limitAmount;
        this.month = month;
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

    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(@NonNull String categoryId) {
        this.categoryId = categoryId;
    }

    public double getLimitAmount() {
        return limitAmount;
    }

    public void setLimitAmount(double limitAmount) {
        this.limitAmount = limitAmount;
    }

    @NonNull
    public Long getMonth() {
        return month;
    }

    public void setMonth(@NonNull Long month) {
        this.month = month;
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
