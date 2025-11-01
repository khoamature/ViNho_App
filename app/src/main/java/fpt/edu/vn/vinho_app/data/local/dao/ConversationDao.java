package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Conversation;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ConversationDao {
    @Query("SELECT * FROM conversations WHERE userId = :userId AND deletedAt IS NULL ORDER BY updatedAt DESC")
    Flowable<List<Conversation>> getAllConversations(String userId);

    @Query("SELECT * FROM conversations WHERE id = :id AND deletedAt IS NULL")
    Single<Conversation> getConversationById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Conversation conversation);

    @Update
    Completable updateConversation(Conversation conversation);

    @Query("UPDATE conversations SET deletedAt = :timestamp WHERE id = :id")
    Completable softDeleteConversation(String id, long timestamp);
}
