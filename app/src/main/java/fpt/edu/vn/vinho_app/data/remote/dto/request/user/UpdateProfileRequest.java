package fpt.edu.vn.vinho_app.data.remote.dto.request.user;

public class UpdateProfileRequest {
    private String fullName;

    public UpdateProfileRequest(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
