package com.example.highload.notification.controller;

import com.example.highload.notification.mapper.NotificationMapper;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.services.NotificationService;
import com.example.highload.notification.utils.PaginationHeadersCreator;
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
@RequestMapping(value = "/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final PaginationHeadersCreator paginationHeadersCreator;

    @PostMapping("/send/from-{senderId}/to-{receiverId}")
    public ResponseEntity<Mono<NotificationDto>> send(@PathVariable int senderId, @PathVariable int receiverId, @RequestHeader(value = "Authorization", required = true) String token){
        return ResponseEntity.ok(notificationService.sendNotification(senderId, receiverId, token).map(notificationMapper::notificationToNotificationDto));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Mono<NotificationDto>> setRead(@PathVariable int id){
        return ResponseEntity.ok(notificationService.readNotification(id).map(notificationMapper::notificationToNotificationDto));
    }

    @GetMapping("/all/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<NotificationDto>> getAllQueries(@PathVariable int userId) {

        Flux<Notification> entityList = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok().body(entityList.map(notificationMapper::notificationToNotificationDto));

    }


    @GetMapping("/new/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<Flux<NotificationDto>> getNewQueries(@PathVariable int userId) {

        Flux<Notification> entityList = notificationService.getNewUserNotifications(userId);
        return ResponseEntity.ok().body(entityList.map(notificationMapper::notificationToNotificationDto));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
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
