package com.example.highload.notification.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CircuitBreaker(name = "CbServiceBasedOnCount")
@FeignClient("profile-service")
public interface ProfileServiceFeignClient {

    @PostMapping("/api/profile/core/all/exists")
    @Async
    ResponseEntity<Boolean> checkProfileExistsByIds(@RequestBody List<Integer> ids, @RequestHeader(value = "Authorization", required = true) String token);
}
