package com.portfolioBackend.backend.common.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Map;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // Handle custom AppException
    @ExceptionHandler(AppException.class)
    public ResponseEntity<ApiResponse<?>> handleAppException(AppException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatusCode())
                .body(ApiResponse.error(errorCode.getCode(), ex.getMessage()));
    }

    // Handle @Valid validation errors (BindException)
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponse<?>> handleBindException(BindException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .findFirst()
                .orElse("Validation error");
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    // Handle invalid parameter type mismatch
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handleTypeMismatch(TypeMismatchException ex) {
        String message = "Invalid value for parameter: " + ex.getPropertyName();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }

    // Handle malformed or unreadable JSON payload
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<?>> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), "Invalid JSON format"));
    }

    // Handle missing required query parameters
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<?>> handleMissingParams(MissingServletRequestParameterException ex) {
        String message = "Missing parameter: " + ex.getParameterName();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.error(HttpStatus.BAD_REQUEST.value(), message));
    }



    // Handle unsupported HTTP method (e.g. GET instead of POST)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        String message = "Method not supported: " + ex.getMethod();
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiResponse.error(HttpStatus.METHOD_NOT_ALLOWED.value(), message));
    }

    // Handle unsupported Content-Type (e.g. expected JSON but received form-data)
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiResponse<?>> handleMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        return ResponseEntity
                .status(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
                .body(ApiResponse.error(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(), "Unsupported media type"));
    }


    // Fallback handler for all uncategorized exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleUncategorized(Exception ex) {
        ErrorCode errorCode = ErrorCode.UNCATEGORIZED_EXCEPTION;
        log.error("Uncategorized exception: ", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

    // Handle 403 Forbidden / Access Denied
    @ExceptionHandler(value = org.springframework.security.access.AccessDeniedException.class)
    ResponseEntity<ApiResponse<Object>> handlingAccessDeniedException(AccessDeniedException exception) {
        ErrorCode errorCode = ErrorCode.UNAUTHORIZED; // Unauthorized access error

        return ResponseEntity.status(errorCode.getStatusCode())
                .body(ApiResponse.builder()
                        .code(errorCode.getCode())
                        .message(errorCode.getMessage())
                        .build());
    }

    // Handle @Valid MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleValidation(MethodArgumentNotValidException ex) {
        String enumKey = ex.getFieldError() != null ? ex.getFieldError().getDefaultMessage() : null;
        ErrorCode errorCode = ErrorCode.INVALID_KEY;

        Map<String, Object> attributes = null;
        try {
            if (enumKey != null) {
                errorCode = ErrorCode.valueOf(enumKey);
            }

            if (!ex.getBindingResult().getAllErrors().isEmpty()) {
                var violation = ex.getBindingResult().getAllErrors().get(0)
                        .unwrap(jakarta.validation.ConstraintViolation.class);

                attributes = violation.getConstraintDescriptor().getAttributes();
                log.debug("Validation attributes: {}", attributes);
            }
        } catch (IllegalArgumentException e) {
            log.warn("Invalid error code: {}", enumKey, e);
        } catch (Exception e) {
            log.error("Failed to extract constraint violation attributes", e);
        }

        String message = (attributes != null)
                ? mapAttribute(errorCode.getMessage(), attributes)
                : errorCode.getMessage();

        return ResponseEntity.badRequest()
                .body(ApiResponse.error(errorCode.getCode(), message));
    }

    private String mapAttribute(String message, Map<String, Object> attributes) {
        if (attributes.containsKey("min")) {
            message = message.replace("{min}", String.valueOf(attributes.get("min")));
        }
        if (attributes.containsKey("max")) {
            message = message.replace("{max}", String.valueOf(attributes.get("max")));
        }
        return message;
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolationException(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);

        ErrorCode errorCode = ErrorCode.DUPLICATE_RESOURCE;
        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(ApiResponse.error(errorCode.getCode(), errorCode.getMessage()));
    }

}
