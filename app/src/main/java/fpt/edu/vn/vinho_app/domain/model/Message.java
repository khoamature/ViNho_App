package fpt.edu.vn.vinho_app.domain.model;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

@Entity(tableName = "messages",
        foreignKeys = @ForeignKey(entity = Conversation.class,
                parentColumns = "id",
                childColumns = "conversationId",
                onDelete = ForeignKey.CASCADE), // Delete messages if conversation is deleted
        indices = {@Index("conversationId"), @Index("createdAt")})
public class Message {

    @PrimaryKey
    @NonNull
    public String id; // Mapped from Guid

    @NonNull
    public String conversationId; // Mapped from Guid

    @NonNull // Assuming SenderType is never null
    public String sender; // Mapped from enum SenderType (store as String, e.g., "User", "AI")

    @ColumnInfo(typeAffinity = ColumnInfo.TEXT, defaultValue = "") // Ensure TEXT type and default
    public String content = "";

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

    public Message() {}

    // Add constructor, getters, setters...

    public Message(@NonNull String id, @NonNull String conversationId, @NonNull String sender, String content, @Nullable Long deletedAt, @NonNull Long createdAt, @NonNull Long updatedAt, boolean isSynced, @Nullable Long syncedAt) {
        this.id = id;
        this.conversationId = conversationId;
        this.sender = sender;
        this.content = content;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.isSynced = isSynced;
        this.syncedAt = syncedAt;
    }

    @NonNull
    public String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @NonNull
    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(@NonNull String conversationId) {
        this.conversationId = conversationId;
    }

    @NonNull
    public String getSender() {
        return sender;
    }

    public void setSender(@NonNull String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
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