package com.example.highload.notification.controller;

import com.example.highload.notification.mapper.NotificationMapper;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.services.NotificationService;
import feign.FeignException;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.NoSuchElementException;

@RestController
@RequestMapping(value = "/api/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;
    private final String topicName = "notifications";

//    @MessageMapping("/send/from-{senderId}/to-{receiverId}")
//    public ResponseEntity<Mono<NotificationDto>> send(@PathVariable int senderId, @PathVariable int receiverId, @RequestHeader(value = "Authorization", required = true) String token){
//        return ResponseEntity.ok(notificationService.sendNotification(senderId, receiverId, token, topicName).map(notificationMapper::notificationToNotificationDto));
//    }

    @PostMapping("/update/{id}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    @Operation(description = "Read notification",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            schema = @Schema (implementation = NotificationDto.class)
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<Mono<NotificationDto>> setRead(@PathVariable int id){
        return ResponseEntity.ok(notificationService.readNotification(id).map(notificationMapper::notificationToNotificationDto));
    }

    @GetMapping("/all/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    @Operation(description = "Get all notifications",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Данные запроса некорректные"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
    public ResponseEntity<Flux<NotificationDto>> getAllQueries(@PathVariable int userId) {

        Flux<Notification> entityList = notificationService.getAllUserNotifications(userId);
        return ResponseEntity.ok().body(entityList.map(notificationMapper::notificationToNotificationDto));

    }


    @GetMapping("/new/{userId}/{page}")
    @PreAuthorize("hasAnyAuthority('CLIENT', 'ARTIST')")
    @Operation(description = "Get only unread notifications",
            security = { @SecurityRequirement(name = "bearer-key")})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = {
                    @Content(
                            mediaType = "application/json",
                            array = @ArraySchema(schema = @Schema(implementation = NotificationDto.class))
                    )
            }),
            @ApiResponse(responseCode = "400", description = "Request data incorrect"),
            @ApiResponse(responseCode = "403", description = "No authority for this operations"),
            @ApiResponse(responseCode = "401", description = "Unauthorized request")
    })
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

    @ExceptionHandler({CallNotPermittedException.class})
    public ResponseEntity<?> handleExternalServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("External service is unavailable now!");
    }

    @ExceptionHandler({FeignException.class})
    public ResponseEntity<?> handleUnexpectedServiceExceptions() {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Couldn't make call for external service");
    }
}
