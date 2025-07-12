package com.yusssss.sso.userservice.dtos.user;

import com.yusssss.sso.userservice.core.results.DataResult;
import org.springframework.http.HttpStatus;

public class UserResult extends DataResult<UserDto> {
    public UserResult(UserDto data, boolean success, String message, HttpStatus httpStatus, String path) {
        super(data, success, message, httpStatus, path);
    }
}
