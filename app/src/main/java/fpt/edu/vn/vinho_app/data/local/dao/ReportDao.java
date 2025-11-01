package fpt.edu.vn.vinho_app.data.local.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import fpt.edu.vn.vinho_app.domain.model.Report;
import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ReportDao {
    @Query("SELECT * FROM reports WHERE userId = :userId AND deletedAt IS NULL")
    Flowable<List<Report>> getAllReports(String userId);

    @Query("SELECT * FROM reports WHERE id = :id AND deletedAt IS NULL")
    Single<Report> getReportById(String id);

    @Query("SELECT * FROM reports WHERE month = :month AND userId = :userId AND deletedAt IS NULL")
    Flowable<List<Report>> getReportsByMonth(String userId, Long month);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insert(Report report);

    @Update
    Completable updateReport(Report report);

    @Delete
    Completable deleteReport(Report report);

    @Query("UPDATE reports SET deletedAt = :timestamp WHERE id = :id")
    Completable softDeleteReport(String id, long timestamp);
}
