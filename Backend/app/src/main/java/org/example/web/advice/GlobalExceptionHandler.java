package org.example.web.advice;

import org.example.domain.dto.ApiResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgument(IllegalArgumentException e) {
        logger.warn("Invalid request: {}", e.getMessage());
        return ResponseEntity.badRequest().body(
            ApiResponse.error(
                new ApiResponse.ApiError("VALIDATION_ERROR", e.getMessage()),
                UUID.randomUUID().toString()
            )
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Void>> handleFileUploadException(MaxUploadSizeExceededException e) {
        logger.warn("File upload size exceeded: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(
            ApiResponse.error(
                new ApiResponse.ApiError("FILE_TOO_LARGE", "File size exceeds maximum allowed limit"),
                UUID.randomUUID().toString()
            )
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(Exception e) {
        logger.error("Unexpected error occurred", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.error(
                new ApiResponse.ApiError("INTERNAL_ERROR", "An unexpected error occurred"),
                UUID.randomUUID().toString()
            )
        );
    }
}