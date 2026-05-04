package com.example.SmartShop.AI.Assistant.Exception;

import com.example.SmartShop.AI.Assistant.Dto.ErrorResponseDTO;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // -------------------------------------------------
    // 400 – IllegalArgumentException
    // -------------------------------------------------
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgument(
            IllegalArgumentException ex,
            HttpServletRequest request
    ) {
        log.warn("Bad request at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // -------------------------------------------------
    // 404 – Entity Not Found
    // -------------------------------------------------
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDTO> handleEntityNotFound(
            EntityNotFoundException ex,
            HttpServletRequest request
    ) {
        log.warn("Entity not found at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // -------------------------------------------------
    // 400 – Method argument validation (@Valid)
    // -------------------------------------------------
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> handleValidation(
            MethodArgumentNotValidException ex,
            HttpServletRequest request
    ) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining(", "));

        log.warn("Validation error at [{}]: {}", request.getRequestURI(), errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    // -------------------------------------------------
    // 400 – Constraint violations (@Validated)
    // -------------------------------------------------
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDTO> handleConstraintViolation(
            ConstraintViolationException ex,
            HttpServletRequest request
    ) {
        String errorMessage = ex.getConstraintViolations()
                .stream()
                .map(v -> v.getPropertyPath() + " : " + v.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("Constraint violation at [{}]: {}", request.getRequestURI(), errorMessage);
        return buildResponse(HttpStatus.BAD_REQUEST, errorMessage, request);
    }

    // -------------------------------------------------
    // 400 – Invalid JSON body
    // -------------------------------------------------
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidJson(
            HttpMessageNotReadableException ex,
            HttpServletRequest request
    ) {
        log.warn("Invalid request body at [{}]", request.getRequestURI());
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid request body. Please check JSON format.", request);
    }

    // -------------------------------------------------
    // 400 – UUID parsing error
    // -------------------------------------------------
    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<ErrorResponseDTO> handleInvalidUUID(Exception ex, HttpServletRequest request) {
        String message = ex.getMessage().toLowerCase().contains("uuid")
                ? "Invalid product ID format. Must be a valid UUID."
                : ex.getMessage();

        log.warn("Invalid UUID at [{}]: {}", request.getRequestURI(), ex.getMessage());
        return buildResponse(HttpStatus.BAD_REQUEST, message, request);
    }

    // -------------------------------------------------
    // 401 / 403 – Spring Security
    // -------------------------------------------------
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(
            AccessDeniedException ex,
            HttpServletRequest request
    ) {
        log.warn("Access denied at [{}]", request.getRequestURI());
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to perform this action.", request);
    }

    // -------------------------------------------------
    // propagate ResponseStatusException
    // -------------------------------------------------
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponseDTO> handleStatusException(
            ResponseStatusException ex,
            HttpServletRequest request
    ) {
        HttpStatusCode statusCode = ex.getStatusCode();
        log.warn("ResponseStatusException at [{}]: {}", request.getRequestURI(), ex.getReason());
        return ResponseEntity.status(statusCode)
                .body(new ErrorResponseDTO(
                        ex.getReason(),
                        statusCode.toString(),
                        statusCode.value(),
                        LocalDateTime.now()
                ));
    }

    // -------------------------------------------------
    // Fallback – 500
    // -------------------------------------------------
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleAll(
            Exception ex,
            HttpServletRequest request
    ) {
        log.error("Unexpected error at [{}]", request.getRequestURI(), ex);
        return buildResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please contact support.",
                request
        );
    }

    // -------------------------------------------------
    // Helper: format validation errors
    // -------------------------------------------------
    private String formatFieldError(FieldError error) {
        return error.getField() + " : " + error.getDefaultMessage();
    }

    // -------------------------------------------------
    // Centralized Response Builder
    // -------------------------------------------------
    private ResponseEntity<ErrorResponseDTO> buildResponse(
            HttpStatus status,
            String message,
            HttpServletRequest request
    ) {
        ErrorResponseDTO error = new ErrorResponseDTO(
                message,
                status.getReasonPhrase(),
                status.value(),
                LocalDateTime.now()
        );
        return ResponseEntity.status(status).body(error);
    }
}