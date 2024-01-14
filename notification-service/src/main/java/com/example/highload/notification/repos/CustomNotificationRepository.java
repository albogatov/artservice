package com.example.highload.notification.repos;

import com.example.highload.notification.model.inner.Notification;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomNotificationRepository {

    Mono<Notification> fetchById(Integer id);
    Flux<Notification> fetchAllByReceiverProfileId(Integer id);

    public Flux<Notification> fetchAllNotReadByReceiverProfileId(Integer id);


}
