package com.example.highload.admin.feign;

import com.example.highload.admin.model.network.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(value = "user-service")
@CircuitBreaker(name = "userServiceBreaker")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login, @RequestHeader("Authorization") String token);

    @GetMapping("/api/user/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id, @RequestHeader("Authorization") String token);
    @PostMapping("/api/user/save")
    ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token);

    @PostMapping("/api/user/deleteId/{id}")
    ResponseEntity<String> deleteUser(@PathVariable int id, @RequestHeader("Authorization") String token);

    @GetMapping("/api/user/findExpired/{daysToExpire}/{page}")
    ResponseEntity<List<UserDto>> findExpired(@PathVariable int daysToExpire, @PathVariable int page, @RequestHeader("Authorization") String token);

    @PostMapping("/api/user/deleteAllExpired/{daysToExpire}")
    ResponseEntity<String> deleteAllExpired(@PathVariable int daysToExpire, @RequestHeader("Authorization") String token);
}
