package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.conversation.GetConversationsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.request.conversation.RenameConversationRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.ConversationResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.GetConversationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ConversationService {

    @GET("conversations")
    Call<PagedResponse<ConversationResponse>> getConversations(
            @Query("UserId") String userId,
            @Query("PageSize") int pageSize,
            @Query("SortBy") String sortBy,
            @Query("SortDescending") boolean sortDescending
    );

    @PUT("conversations/{id}")
    Call<Void> renameConversation(
            @Path("id") String conversationId,
            @Body RenameConversationRequest request
    );

    @DELETE("conversations/{id}")
    Call<Void> deleteConversation(@Path("id") String conversationId);
    @POST("conversations/search")
    Call<PagedResponse<GetConversationResponse>> getConversations(@Body GetConversationsRequest request);
}
