package com.example.highload.profile.feign;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.awt.*;

@FeignClient("image-service")
@CircuitBreaker(name = "imageServiceBreaker")
public interface ImageServiceFeignClient {
    @GetMapping("/api/image/findAllProfile/{id}/{page}")
    ResponseEntity<Page<Image>> findAllProfileImages(int id, int page);

}
