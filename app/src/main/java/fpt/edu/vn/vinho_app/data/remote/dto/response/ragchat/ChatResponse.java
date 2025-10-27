package fpt.edu.vn.vinho_app.data.remote.dto.response.ragchat;

import fpt.edu.vn.vinho_app.data.remote.dto.response.base.BaseResponse;

public class ChatResponse extends BaseResponse<ChatResponse.ChatResponsePayload> {

    public static class ChatResponsePayload {
        private String answer;

        public String getAnswer() {
            return answer;
        }

        public void setAnswer(String answer) {
            this.answer = answer;
        }
    }
}
