package fpt.edu.vn.vinho_app.data.remote.service;

import fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat.ChatRequest;
import fpt.edu.vn.vinho_app.data.remote.dto.response.ragchat.ChatResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface RagChatService {
    @POST("ragchat/chat")
    Call<ChatResponse> chat(@Body ChatRequest request);
}
