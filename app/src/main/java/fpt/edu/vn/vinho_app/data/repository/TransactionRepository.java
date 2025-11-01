package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.TransactionService;

public class TransactionRepository {
    public static TransactionService getTransactionService(Context context) {
        return AuthApiClient.getClient(context).create(TransactionService.class);
    }
}
