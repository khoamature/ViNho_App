package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat.ChatRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.ragchat.ChatResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.ragchat.MessageResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface ChatService {
    @POST("chats")
    Call<BaseResponse<ChatResponse>> postChatMessage(@Body ChatRequest request);

    @GET("messages")
    Call<PagedResponse<MessageResponse>> getMessagesForConversation(
            @Query("ConversationId") String conversationId,
            @Query("PageSize") int pageSize,
            @Query("SortBy") String sortBy,
            @Query("SortDescending") boolean sortDescending
    );
}
