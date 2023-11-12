package com.yourrents.services.common.util.exception;

import java.time.LocalDateTime;

public record ApiError(String message, String error, int status, String path, LocalDateTime timestamp) {
    public ApiError(String message, String error, int status, String path) {
        this(message, error, status, path, LocalDateTime.now());
    }
}