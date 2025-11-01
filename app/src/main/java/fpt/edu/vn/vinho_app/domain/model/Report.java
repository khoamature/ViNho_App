package fpt.edu.vn.vinho_app.domain.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.UUID;

@Entity(tableName = "reports",
        foreignKeys = @ForeignKey(entity = User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("userId"), @Index("month")})
public class Report {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @NonNull
    public String userId; // Mapped from Guid

    @NonNull
    public String month;

    @ColumnInfo(defaultValue = "")
    public String aiSuggestion = "";

    public double totalIncome; // Mapped from decimal
    public double totalExpense; // Mapped from decimal

    // --- Audit, Soft Delete, Sync Fields ---
    @Nullable
    public Long deletedAt;

    @NonNull
    public Long createdAt;

    @NonNull
    public Long updatedAt;

    // Reports are usually generated server-side and downloaded.
    // 'isSynced' might represent if it's up-to-date with the server version.
    @ColumnInfo(defaultValue = "1") // Assume synced when downloaded
    public boolean isSynced = true;

    @Nullable
    public Long syncedAt; // Timestamp when downloaded/updated from server

    // --- Room requires a no-arg constructor ---
    public Report() {
        // Default constructor for Room
    }

    // --- Constructor for convenience ---
    public Report(@NonNull String userId, @NonNull String month, String aiSuggestion, double totalIncome, double totalExpense) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.month = month;
        this.aiSuggestion = aiSuggestion;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
        long now = System.currentTimeMillis();
        this.createdAt = now;
        this.updatedAt = now;
        this.syncedAt = now;
        this.isSynced = true;
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
    public String getMonth() {
        return month;
    }

    public void setMonth(@NonNull String month) {
        this.month = month;
    }

    public String getAiSuggestion() {
        return aiSuggestion;
    }

    public void setAiSuggestion(String aiSuggestion) {
        this.aiSuggestion = aiSuggestion;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public void setTotalIncome(double totalIncome) {
        this.totalIncome = totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }

    public void setTotalExpense(double totalExpense) {
        this.totalExpense = totalExpense;
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
