package com.example.highload.notification.repos;

import com.example.highload.notification.model.inner.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Integer>, CustomNotificationRepository {


}
