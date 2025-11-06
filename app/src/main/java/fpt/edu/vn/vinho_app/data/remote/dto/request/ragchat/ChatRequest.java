package fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat;

public class ChatRequest {
    private String userId;
    private String conversationId;
    private String question;
    public ChatRequest(String userId, String question) {
        this.userId = userId;
        this.question = question;
    }
    public ChatRequest(String userId, String conversationId, String question) {
        this.userId = userId;
        this.conversationId = conversationId;
        this.question = question;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
