package com.example.highload.image.feign;

import com.example.highload.image.model.network.OrderDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

@FeignClient("order-service")
public interface OrderServiceFeignClient {
    @GetMapping("/single/{orderId}")
    ResponseEntity<OrderDto> getById(@PathVariable int orderId, @RequestHeader("Authorization") String token);
}
