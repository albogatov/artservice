package com.example.highload.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient("login-service")
public interface LoginServiceFeignClient {
    @PostMapping("/api/auth/validate")
    ResponseEntity<?> validateToken(String token);

    @PostMapping("/api/auth/get-login-from-token")
    ResponseEntity<String> getLoginFromToken(String token);

    @PostMapping("/api/auth/details")
    ResponseEntity<UserDetailsService> details();
}