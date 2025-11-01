package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;

public class BudgetRepository {
    public static BudgetRepository getBudgetService(Context context){
        return AuthApiClient.getClient(context).create(BudgetRepository.class);
    }
}
