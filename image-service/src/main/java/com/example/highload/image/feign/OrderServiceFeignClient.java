package com.example.highload.image.feign;

import com.example.highload.image.model.network.OrderDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import reactor.core.publisher.Mono;

@CircuitBreaker(name = "orderServiceBreaker")
@FeignClient("order-service")
public interface OrderServiceFeignClient {
    @GetMapping("/api/order/client/single/{orderId}")
    ResponseEntity<OrderDto> getById(@PathVariable int orderId, @RequestHeader("Authorization") String token);
}
