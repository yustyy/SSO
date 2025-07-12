package com.yusssss.sso.ticketservice.core.exceptions;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;

import java.io.InputStream;
import java.util.Map;

public class CustomFeignErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        String message = "Remote call failed";
        try (InputStream body = response.body().asInputStream()) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> errorMap = mapper.readValue(body, new TypeReference<>() {});
            if (errorMap.containsKey("message")) {
                message = (String) errorMap.get("message");
            }
        } catch (Exception ignored) {}

        HttpStatus status = HttpStatus.resolve(response.status());
        return new FeignServiceException(message, status != null ? status : HttpStatus.INTERNAL_SERVER_ERROR);
    }
}