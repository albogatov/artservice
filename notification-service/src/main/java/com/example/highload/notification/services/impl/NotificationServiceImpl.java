package com.example.highload.notification.services.impl;

import com.example.highload.notification.feign.ProfileServiceFeignClient;
import com.example.highload.notification.mapper.NotificationMapper;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.inner.Profile;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.repos.NotificationRepository;
import com.example.highload.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.awt.*;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ProfileServiceFeignClient profileServiceFeignClient;

    @Override
    public Mono<Notification> readNotification(int id) {
        return notificationRepository.fetchById(id)
                .switchIfEmpty(Mono.error(new NoSuchElementException("Wrong id!")))
                .doOnNext(notification -> {
                    notification.setIsRead(true);
                }).flatMap(notificationRepository::save);
    }

    @Override
    public Flux<Notification> getAllUserNotifications(int userId) {
        return notificationRepository.fetchAllByReceiverProfileId(userId);
    }

    @Override
    public Flux<Notification> getNewUserNotifications(int userId) {
        return notificationRepository.fetchAllNotReadByReceiverProfileId(userId);
    }

    @Override
    public Mono<Notification> sendNotification(int senderId, int receiverId, String token) {

        return Mono.fromCallable(()->{

            Flux.just(senderId, receiverId).subscribe(
                    id -> {
                        CompletableFuture<Boolean> fut = profileServiceFeignClient.checkProfileExistsById(senderId, token);
                        fut.thenAccept(val -> {
                            if (!val) {
                                throw new NoSuchElementException("Wrong profile id!");
                            }
                        });
                    }
            );

            Notification notification = new Notification();
            notification.setSenderProfileId(senderId);
            notification.setReceiverProfileId(receiverId);
            notification.setIsRead(false);
            notification.setTime(LocalDateTime.now());
            return notification;

        }).flatMap(notificationRepository::save);


    }
}
