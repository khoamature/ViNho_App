package fpt.edu.vn.vinho_app.data.remote.dto.request.auth;

public class RegisterRequest {
    private String userName;
    private String fullName;
    private String email;
    private String password;
    private String clientUri;
    private String userId;

    public RegisterRequest(String userName, String fullName, String email, String password, String clientUri, String userId) {
        this.userName = userName;
        this.fullName = fullName;
        this.email = email;
        this.password = password;
        this.clientUri = clientUri;
        this.userId = userId;
    }

    // Getters and Setters
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getClientUri() {
        return clientUri;
    }

    public void setClientUri(String clientUri) {
        this.clientUri = clientUri;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
