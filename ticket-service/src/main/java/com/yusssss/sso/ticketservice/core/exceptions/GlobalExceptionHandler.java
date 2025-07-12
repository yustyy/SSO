package com.yusssss.sso.ticketservice.core.exceptions;

import com.yusssss.sso.ticketservice.core.results.ErrorResult;
import com.yusssss.sso.ticketservice.core.results.Result;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(FeignServiceException.class)
    public ResponseEntity<Result> handleFeignServiceException(FeignServiceException ex, HttpServletRequest request) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                new ErrorResult(ex.getMessage(), ex.getHttpStatus(), request.getRequestURI())
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Result> handleTypeMismatch(MethodArgumentTypeMismatchException ex, HttpServletRequest request) {
        String message = String.format("Invalid value '%s' for parameter '%s'. Expected type: %s",
                ex.getValue(), ex.getName(), ex.getRequiredType().getSimpleName());

        return ResponseEntity.badRequest().body(
                new ErrorResult(message, HttpStatus.BAD_REQUEST, request.getRequestURI())
        );
        }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Result> handleGeneralException(Exception ex, HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                new ErrorResult(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR, request.getRequestURI())
        );
    }


}