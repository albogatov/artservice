package com.example.highload.notification.feign;

import com.example.highload.notification.model.network.ProfileDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@CircuitBreaker(name = "profileServiceBreaker")
@FeignClient("profile-service")
public interface ProfileServiceFeignClient {

    @GetMapping("/api/profile/core/user/single/${id}")
    @Async
    ResponseEntity<ProfileDto> getProfileDataByUserId(@PathVariable int ids);

}
