package fpt.edu.vn.vinho_app.data.remote.dto.request.auth;

import com.google.gson.annotations.SerializedName;

public class ResetPasswordByPinRequest {
    @SerializedName("email")
    private String email;
    @SerializedName("pin")
    private String pin;
    @SerializedName("newPassword")
    private String newPassword;
    @SerializedName("confirmNewPassword")
    private String confirmNewPassword;

    public ResetPasswordByPinRequest(String email, String pin, String newPassword, String confirmNewPassword) {
        this.email = email;
        this.pin = pin;
        this.newPassword = newPassword;
        this.confirmNewPassword = confirmNewPassword;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmNewPassword() {
        return confirmNewPassword;
    }

    public void setConfirmNewPassword(String confirmNewPassword) {
        this.confirmNewPassword = confirmNewPassword;
    }
}
