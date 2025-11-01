package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.ReportService;

public class ReportRepository {
    public static ReportService getReportService(Context context){
        return AuthApiClient.getClient(context).create(ReportService.class);
    }
}
