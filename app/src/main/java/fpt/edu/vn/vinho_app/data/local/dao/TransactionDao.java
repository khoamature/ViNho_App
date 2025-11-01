package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Transaction;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions WHERE userId = :userId AND deletedAt IS NULL ORDER BY transactionDate DESC")
    Flowable<List<Transaction>> getAllTransactions(String userId);

    @Query("SELECT * FROM transactions WHERE id = :id AND deletedAt IS NULL")
    Single<Transaction> getTransactionById(String id);

    @Query("SELECT * FROM transactions WHERE userId = :userId AND transactionDate >= :fromDate AND transactionDate <= :toDate AND deletedAt IS NULL ORDER BY transactionDate DESC")
    Flowable<List<Transaction>> getTransactionsByDateRange(String userId, Long fromDate, Long toDate);

    @Query("SELECT * FROM transactions WHERE categoryId = :categoryId AND userId = :userId AND deletedAt IS NULL ORDER BY transactionDate DESC")
    Flowable<List<Transaction>> getTransactionsByCategoryId(String userId, String categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Transaction transaction);

    @Update
    Completable updateTransaction(Transaction transaction);

    @Delete
    Completable deleteTransaction(Transaction transaction);

    @Query("UPDATE transactions SET deletedAt = :timestamp WHERE id = :id")
    Completable softDeleteTransaction(String id, long timestamp);
}
