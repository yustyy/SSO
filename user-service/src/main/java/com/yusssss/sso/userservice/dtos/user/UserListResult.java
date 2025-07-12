package com.yusssss.sso.userservice.dtos.user;

import com.yusssss.sso.userservice.core.results.DataResult;
import org.springframework.http.HttpStatus;

import java.util.List;

public class UserListResult extends DataResult<List<UserDto>> {
    public UserListResult(List<UserDto> data, boolean success, String message, HttpStatus httpStatus, String path) {
        super(data, success, message, httpStatus, path);
    }
}
