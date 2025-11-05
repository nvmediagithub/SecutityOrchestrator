package org.example.domain.dto;

import java.time.Instant;

public class ApiResponse<T> {
    private boolean success;
    private T data;
    private ApiError error;
    private ApiMeta meta;

    public ApiResponse() {}

    public ApiResponse(boolean success, T data, ApiError error, String requestId) {
        this.success = success;
        this.data = data;
        this.error = error;
        this.meta = new ApiMeta(requestId, Instant.now());
    }

    public static <T> ApiResponse<T> success(T data, String requestId) {
        return new ApiResponse<>(true, data, null, requestId);
    }

    public static <T> ApiResponse<T> error(ApiError error, String requestId) {
        return new ApiResponse<>(false, null, error, requestId);
    }

    // Getters and setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public T getData() { return data; }
    public void setData(T data) { this.data = data; }

    public ApiError getError() { return error; }
    public void setError(ApiError error) { this.error = error; }

    public ApiMeta getMeta() { return meta; }
    public void setMeta(ApiMeta meta) { this.meta = meta; }

    public static class ApiError {
        private String code;
        private String message;

        public ApiError() {}

        public ApiError(String code, String message) {
            this.code = code;
            this.message = message;
        }

        // Getters and setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class ApiMeta {
        private String requestId;
        private Instant timestamp;

        public ApiMeta() {}

        public ApiMeta(String requestId, Instant timestamp) {
            this.requestId = requestId;
            this.timestamp = timestamp;
        }

        // Getters and setters
        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }

        public Instant getTimestamp() { return timestamp; }
        public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    }
}