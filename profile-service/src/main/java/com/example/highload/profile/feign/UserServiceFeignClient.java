package com.example.highload.profile.feign;

import com.example.highload.profile.model.network.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login, @RequestHeader("Authorization") String token);
}
