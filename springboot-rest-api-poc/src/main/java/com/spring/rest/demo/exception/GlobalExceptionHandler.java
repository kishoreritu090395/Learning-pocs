package com.spring.rest.demo.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	
	private static final Logger logger =
	        LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleProductNotFound(
            ProductNotFoundException ex) {

        Map<String, Object> error =
                new HashMap<String, Object>();

        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("message", ex.getMessage());

        return new ResponseEntity<Map<String, Object>>(
                error,
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationException(
            MethodArgumentNotValidException ex) {

        Map<String, String> validationErrors =
                new HashMap<String, String>();

        ex.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        Map<String, Object> response =
                new HashMap<String, Object>();

        response.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        response.put(
                "errors",
                validationErrors
        );

        return new ResponseEntity<Map<String, Object>>(
                response,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidJson(
            HttpMessageNotReadableException ex) {

        Map<String, Object> error =
                new HashMap<String, Object>();

        error.put(
                "status",
                HttpStatus.BAD_REQUEST.value()
        );

        error.put(
                "message",
                "Invalid request body"
        );

        return new ResponseEntity<Map<String, Object>>(
                error,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGenericException(
            Exception ex) {

        logger.error(
                "Unexpected error occurred",
                ex
        );

        Map<String, Object> error =
                new HashMap<String, Object>();

        error.put(
                "status",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        error.put(
                "message",
                "An unexpected error occurred"
        );

        return new ResponseEntity<Map<String, Object>>(
                error,
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
    
    @ExceptionHandler(CategoryNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleCategoryNotFound(
            CategoryNotFoundException ex) {

        Map<String, Object> error =
                new HashMap<String, Object>();

        error.put("status", HttpStatus.NOT_FOUND.value());
        error.put("message", ex.getMessage());

        return new ResponseEntity<Map<String, Object>>(
                error,
                HttpStatus.NOT_FOUND
        );
    }
}