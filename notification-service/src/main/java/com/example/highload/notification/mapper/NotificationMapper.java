package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.inner.User;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "notification")
public interface NotificationMapper {

    @Mapping(target = "receiverId", source = "notification.receiverProfileId")
    @Mapping(target = "senderId", source = "notification.senderProfileId")
    @Mapping(target = "senderMail", source = "notification.senderProfile.mail")
    NotificationDto notificationToNotificationDto(Notification notification);

//    Notification notificationDtoToNotification(NotificationDto notificationDto);
}
