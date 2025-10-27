package fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat;

public class ChatRequest {
    private String question;
    private String conversationId;

    public ChatRequest(String question, String conversationId) {
        this.question = question;
        this.conversationId = conversationId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }
}
