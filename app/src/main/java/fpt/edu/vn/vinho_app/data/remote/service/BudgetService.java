package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.CreateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.GetPagedBudgetsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.UpdateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.BudgetOverviewResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface BudgetService {
    @POST("budgets/search")
    Call<PagedResponse<GetBudgetResponse>> getBudgets(@Body GetPagedBudgetsRequest request);

    @POST("budgets")
    Call<BaseResponse<String>> createBudget(@Body CreateBudgetRequest request);

    @POST("/api/budgets/over-view")
    Call<BaseResponse<BudgetOverviewResponse>> getBudgetOverview(@Body GetPagedBudgetsRequest request);

    @PUT("/api/budgets/{id}")
    Call<BaseResponse<String>> updateBudget(@Path("id") String budgetId, @Body UpdateBudgetRequest request);

    @DELETE("/api/budgets/{id}")
    Call<Void> deleteBudget(@Path("id") String budgetId);

    @GET("/api/budgets/{id}")
    Call<BaseResponse<GetBudgetResponse>> getBudgetById(@Path("id") String budgetId);

}
