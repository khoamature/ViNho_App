package fpt.edu.vn.vinho_app.data.remote.service;

import java.util.List;

import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.CreateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.GetPagedTransactionsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.UpdateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionApiResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.TransactionSummaryResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface TransactionService {
    @GET("transactions/{id}")
    Call<GetTransactionResponse> getTransaction(@Path("id") int id);

    @POST("transactions/search")
    Call<PagedResponse<GetTransactionResponse>> getTransactions(@Body GetPagedTransactionsRequest request);

    @POST("transactions")
    Call<Void> createTransaction(@Body CreateTransactionRequest request);

    @PUT("transactions/{id}")
    Call<Void> updateTransaction(@Path("id") String transactionId, @Body UpdateTransactionRequest request);

    @DELETE("transactions/{id}")
    Call<Void> deleteTransaction(@Path("id") String transactionId);

    @GET("transactions")
    Call<TransactionApiResponse> getTransactions(
            @Query("UserId") String userId,
            @Query("CategoryType") String categoryType,
            @Query("CategoryId") String categoryId,
            @Query("Description") String description,
            @Query("FromDate") String fromDate,
            @Query("ToDate") String toDate,
            @Query("PageSize") Integer pageSize
    );
}
