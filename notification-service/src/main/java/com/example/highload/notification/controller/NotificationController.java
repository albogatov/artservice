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

import java.util.List;
import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PaginationHeadersCreator paginationHeadersCreator;
    private final DataTransformer dataTransformer;

    @PostMapping("/save")
    public ResponseEntity<?> save(@Valid @RequestBody NotificationDto data){
        if(notificationService.saveNotification(data) != null)
            return ResponseEntity.ok("Notification successfully created");
        else return ResponseEntity.badRequest().body("Couldn't save notification, check data");
    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> setRead(@PathVariable int id){
        if(notificationService.readNotification(id) != null)
            return ResponseEntity.ok("Notification status is set");
        else return ResponseEntity.badRequest().body("Couldn't change notification, check data");
    }

    @GetMapping("/all/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getAllQueries(@PathVariable int userId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Notification> entityList = notificationService.getAllUserNotifications(userId,pageable);
        List<NotificationDto> dtoList = dataTransformer.notificationListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
    }


    @GetMapping("/new/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    public ResponseEntity<?> getNewQueries(@PathVariable int userId, @PathVariable int page) {
        Pageable pageable = PageRequest.of(page, 50);
        Page<Notification> entityList = notificationService.getNewUserNotifications(userId, pageable);
        List<NotificationDto> dtoList = dataTransformer.notificationListToDto(entityList.getContent());
        HttpHeaders responseHeaders = paginationHeadersCreator.pageWithTotalElementsHeadersCreate(entityList);
        return ResponseEntity.ok().headers(responseHeaders).body(dtoList);
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
