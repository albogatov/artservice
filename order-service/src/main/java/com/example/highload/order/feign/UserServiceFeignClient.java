package com.example.highload.order.feign;

import com.example.highload.order.model.network.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

@CircuitBreaker(name = "userServiceBreaker")
@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login, @RequestHeader("Authorization") String token);

    @GetMapping("/api/user/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id, @RequestHeader("Authorization") String token);
}
