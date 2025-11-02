package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.CreateBudgetRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.budget.GetPagedBudgetsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.budget.GetBudgetResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface BudgetService {
    @POST("budgets/search")
    Call<PagedResponse<GetBudgetResponse>> getBudgets(@Body GetPagedBudgetsRequest request);

    @POST("budgets")
    Call<BaseResponse<String>> createBudget(@Body CreateBudgetRequest request);
}
