package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.conversation.GetConversationsRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.base.PagedResponse;
import fpt.edu.vn.vinho_app.data.remote.dto.response.conversation.GetConversationResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ConversationService {
    @POST("conversations/search")
    Call<PagedResponse<GetConversationResponse>> getConversations(@Body GetConversationsRequest request);
}
