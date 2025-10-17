package fpt.edu.vn.vinho_app.daos;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.models.Category;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

public interface CategoryDao {
    @Query("SELECT * FROM categories")
    Flowable<List<Category>> getAll();

    @Insert
    Completable insert(Category category);

    @Update
    Completable update(Category category);

    @Delete
    Completable delete(Category category);
}
