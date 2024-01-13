package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.NotificationDto;
import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;

import java.util.List;

@Mapper(componentModel = "notification", uses = {NotificationMapper.class})
public interface NotificationListMapper {

    List<Notification> notificationDtosToNotifications(List<NotificationDto> notificationDtos);

    @InheritInverseConfiguration
    List<NotificationDto> notificationsToNotificationDtos(List<Notification> notifications);
}
