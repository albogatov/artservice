package com.example.highload.order.controller;

import com.example.highload.order.model.inner.ClientOrder;
import com.example.highload.profile.model.inner.Image;
import com.example.highload.profile.model.network.ImageDto;
import com.example.highload.order.model.network.OrderDto;
import com.example.highload.order.services.ImageService;
import com.example.highload.order.services.OrderService;
import com.example.highload.utils.DataTransformer;
import com.example.highload.order.utils.PaginationHeadersCreator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/order")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final ImageService imageService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final DataTransformer dataTransformer;

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

    @GetMapping("/all/user/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllUserOrders(@PathVariable int userId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<ClientOrder> entityList = orderService.getUserOrders(userId, pageable);
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dataTransformer.orderListToDto(entityList.getContent()));
    }

    @GetMapping("/open/user/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllUserOpenOrders(@PathVariable int userId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<ClientOrder> entityList = orderService.getUserOpenOrders(userId, pageable);
        List<OrderDto> dtoList = dataTransformer.orderListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/single/{orderId}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getById(@PathVariable int orderId) {
        ClientOrder entity = orderService.getOrderById(orderId);
        return ResponseEntity.ok(dataTransformer.orderToDto(entity));
    }


    @PostMapping("/single/{orderId}/tags/add")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<?> addTagsToOrder(@Valid @RequestBody List<Integer> tagIds, @PathVariable int orderId) {
        ClientOrder order = orderService.addTagsToOrder(tagIds, orderId);
        if (order != null) {
            return ResponseEntity.ok(dataTransformer.orderToDto(order));
        }
        return ResponseEntity.badRequest().body("Invalid total tag number (should be not more than 10)!");
    }

    @PostMapping("/single/{orderId}/tags/delete")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<?> deleteTagsFromOrder(@Valid @RequestBody List<Integer> tagIds, @PathVariable int orderId) {
        ClientOrder order = orderService.deleteTagsFromOrder(tagIds, orderId);
        if (order != null) {
            return ResponseEntity.ok(dataTransformer.orderToDto(order));
        }
        return ResponseEntity.badRequest().body("Invalid tag ids!");
    }

    @GetMapping("/single/{orderId}/images/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getOrderImages(@Valid @PathVariable int orderId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Image> entityList = imageService.findAllOrderImages(orderId, pageable);
        List<ImageDto> dtoList = dataTransformer.imageListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }

    @GetMapping("/all/tag/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllOrdersByTags(@Valid @RequestBody List<Integer> tags, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<ClientOrder> entityList = orderService.getOrdersByTags(tags, pageable);
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dataTransformer.orderListToDto(entityList.getContent()));
    }

    @GetMapping("/open/tag/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllOpenOrdersByTags(@Valid @RequestBody List<Integer> tags, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<ClientOrder> entityList = orderService.getOpenOrdersByTags(tags, pageable);
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dataTransformer.orderListToDto(entityList.getContent()));
    }

    @GetMapping("/all/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllOrders(@PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<ClientOrder> entityList = orderService.getAllOrders(pageable);
        List<OrderDto> dtoList = dataTransformer.orderListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.endlessSwipeHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
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
