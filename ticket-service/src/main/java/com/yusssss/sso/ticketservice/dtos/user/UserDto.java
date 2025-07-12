package com.yusssss.sso.ticketservice.dtos.user;

import lombok.Getter;
import lombok.Setter;

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
}
