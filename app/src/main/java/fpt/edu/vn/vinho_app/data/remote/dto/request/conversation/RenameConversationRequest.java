package fpt.edu.vn.vinho_app.data.remote.dto.request.conversation;

public class RenameConversationRequest {
    private String title;

    public RenameConversationRequest(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
