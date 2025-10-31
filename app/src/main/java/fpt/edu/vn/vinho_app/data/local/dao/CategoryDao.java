package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Category;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface CategoryDao {
    @Query("SELECT * FROM categories WHERE deletedAt IS NULL")
    Flowable<List<Category>> getAllCategories();

    @Query("SELECT * FROM categories WHERE id = :id AND deletedAt IS NULL")
    Single<Category> getCategoryById(String id);

    @Query("SELECT * FROM categories WHERE userId = :userId AND deletedAt IS NULL")
    Flowable<List<Category>> getCategoriesByUserId(String userId);

    @Query("SELECT * FROM categories WHERE (userId = :userId OR userId IS NULL) AND type = :type AND deletedAt IS NULL")
    Flowable<List<Category>> getCategoriesByType(String userId, String type);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertCategory(Category category);

    @Update
    Completable updateCategory(Category category);

    @Delete
    Completable deleteCategory(Category category);

    @Query("UPDATE categories SET deletedAt = :timestamp WHERE id = :id")
    Completable softDeleteCategory(String id, long timestamp);
}
