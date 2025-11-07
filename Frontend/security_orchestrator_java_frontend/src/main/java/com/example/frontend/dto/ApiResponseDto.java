package com.example.frontend.dto;

public class ApiResponseDto<T> {
    private boolean success;
    private T data;
    private String error;
    private String requestId;

    public ApiResponseDto() {}

    public ApiResponseDto(boolean success, T data, String error, String requestId) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.requestId = requestId;
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getRequestId() { return requestId; }
    public void setRequestId(String requestId) { this.requestId = requestId; }
}