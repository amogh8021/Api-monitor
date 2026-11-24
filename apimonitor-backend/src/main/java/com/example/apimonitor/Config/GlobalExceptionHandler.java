package com.example.apimonitor.Config;

import com.example.apimonitor.Dtos.ApiError.ApiErrorResponse;
import com.example.apimonitor.exception.InvalidUrlException;
import com.example.apimonitor.exception.UnsafeDestinationException;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(InvalidUrlException.class)
    public ResponseEntity<ApiErrorResponse> handleInvalidUrl(
            InvalidUrlException e,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                400,
                e.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(UnsafeDestinationException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsafeDestination(
            UnsafeDestinationException e,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                400,
                e.getMessage(),
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }

  //valid exception logic for validation of the entry
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(
            MethodArgumentNotValidException e,
            HttpServletRequest request) {

        String message = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": "
                        + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (message.isBlank()) {
            message = "Request validation failed";
        }

        ApiErrorResponse error = new ApiErrorResponse(
                400,
                message,
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiErrorResponse> handleMalformedRequest(
            HttpMessageNotReadableException e,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                400,
                "Malformed or missing request body",
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.badRequest().body(error);
    }


    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMethod(
            HttpRequestMethodNotSupportedException e,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                405,
                "HTTP method " + e.getMethod() + " is not supported",
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(405)
                .headers(e.getHeaders())
                .body(error);
    }


    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ApiErrorResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException e,
            HttpServletRequest request) {

        ApiErrorResponse error = new ApiErrorResponse(
                415,
                "Unsupported content type",
                Instant.now(),
                request.getRequestURI()
        );

        return ResponseEntity.status(415)
                .headers(e.getHeaders())
                .body(error);
    }

}