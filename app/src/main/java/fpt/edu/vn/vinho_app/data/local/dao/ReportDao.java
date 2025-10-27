package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Report;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;

@Dao
public interface ReportDao {
    @Query("select * from reports")
    Flowable<List<Report>> getAll();

    @Query("select * from reports where id = :id")
    Flowable<Report> getById(int id);

    @Query("Select * from reports where month = :month")
    Flowable<List<Report>> getByMonth(String month);

    @Insert
    Completable insert(Report report);

    @Update
    Completable update(Report report);

    @Delete
    Completable delete(Report report);
}
