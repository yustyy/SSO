package com.yusssss.sso.ticketservice.core.externalServices;

import com.yusssss.sso.ticketservice.dtos.user.UserResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "user-service")
public interface UserFeignClient {

    @GetMapping(value = "/api/users/{id}")
    ResponseEntity<UserResult> getUserById(@PathVariable("id") UUID id);

}
