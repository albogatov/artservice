package com.example.highload.order.controller;

import com.example.highload.order.mapper.TagMapper;
import com.example.highload.order.model.inner.Tag;
import com.example.highload.order.model.network.TagDto;
import com.example.highload.order.services.TagService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.postgresql.util.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/order/tag")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public ResponseEntity<Mono<TagDto>> save(@Valid @RequestBody TagDto data) {
       // tagService.saveTag(data);
        return ResponseEntity.ok(tagService.saveTag(data));
    }

    @GetMapping("/all")
    public ResponseEntity<Flux<TagDto>> getAll() {
        return ResponseEntity.ok().body(tagService.findAll());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions() {
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler(NumberFormatException.class)
    public ResponseEntity<?> handlePathExceptions() {
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler({CallNotPermittedException.class, FeignException.class})
    public ResponseEntity<?> handleExternalServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable now!");
    }

}
