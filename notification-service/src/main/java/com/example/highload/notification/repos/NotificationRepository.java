package com.example.highload.notification.repos;

import com.example.highload.notification.model.inner.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Integer> {
    Optional<Page<Notification>> findAllByReceiverProfile_Id(Integer id, Pageable pageable);
    Optional<Page<Notification>> findAllBySenderProfile_Id(Integer id, Pageable pageable);
    Optional<Page<Notification>> findAllByIsReadFalseAndReceiverProfile_Id(Integer receiverId, Pageable pageable);

}
