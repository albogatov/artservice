package com.example.highload.admin.feign;

import com.example.highload.admin.config.FeignConfiguration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "login-service", configuration = FeignConfiguration.class)
@CircuitBreaker(name = "loginServiceBreaker")
public interface LoginServiceFeignClient {
    @PostMapping("/api/auth/validate")
    ResponseEntity<?> validateToken(String token);

    @PostMapping("/api/auth/get-login-from-token")
    ResponseEntity<String> getLoginFromToken(String token);
}
