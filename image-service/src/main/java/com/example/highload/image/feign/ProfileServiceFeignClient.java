package com.example.highload.image.feign;

import com.example.highload.image.model.network.ImageDto;
import com.example.highload.image.model.network.ProfileDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient("profile-service")
public interface ProfileServiceFeignClient {
    @GetMapping("/single/{id}")
    public ResponseEntity<ProfileDto> getById(@PathVariable int id, @RequestHeader("Authorization") String token);

    @GetMapping("/image/{id}")
    public ResponseEntity<ImageDto> setNewMainImage(@PathVariable int id, @RequestBody ImageDto imageDto, @RequestHeader("Authorization") String token);
}
