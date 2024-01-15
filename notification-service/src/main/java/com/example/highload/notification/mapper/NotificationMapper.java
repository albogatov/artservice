package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.inner.User;
import com.example.highload.notification.model.network.NotificationDto;
import com.example.highload.notification.model.network.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(target = "receiverId", source = "receiverProfileId")
    @Mapping(target = "senderId", source = "senderProfileId")
    @Mapping(target = "senderMail", source = "senderProfile.mail")
    @Mapping(target = "read", source = "isRead")
    NotificationDto notificationToNotificationDto(Notification notification);

//    Notification notificationDtoToNotification(NotificationDto notificationDto);
}
