package com.example.highload.admin.feign;

import com.example.highload.admin.model.network.OrderDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient("order-service")
@CircuitBreaker(name = "CbServiceBasedOnCount")
public interface OrderServiceFeignClient {
    @GetMapping("/api/order/client/all/user/{userId}")
    ResponseEntity<List<OrderDto>> getAllUserOrders(@PathVariable int userId, @RequestHeader("Authorization") String token);
}
