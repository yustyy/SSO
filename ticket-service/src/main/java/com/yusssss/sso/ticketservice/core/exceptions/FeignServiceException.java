package com.yusssss.sso.ticketservice.core.exceptions;

import org.springframework.http.HttpStatus;

public class FeignServiceException extends RuntimeException {

    private final HttpStatus httpStatus;

    public FeignServiceException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}