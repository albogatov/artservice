package com.example.highload.admin.feign;

import com.example.highload.admin.config.FeignConfiguration;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(value = "image-service", configuration = FeignConfiguration.class)
@CircuitBreaker(name = "imageServiceBreaker")
public interface ImageServiceFeignClient {
    @PostMapping("/api/image/removeAll/profile/{profileId}")
    ResponseEntity<?> removeAllImagesForProfile(@PathVariable int profileId, @RequestHeader("Authorization") String token);

    @PostMapping("/api/image/removeAll/order/{orderId}")
    ResponseEntity<?> removeAllImagesForOrder(@PathVariable int orderId, @RequestHeader("Authorization") String token);
}
