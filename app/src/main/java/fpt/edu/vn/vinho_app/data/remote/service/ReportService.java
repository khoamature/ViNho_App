package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.AIInsightResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.HomePageReportResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.StatisticsReportResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ReportService {
    @GET("reports/home-page/user/{userId}")
    Call<BaseResponse<HomePageReportResponse>> getHomePageReport(@Path("userId") String userId);

    @GET("reports/statistics")
    Call<BaseResponse<StatisticsReportResponse>> getStatisticsReport(
            @Query("UserId") String userId,
            @Query("Range") String range // "Daily", "Weekly", "Monthly"
    );

    @GET("reports/aiinsight/users/{userId}")
    Call<BaseResponse<AIInsightResponse>> getAIInsight(@Path("userId") String userId);
}
