package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;

public class ReportRepository {
    public static ReportRepository getReportService(Context context){
        return AuthApiClient.getClient(context).create(ReportRepository.class);
    }
}
