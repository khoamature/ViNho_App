package fpt.edu.vn.vinho_app.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "transactions",
        foreignKeys = {
                @ForeignKey(entity = User.class,
                        parentColumns = "userId",
                        childColumns = "userId",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Category.class,
                        parentColumns = "id",
                        childColumns = "categoryId",
                        onDelete = ForeignKey.RESTRICT) // Prevent deleting Category if Transactions exist
        },
        indices = {@Index("userId"), @Index("categoryId"), @Index("transactionDate")})
public class Transaction {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @NonNull
    public String userId; // Mapped from Guid

    @NonNull
    public String categoryId; // Mapped from Guid

    public double amount; // Mapped from decimal

    @ColumnInfo(defaultValue = "")
    public String description = "";

    @NonNull
    public Long transactionDate; // Mapped from DateTime

    @ColumnInfo(defaultValue = "0")
    public boolean isNotificationEnabled = false;

    @Nullable
    public Long notificationSentAt; // Mapped from DateTime?

    @ColumnInfo(defaultValue = "0")
    public boolean isAddedToReports = false;

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
    public Transaction() {
        // Default constructor for Room
    }

    // --- Constructor for convenience ---
    public Transaction(@NonNull String userId, @NonNull String categoryId, double amount, @NonNull Long transactionDate, String description) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.categoryId = categoryId;
        this.amount = amount;
        this.transactionDate = transactionDate;
        this.description = description;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.isSynced = false;
        this.isNotificationEnabled = false;
        this.isAddedToReports = false;
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

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @NonNull
    public Long getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(@NonNull Long transactionDate) {
        this.transactionDate = transactionDate;
    }

    public boolean isNotificationEnabled() {
        return isNotificationEnabled;
    }

    public void setNotificationEnabled(boolean notificationEnabled) {
        isNotificationEnabled = notificationEnabled;
    }

    @Nullable
    public Long getNotificationSentAt() {
        return notificationSentAt;
    }

    public void setNotificationSentAt(@Nullable Long notificationSentAt) {
        this.notificationSentAt = notificationSentAt;
    }

    public boolean isAddedToReports() {
        return isAddedToReports;
    }

    public void setAddedToReports(boolean addedToReports) {
        isAddedToReports = addedToReports;
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
