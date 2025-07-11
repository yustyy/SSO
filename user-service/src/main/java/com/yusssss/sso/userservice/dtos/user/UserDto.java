package com.yusssss.sso.userservice.dtos.user;

import com.yusssss.sso.userservice.entities.User;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class UserDto {

    private UUID id;

    private String username;

    private String email;

    private String firstName;

    private String lastName;

    private String phoneNumber;

    public UserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
    }
}
