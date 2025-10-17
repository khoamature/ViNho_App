package fpt.edu.vn.vinho_app.daos;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.models.Budget;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface BudgetDao {
    @Query("SELECT * FROM budgets")
    Flowable<List<Budget>> GetAll();

    @Query("SELECT * FROM budgets WHERE id = :id")
    Flowable<Budget> GetById(int id);

    @Query("SELECT * FROM budgets WHERE month LIKE :month")
    Flowable<List<Budget>> GetByMonth(String month);

    @Query("SELECT * FROM budgets WHERE category_name LIKE :categoryName")
    Flowable<List<Budget>> GetByCategoryName(String categoryName);

    @Insert
    Completable Insert(Budget budget);

    @Update
    Completable Update(Budget budget);

    @Delete
    Completable Delete(Budget budget);
}
