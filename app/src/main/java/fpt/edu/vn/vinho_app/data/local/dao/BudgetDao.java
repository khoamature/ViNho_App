package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Budget;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface BudgetDao {
    @Query("SELECT * FROM budgets WHERE deletedAt IS NULL")
    Flowable<List<Budget>> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE id = :id AND deletedAt IS NULL")
    Single<Budget> getBudgetById(String id);

    @Query("SELECT * FROM budgets WHERE userId = :userId AND deletedAt IS NULL")
    Flowable<List<Budget>> getBudgetsByUserId(String userId);

    @Query("SELECT * FROM budgets WHERE month = :month AND userId = :userId AND deletedAt IS NULL")
    Flowable<List<Budget>> getBudgetsByMonth(String userId, Long month);

    @Query("SELECT * FROM budgets WHERE categoryId = :categoryId AND userId = :userId AND deletedAt IS NULL")
    Flowable<List<Budget>> getBudgetsByCategoryId(String userId, String categoryId);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertBudget(Budget budget);

    @Update
    Completable updateBudget(Budget budget);

    @Delete
    Completable deleteBudget(Budget budget);

    @Query("UPDATE budgets SET deletedAt = :timestamp WHERE id = :id")
    Completable softDeleteBudget(String id, long timestamp);
}
