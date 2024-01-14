package com.example.highload.notification.services;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface NotificationService {

//    Notification saveNotification(NotificationDto notificationDto);

    Mono<Notification> readNotification(int id);

    Flux<Notification> getAllUserNotifications(int userId);

    Flux<Notification> getNewUserNotifications(int userId);

    public Mono<Notification> sendNotification(int senderId, int receiverId, String token);
}
