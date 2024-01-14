package com.example.highload.image.feign;

import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.model.network.ProfileDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@CircuitBreaker(name = "CbServiceBasedOnCount")
@FeignClient("profile-service")
public interface ProfileServiceFeignClient {
    @GetMapping("/api/profile/core/single/{id}")
    ResponseEntity<ProfileDto> getById(@PathVariable int id, @RequestHeader("Authorization") String token);

    @GetMapping("/api/profile/core/single/{id}/image")
    ResponseEntity<ImageDto> setNewMainImage(@PathVariable int id, @RequestBody ImageDto imageDto, @RequestHeader("Authorization") String token);
}
