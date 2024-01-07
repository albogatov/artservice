package com.example.highload.admin.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Интерфейс для взаимодействия с микросервисом login-service
 */
@FeignClient("login-service")
public interface LoginServiceFeignClient {
    @PostMapping("/api/auth/validate")
    ResponseEntity<?> validateToken(String token);

    @PostMapping("/api/auth/get-login-from-token")
    ResponseEntity<String> getLoginFromToken(String token);
}
