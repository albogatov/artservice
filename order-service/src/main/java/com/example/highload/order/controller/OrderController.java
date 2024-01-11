package com.example.highload.order.controller;

import com.example.highload.order.mapper.OrderMapper;
import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.services.OrderService;
import com.example.highload.order.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Or;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final OrderMapper orderMapper;

    @PostMapping("/save")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> save(@Valid @RequestBody OrderDto data) {
        if (orderService.saveOrder(data) != null)
            return ResponseEntity.ok("Order saved");
        else return ResponseEntity.badRequest().body("Couldn't save order, check data");
    }

    @PostMapping("/update/{orderId}")
    @PreAuthorize("hasAuthority('CLIENT')")
    public ResponseEntity<?> update(@Valid @RequestBody OrderDto data, @PathVariable int orderId) {
        if (orderService.updateOrder(data, orderId) != null)
            return ResponseEntity.ok("Order updated");
        else return ResponseEntity.badRequest().body("Couldn't save order, check data");
    }

    @GetMapping("/all/user/{userId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<OrderDto>> getAllUserOrders(@PathVariable int userId) {
        Flux<OrderDto> entityList = orderService.getUserOrders(userId);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/open/user/{userId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllUserOpenOrders(@PathVariable int userId) {
        Flux<OrderDto> entityList = orderService.getUserOpenOrders(userId);
        return ResponseEntity.ok().body(entityList);
    }

    @GetMapping("/single/{orderId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Mono<OrderDto>> getById(@PathVariable int orderId) {
        return ResponseEntity.ok(orderService.getOrderById(orderId));
    }


    @PostMapping("/single/{orderId}/tags/add")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<?> addTagsToOrder(@Valid @RequestBody List<Integer> tagIds, @PathVariable int orderId) {
        Mono<OrderDto> order = orderService.addTagsToOrder(tagIds, orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().body("Invalid total tag number (should be not more than 10)!");
    }

    @PostMapping("/single/{orderId}/tags/delete")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<?> deleteTagsFromOrder(@Valid @RequestBody List<Integer> tagIds, @PathVariable int orderId) {
        Mono<OrderDto> order = orderService.deleteTagsFromOrder(tagIds, orderId);
        if (order != null) {
            return ResponseEntity.ok(order);
        }
        return ResponseEntity.badRequest().body("Invalid tag ids!");
    }

    @GetMapping("/all/tag")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<OrderDto>> getAllOrdersByTags(@Valid @RequestBody List<Integer> tags) {
        return ResponseEntity.ok().body(orderService.getOrdersByTags(tags));
    }

    @GetMapping("/open/tag/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<OrderDto>> getAllOpenOrdersByTags(@Valid @RequestBody List<Integer> tags, @PathVariable int page) {
        return ResponseEntity.ok().body(orderService.getOpenOrdersByTags(tags));
    }

    @GetMapping("/all")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<OrderDto>> getAllOrders(@PathVariable int page) {
        return ResponseEntity.ok().body(orderService.getAllOrders());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions() {
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

}
