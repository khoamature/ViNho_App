package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import fpt.edu.vn.vinho_app.domain.model.User;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface UserDao {
    @Query("SELECT * FROM users WHERE userId = :userId AND deletedAt IS NULL")
    Single<User> getUserById(String userId);

    @Query("SELECT * FROM users WHERE email = :email AND deletedAt IS NULL")
    Single<User> getUserByEmail(String email);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertUser(User user);

    @Update
    Completable updateUser(User user);

    @Query("UPDATE users SET deletedAt = :timestamp WHERE userId = :userId")
    Completable softDeleteUser(String userId, long timestamp);
}
