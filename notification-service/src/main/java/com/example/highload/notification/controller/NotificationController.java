package com.example.highload.notification.controller;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.services.NotificationService;
import com.example.highload.notification.utils.PaginationHeadersCreator;
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
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PaginationHeadersCreator paginationHeadersCreator;

    @PostMapping("/send/from-{senderId}/to-{receiverId}")
    public ResponseEntity<Mono<Notification>> send(@PathVariable int senderId, @PathVariable int receiverId, @RequestHeader(value = "Authorization", required = true) String token){
        return ResponseEntity.ok(notificationService.sendNotification(senderId, receiverId, token).onErrorResume(t -> {
            return Mono.error(new NoSuchElementException("Wrong profile id!"));
        }));
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> setRead(@PathVariable int id){
        notificationService.readNotification(id).subscribe();
        return ResponseEntity.ok("Notification status is set");
    }

    @GetMapping("/all/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllQueries(@PathVariable int userId) {

        Flux<Notification> entityList = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok().body(entityList);

    }


    @GetMapping("/new/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getNewQueries(@PathVariable int userId) {

        Flux<Notification> entityList = notificationService.getNewUserNotifications(userId);
        return ResponseEntity.ok().body(entityList);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(){
        return ResponseEntity.badRequest().body("Request body validation failed!");
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<?> handleServiceExceptions(){
        return ResponseEntity.badRequest().body("Wrong ids in path!");
    }
}
