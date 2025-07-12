package com.yusssss.sso.userservice.api.controllers;

import com.yusssss.sso.userservice.business.UserService;
import com.yusssss.sso.userservice.core.results.DataResult;
import com.yusssss.sso.userservice.core.results.Result;
import com.yusssss.sso.userservice.core.results.SuccessDataResult;
import com.yusssss.sso.userservice.dtos.user.UserDto;
import com.yusssss.sso.userservice.dtos.user.UserListResult;
import com.yusssss.sso.userservice.dtos.user.UserRequest;
import com.yusssss.sso.userservice.dtos.user.UserResult;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UsersController {

    private final UserService userService;

    public UsersController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/")
    public ResponseEntity<DataResult<UserDto>> createUser(@RequestBody @Valid UserRequest userRequest,
                                                          HttpServletRequest request){


        UserDto savedUser = userService.createUser(userRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(new SuccessDataResult<>(savedUser,
                "User created successfully", HttpStatus.CREATED, request.getRequestURI()));
    }

    @GetMapping("/bulk")
    public ResponseEntity<DataResult<List<UserDto>>> getUsersByIds(@RequestParam List<UUID> ids,
                                                                    HttpServletRequest request) {
        List<UserDto> users = userService.getUsersByIds(ids);
        return ResponseEntity.ok(new SuccessDataResult<>(users, "Users retrieved successfully",
                HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResult> getUserById(@PathVariable UUID id,
                                                           HttpServletRequest request) {
        UserDto user = userService.getUserById(id);
        return ResponseEntity.ok(new UserResult(user, true, "User retrieved successfully",
                HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResult> getCurrentUser(HttpServletRequest request) {
        var user = userService.getCurrentUser(request);
        return ResponseEntity.ok(new UserResult(user, true, "Current user retrieved successfully",
                HttpStatus.OK, request.getRequestURI()));
    }

    @GetMapping("/")
    public ResponseEntity<UserListResult> getAllUsers(HttpServletRequest request) {
        List<UserDto> users = userService.getAllUsers();

        return ResponseEntity.status(HttpStatus.OK).body(new UserListResult(users, true,
                "Users retrieved successfully", HttpStatus.OK, request.getRequestURI()));

    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResult> updateUser(@PathVariable UUID id,
                                                           @RequestBody @Valid UserRequest userRequest,
                                                           HttpServletRequest request) {
        UserDto updatedUser = userService.updateUser(id, userRequest);
        return ResponseEntity.ok(new UserResult(updatedUser, true, "User updated successfully",
                HttpStatus.OK, request.getRequestURI()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Result> deleteUser(@PathVariable UUID id,
                                             HttpServletRequest request) {
        userService.deleteUser(id);
        return ResponseEntity.ok(new SuccessDataResult<>(HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/{id}/activate")
    public ResponseEntity<Result> activateUser(@PathVariable UUID id,
                                               HttpServletRequest request) {
        userService.activateUser(id);
        return ResponseEntity.ok(new SuccessDataResult<>(HttpStatus.OK, request.getRequestURI()));
    }

    @PostMapping("/{id}/deactivate")
    public ResponseEntity<Result> deactivateUser(@PathVariable UUID id,
                                                 HttpServletRequest request) {
        userService.deactivateUser(id);
        return ResponseEntity.ok(new SuccessDataResult<>(HttpStatus.OK, request.getRequestURI()));
    }




}
