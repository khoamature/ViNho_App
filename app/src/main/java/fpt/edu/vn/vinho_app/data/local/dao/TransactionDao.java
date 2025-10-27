package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Transaction;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface TransactionDao {
    @Query("SELECT * FROM transactions")
    Flowable<List<Transaction>> getAll();

    @Query("SELECT * FROM transactions WHERE id = :id")
    Flowable<Transaction> getById(int id);

    @Query("SELECT * FROM transactions WHERE date >= :fromDate AND date <= :toDate")
    Flowable<List<Transaction>> getByDate(String fromDate, String toDate);

    @Query("SELECT * FROM transactions WHERE type = :type")
    Flowable<List<Transaction>> getByType(String type);

    @Insert
    Completable insert(Transaction transaction);

    @Update
    Completable update(Transaction transaction);

    @Delete
    Completable delete(Transaction transaction);
}
