package com.example.highload.notification.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("login-service")
public interface LoginServiceFeignClient {
    @PostMapping("api/v1/airline/validate")
    ResponseEntity<?> validateToken(String token);

    @PostMapping("api/v1/airline/get-login-from-token")
    ResponseEntity<String> getLoginFromToken(String token);
}
