package fpt.edu.vn.vinho_app.data.repository;

import android.content.Context;

import fpt.edu.vn.vinho_app.data.remote.retrofit.AuthApiClient;
import fpt.edu.vn.vinho_app.data.remote.service.CategoryService;

public class CategoryRepository {
    public static CategoryService getCategoryService(Context context){
        return AuthApiClient.getClient(context).create(CategoryService.class);
    }
}
