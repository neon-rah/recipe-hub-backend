package org.schoolproject.backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice

public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> illegalArgumentExceptionHandler(IllegalArgumentException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.BAD_REQUEST.value()));
        response.put("error", "Bad Request");
        response.put("message", ex.getMessage());
        return ResponseEntity.badRequest().body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> generalExceptionHandler(Exception ex) {
        Map<String, String> response = new HashMap<>();
        response.put("status", String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()));
        response.put("error", "Internal Server Error");
        response.put("message", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
}
