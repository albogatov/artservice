package com.example.highload.notification.services;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.ResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface NotificationService {

//    Notification saveNotification(NotificationDto notificationDto);

    Mono<Notification> readNotification(int id);

    Mono<Notification> findById(int userId);

    Flux<Notification> getAllUserNotifications(int userId);

    Flux<Notification> getNewUserNotifications(int userId);

    public void sendNotification(ResponseDto responseDto);
}
