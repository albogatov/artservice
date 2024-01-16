package com.example.highload.order.controller;

import com.example.highload.order.mapper.ResponseMapper;
import com.example.highload.order.model.inner.Response;
import com.example.highload.order.model.network.ResponseDto;
import com.example.highload.order.services.ResponseService;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/order/response")
@RequiredArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @PostMapping("/save")
    @PreAuthorize("hasAnyAuthority('ARTIST')")
    public ResponseEntity<?> save(@Valid @RequestBody ResponseDto data) {
        if(responseService.saveResponse(data) != null)
            return ResponseEntity.ok("Response added");
        else return ResponseEntity.badRequest().body("Couldn't save response, check data");
    }

    @PostMapping("/approve/{responseId}")
    @PreAuthorize("hasAnyAuthority('CLIENT')")
    public ResponseEntity<Mono<ResponseDto>> approve(@PathVariable int responseId) {
        Mono<ResponseDto> response = responseService.approve(responseId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/all/order/{orderId}")
    @PreAuthorize("hasAnyAuthority('ARTIST', 'CLIENT')")
    public ResponseEntity<Flux<ResponseDto>> getAllByOrder(@PathVariable int orderId) {
        return ResponseEntity.ok().body(responseService.findAllForOrder(orderId));
    }

    @GetMapping("/all/user/{userId}")
    @PreAuthorize("hasAnyAuthority('ARTIST')")
    public ResponseEntity<Flux<ResponseDto>> getAllByUser(@PathVariable int userId) {
        return ResponseEntity.ok().body(responseService.findAllForUser(userId));
    }

    @GetMapping("/single/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Mono<ResponseDto>> getById(@PathVariable int id) {
        return ResponseEntity.ok(responseService.findById(id));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class )
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex){
        return ResponseEntity.badRequest().body("Request body validation failed! " + ex.getLocalizedMessage());
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions(){
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }

    @ExceptionHandler({CallNotPermittedException.class, FeignException.class})
    public ResponseEntity<?> handleExternalServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable now!");
    }
}
