package com.portfolioBackend.backend.common.exception;

import lombok.Getter;

@Getter
public class AppException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String detailMessage;

    // Basic constructor with ErrorCode
    public AppException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    // Constructor with additional detail message
    public AppException(ErrorCode errorCode, String detailMessage) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
        this.detailMessage = detailMessage;
    }

    // Wrap root cause exception
    public AppException(ErrorCode errorCode, Throwable cause) {
        super(errorCode.getMessage(), cause);
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    // Dynamic message formatting (e.g. new AppException(ErrorCode.RESOURCE_NOT_FOUND, "User"))
    public AppException(ErrorCode errorCode, Object... args) {
        super(String.format(errorCode.getMessage(), args));
        this.errorCode = errorCode;
        this.detailMessage = null;
    }

    @Override
    public String getMessage() {
        if (detailMessage != null) {
            return errorCode.getMessage() + ": " + detailMessage;
        }
        return super.getMessage();
    }
}
