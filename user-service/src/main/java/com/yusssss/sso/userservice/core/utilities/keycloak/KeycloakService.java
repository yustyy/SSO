package com.yusssss.sso.userservice.core.utilities.keycloak;

import com.yusssss.sso.userservice.core.exceptions.KeycloakException;
import com.yusssss.sso.userservice.dtos.user.UserRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class KeycloakService {

    private final WebClient.Builder webClientBuilder;

    @Value("${keycloak.base-url}")
    private String keycloakBaseUrl;

    @Value("${keycloak.realm}")
    private String realm;

    @Value("${keycloak.client-id}")
    private String clientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    public String createUser(UserRequest userRequest) {
        String token = getAdminAccessToken();

        String userId = createUserInKeycloak(userRequest, token);
        assignRealmRole(userId, "user", token);

        log.info("User successfully created and role assigned: userId={}", userId);
        return userId;
    }


    public void updateUser(String keycloakUserId, UserRequest userRequest) {
        String token = getAdminAccessToken();

        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        Map<String, Object>  updatePayload = Map.of(
                "firstName", userRequest.getFirstName(),
                "lastName", userRequest.getLastName()
        );

        webClient.put()
                .uri("/admin/realms/{realm}/users/{userId}", realm, keycloakUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(updatePayload)
                .retrieve()
                .onStatus(
                        status -> status.isError(),
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Failed to update user in Keycloak: {}", errorBody);
                            return Mono.error(new KeycloakException("User update failed: " + errorBody));
                        })
                )
                .toBodilessEntity()
                .block();

        log.info("User updated successfully in Keycloak: userId={}", keycloakUserId);
    }

    public void deleteUser(String keyCloakUserId){
        String token = getAdminAccessToken();

        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        webClient.delete()
                .uri("/admin/realms/{realm}/users/{userId}", realm, keyCloakUserId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(
                        HttpStatusCode::isError,
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Failed to delete user from keycloak: {}", errorBody);
                            return Mono.error(new KeycloakException("User delete failed: " + errorBody));
                        })
                )
                .toBodilessEntity()
                .block();

        log.info("User successfully deleted from Keycloak: userId={}", keyCloakUserId);

    }



    private String createUserInKeycloak(UserRequest userRequest, String token) {
        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        Map<String, Object> userPayload = Map.of(
                "username", userRequest.getUsername(),
                "email", userRequest.getEmail(),
                "firstName", userRequest.getFirstName(),
                "lastName", userRequest.getLastName(),
                "enabled", true,
                "credentials", List.of(
                        Map.of(
                                "type", "password",
                                "value", userRequest.getPassword(),
                                "temporary", false
                        )
                )
        );

        return webClient.post()
                .uri("/admin/realms/{realm}/users", realm)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(userPayload)
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.isError(),
                        response -> response.bodyToMono(String.class)
                                .map(errorBody -> {
                                    log.error("Failed to create user in Keycloak: {}", errorBody);
                                    return new KeycloakException("User creation failed: " + errorBody);
                                })
                                .flatMap(Mono::error)
                )
                .toBodilessEntity()
                .map(response -> {
                    String location = response.getHeaders().getFirst("Location");
                    if (location != null) {
                        String userId = location.substring(location.lastIndexOf('/') + 1);
                        log.debug("User created in Keycloak. ID: {}", userId);
                        return userId;
                    } else {
                        log.error("User created but 'Location' header is missing.");
                        throw new KeycloakException("User created but 'Location' header is missing.");
                    }
                })
                .block();
    }





    private void assignRealmRole(String userId, String roleName, String token) {
        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        Map<String, Object> role = webClient.get()
                .uri("/admin/realms/{realm}/roles/{roleName}", realm, roleName)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.isError(),
                        response -> response.bodyToMono(String.class).flatMap(errorBody -> {
                            log.error("Failed to fetch role '{}' from Keycloak: {}", roleName, errorBody);
                            return Mono.error(new KeycloakException("Role fetch failed: " + errorBody));
                        })
                )
                .bodyToMono(Map.class)
                .block();

        webClient.post()
                .uri("/admin/realms/{realm}/users/{userId}/role-mappings/realm", realm, userId)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(List.of(role))
                .retrieve()
                .onStatus(
                        httpStatusCode -> httpStatusCode.isError(),
                        response -> response.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    log.error("Failed to assign role '{}' to user '{}': {}", roleName, userId, errorBody);
                                    return Mono.error(new KeycloakException("Role assignment failed: " + errorBody));
                                })
                )
                .toBodilessEntity()
                .block();

        log.debug("Role '{}' successfully assigned to user '{}'", roleName, userId);
    }

    private String getAdminAccessToken() {
        WebClient webClient = webClientBuilder.baseUrl(keycloakBaseUrl).build();

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("grant_type", "client_credentials");
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);

        try {
            Map<String, Object> response = webClient.post()
                    .uri("/realms/{realm}/protocol/openid-connect/token", realm)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                    .body(BodyInserters.fromFormData(formData))
                    .retrieve()
                    .onStatus(
                            httpStatusCode -> httpStatusCode.isError(),
                            res -> res.bodyToMono(String.class).flatMap(body -> {
                                log.error("Failed to obtain admin token: {}", body);
                                return Mono.error(new KeycloakException("Admin token request failed: " + body));
                            })
                    )
                    .bodyToMono(Map.class)
                    .block();

            String token = (String) response.get("access_token");
            if (token == null) {
                log.error("Admin token is missing from Keycloak response.");
                throw new KeycloakException("Access token not found in response.");
            }

            log.debug("Admin access token successfully obtained.");
            return token;

        } catch (WebClientResponseException e) {
            log.error("WebClient error while obtaining admin token: {}", e.getResponseBodyAsString(), e);
            throw new KeycloakException("Admin token WebClient error", e);
        } catch (Exception e) {
            log.error("Unexpected error while obtaining admin token", e);
            throw new KeycloakException("Unexpected error while obtaining admin token", e);
        }
    }
}