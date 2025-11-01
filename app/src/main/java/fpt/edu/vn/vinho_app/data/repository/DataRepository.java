package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.DataService;

public class DataRepository {
    public static DataService getDataService(Context context){
        return AuthApiClient.getClient(context).create(DataService.class);
    }
}
