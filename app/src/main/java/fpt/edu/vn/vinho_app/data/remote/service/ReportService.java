package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.report.HomePageReportResponse;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

public interface ReportService {
    @GET("reports/home-page/user/{userId}")
    Call<BaseResponse<HomePageReportResponse>> getHomePageReport(@Path("userId") String userId);
}
