package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.inner.User;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.model.network.UserDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto notificationToDto(Notification notification);

    Notification notificationDtoToNotification(NotificationDto notificationDto);
}
