package com.example.highload.image.feign;

import com.example.highload.image.model.network.UserDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("user-service")
public interface UserServiceFeignClient {

    @GetMapping("/api/user/findLogin/{login}")
    ResponseEntity<UserDto> findByLoginElseNull(@PathVariable String login);

    @GetMapping("/api/user/findId/{id}")
    ResponseEntity<UserDto> findById(@PathVariable int id);
}
