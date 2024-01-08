package com.example.highload.admin.feign;

import com.example.highload.admin.model.network.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.LocalDateTime;

@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login);

    @GetMapping("/api/user/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id);
    @PostMapping("/api/user/save")
    ResponseEntity<UserDto> saveUser(@RequestBody UserDto userDto);

    @PostMapping("/api/user/deleteId/{id}")
    ResponseEntity<?> deleteUser(@PathVariable int id);

    @GetMapping("/api/user/findExpired/{expiryTime}/{page}")
    ResponseEntity<Page<UserDto>> findExpired(@PathVariable LocalDateTime expiryTime, int page);

    @PostMapping("/api/user/deleteAllExpired/{expiryTime}")
    ResponseEntity<Page<UserDto>> deleteAllExpired(@PathVariable LocalDateTime expiryTime);
}
