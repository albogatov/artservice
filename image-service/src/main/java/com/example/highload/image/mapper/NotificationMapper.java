package com.example.highload.notification.mapper;

import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    NotificationDto notificationToDto(Notification notification);

    Notification notificationDtoToNotification(NotificationDto notificationDto);
}
