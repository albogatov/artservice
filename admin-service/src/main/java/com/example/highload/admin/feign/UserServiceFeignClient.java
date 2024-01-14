package com.example.highload.admin.feign;

import com.example.highload.admin.model.network.UserDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@FeignClient("user-service")
@CircuitBreaker(name = "CbServiceBasedOnCount")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login, @RequestHeader("Authorization") String token);

    @GetMapping("/api/user/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id, @RequestHeader("Authorization") String token);
    @PostMapping("/api/user/save")
    ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto, @RequestHeader("Authorization") String token);

    @PostMapping("/api/user/deleteId/{id}")
    ResponseEntity<?> deleteUser(@PathVariable int id, @RequestHeader("Authorization") String token);

    @GetMapping("/api/user/findExpired/{expiryTime}/{page}")
    ResponseEntity<Page<UserDto>> findExpired(@PathVariable LocalDateTime expiryTime, int page, @RequestHeader("Authorization") String token);

    @PostMapping("/api/user/deleteAllExpired/{expiryTime}")
    ResponseEntity<Page<UserDto>> deleteAllExpired(@PathVariable LocalDateTime expiryTime, @RequestHeader("Authorization") String token);
}
