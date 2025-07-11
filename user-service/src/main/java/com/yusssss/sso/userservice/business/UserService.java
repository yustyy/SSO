package com.yusssss.sso.userservice.business;

import com.nimbusds.jwt.SignedJWT;
import com.yusssss.sso.userservice.core.exceptions.UnauthorizedException;
import com.yusssss.sso.userservice.core.exceptions.UserNotFoundException;
import com.yusssss.sso.userservice.core.utilities.keycloak.KeycloakService;
import com.yusssss.sso.userservice.dataAccess.UserDao;
import com.yusssss.sso.userservice.dtos.user.UserDto;
import com.yusssss.sso.userservice.dtos.user.UserRequest;
import com.yusssss.sso.userservice.entities.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
public class UserService {

    private final UserDao userDao;
    private final KeycloakService keycloakService;

    public UserService(UserDao userDao, KeycloakService keycloakService) {
        this.userDao = userDao;
        this.keycloakService = keycloakService;
    }

    @Transactional
    public UserDto createUser(UserRequest userRequest) {
        String keycloakUserId = keycloakService.createUser(userRequest);

        User user = new User();
        user.setId(UUID.fromString(keycloakUserId));
        user.setUsername(userRequest.getUsername());
        user.setEmail(userRequest.getEmail());
        user.setFirstName(userRequest.getFirstName());
        user.setLastName(userRequest.getLastName());
        user.setPhoneNumber(userRequest.getPhoneNumber());
        user.setActive(true);

        User savedUser = userDao.save(user);
        log.info("User created in DB with ID: {}", savedUser.getId());
        return new UserDto(savedUser);
    }

    public UserDto getUserById(UUID id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return new UserDto(user);
    }

    public List<UserDto> getAllUsers() {
        return userDao.findAll().stream().map(UserDto::new).toList();
    }

    @Transactional
    public UserDto updateUser(UUID id, @Valid UserRequest userRequest) {
        User existingUser = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        existingUser.setFirstName(userRequest.getFirstName() != null ? userRequest.getFirstName() : existingUser.getFirstName());
        existingUser.setLastName(userRequest.getLastName() != null ? userRequest.getLastName() : existingUser.getLastName());
        existingUser.setPhoneNumber(userRequest.getPhoneNumber() != null ? userRequest.getPhoneNumber() : existingUser.getPhoneNumber());

        keycloakService.updateUser(existingUser.getId().toString(), userRequest);

        User savedUser = userDao.save(existingUser);
        log.info("User updated in DB with ID: {}", savedUser.getId());
        return new UserDto(savedUser);
    }

    @Transactional
    public void deleteUser(UUID id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        keycloakService.deleteUser(user.getId().toString());

        userDao.delete(user);
        log.info("User deleted from DB with ID: {}", id);
    }

    public void activateUser(UUID id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setActive(true);
        userDao.save(user);
        log.info("User activated with ID: {}", id);
    }

    public void deactivateUser(UUID id) {
        User user = userDao.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));

        user.setActive(false);
        userDao.save(user);
        log.info("User deactivated with ID: {}", id);
    }

    public UserDto getCurrentUser(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("Authorization header missing or invalid");
        }

        String token = authHeader.substring(7);

        String userId;
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            userId = jwt.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Invalid JWT token", e);
        }

        User user = userDao.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + userId));
        return new UserDto(user);
    }

}