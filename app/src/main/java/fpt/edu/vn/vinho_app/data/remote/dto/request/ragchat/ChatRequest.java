package fpt.edu.vn.vinho_app.data.remote.dto.request.ragchat;

public class ChatRequest {
    private String question;

    public ChatRequest(String question) {
        this.question = question;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }
}
