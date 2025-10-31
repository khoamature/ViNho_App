package fpt.edu.vn.vinho_app.data.remote.dto.request.auth;

public class ForgotPasswordRequest {
    private String email;

    public ForgotPasswordRequest(String email) {
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
