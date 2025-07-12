package com.yusssss.sso.ticketservice.dtos.user;

import com.yusssss.sso.ticketservice.core.results.DataResult;
import org.springframework.http.HttpStatus;

public class UserResult extends DataResult<UserDto> {
    public UserResult(UserDto data, boolean success, String message, HttpStatus httpStatus, String path) {
        super(data, success, message, httpStatus, path);
    }
}
