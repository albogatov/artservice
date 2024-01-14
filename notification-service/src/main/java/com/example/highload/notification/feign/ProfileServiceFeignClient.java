package com.example.highload.notification.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.concurrent.CompletableFuture;

@CircuitBreaker(name = "CbServiceBasedOnCount")
@FeignClient("profile-service")
public interface ProfileServiceFeignClient {

    @GetMapping("/api/profile/core/single/{id}/exists")
    @Async
    CompletableFuture<Boolean> checkProfileExistsById(@PathVariable int id, @RequestHeader(value = "Authorization", required = true) String token);
}
