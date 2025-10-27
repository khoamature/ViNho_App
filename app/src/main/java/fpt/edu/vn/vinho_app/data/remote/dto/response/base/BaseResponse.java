package fpt.edu.vn.vinho_app.data.remote.dto.response.base;

import java.util.List;

public class BaseResponse<T> {
    private boolean isSuccess = true;
    private String message = "OK";
    private List<String> errors;
    private T payload;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public T getPayload() {
        return payload;
    }

    public void setPayload(T payload) {
        this.payload = payload;
    }
}
