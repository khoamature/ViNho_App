package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.CreateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.GetPagedTransactionsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.transaction.UpdateTransactionRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.transaction.GetTransactionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface TransactionService {
    @GET("transactions/{id}")
    Call<GetTransactionResponse> getTransaction(@Path("id") int id);

    @POST("transactions/search")
    Call<PagedResponse<GetTransactionResponse>> getTransactions(@Body GetPagedTransactionsRequest request);

    @POST("transactions")
    Call<GetTransactionResponse> createTransaction(@Body CreateTransactionRequest request);

    @PUT("transactions/{id}")
    Call<GetTransactionResponse> updateTransaction(@Path("id") int id, @Body UpdateTransactionRequest request);
}
