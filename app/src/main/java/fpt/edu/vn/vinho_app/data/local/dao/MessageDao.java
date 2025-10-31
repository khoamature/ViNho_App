package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Message;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface MessageDao {
    @Query("SELECT * FROM messages WHERE conversationId = :conversationId ORDER BY createdAt ASC")
    Flowable<List<Message>> getMessagesForConversation(String conversationId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMessage(Message message);

    @Update
    Completable updateMessage(Message message);
}

