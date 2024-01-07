package com.example.highload.notification.services.impl;

import com.example.highload.notification.mapper.NotificationMapper;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.repos.NotificationRepository;
import com.example.highload.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.awt.*;
import java.util.Collections;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;


    @Override
    public Notification saveNotification(NotificationDto notificationDto) {
        return notificationRepository.save(notificationMapper.notificationDtoToNotification(notificationDto));
    }

    @Override
    public Notification readNotification(int id) {
        Notification notification = notificationRepository.findById(id).orElseThrow();
        notification.setIsRead(true);
        notificationRepository.save(notification);
        return notification;
    }

    @Override
    public Page<Notification> getAllUserNotifications(int userId, Pageable pageable) {
        return notificationRepository.findAllByReceiverProfile_Id(userId, pageable).orElse(Page.empty());
    }

    @Override
    public Page<Notification> getNewUserNotifications(int userId, Pageable pageable) {
        return notificationRepository.findAllByIsReadFalseAndReceiverProfile_Id(userId, pageable).orElse(Page.empty());
    }
}
