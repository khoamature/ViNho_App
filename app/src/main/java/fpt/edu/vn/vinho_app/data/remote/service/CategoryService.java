package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.category.GetPagedCategoriesRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.category.GetCategoryResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface CategoryService {

    @GET("categories")
    Call<PagedResponse<GetCategoryResponse>> getCategories(
            @Query("Type") String type, // Tham số này có thể là "Expense", "Income", hoặc null
            @Query("UserId") String userId
    );
}
