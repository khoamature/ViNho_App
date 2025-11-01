package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.BudgetService;

public class BudgetRepository {
    public static BudgetService getBudgetService(Context context){
        return AuthApiClient.getClient(context).create(BudgetService.class);
    }
}
