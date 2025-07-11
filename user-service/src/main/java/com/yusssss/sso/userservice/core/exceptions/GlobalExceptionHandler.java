package com.yusssss.sso.userservice.core.exceptions;

import com.yusssss.sso.userservice.core.results.ErrorResult;
import com.yusssss.sso.userservice.core.results.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<Result> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResult(
                        ex.getMessage(),
                        HttpStatus.NOT_FOUND,
                        request.getRequestURI()));
    }

    @ExceptionHandler(KeycloakException.class)
    public ResponseEntity<Result> handleKeycloakException(KeycloakException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResult(
                        ex.getMessage(),
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleGeneralException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResult(
                        "Internal server error",
                        HttpStatus.INTERNAL_SERVER_ERROR,
                        request.getRequestURI()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Result> handleUnauthorizedException(UnauthorizedException ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResult(
                        ex.getMessage(),
                        HttpStatus.UNAUTHORIZED,
                        request.getRequestURI()));
    }
}