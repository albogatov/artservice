package com.example.highload.notification.services.impl;

import com.example.highload.notification.feign.ProfileServiceFeignClient;
import com.example.highload.notification.mapper.NotificationMapper;
import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.network.ProfileDto;
import com.example.highload.notification.model.network.ResponseDto;
import com.example.highload.notification.repos.NotificationRepository;
import com.example.highload.notification.services.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.messaging.simp.user.SimpUserRegistry;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final ProfileServiceFeignClient profileServiceFeignClient;
    private final SimpMessageSendingOperations messagingTemplate;
    private final SimpUserRegistry userRegistry;

    @Override
    public Mono<Notification> readNotification(int id) {
        return notificationRepository.setRead(id)
                .onErrorResume(t -> {
                    return Mono.error(new NoSuchElementException("Wrong id!"));
                });
    }

    @Override
    public Mono<Notification> findById(int userId) {
        return notificationRepository.fetchById(userId);
    }

    @Override
    public Flux<Notification> getAllUserNotifications(int userId) {
        return notificationRepository.fetchAllByReceiverProfileId(userId);
    }

    @Override
    public Flux<Notification> getNewUserNotifications(int userId) {
        return notificationRepository.fetchAllNotReadByReceiverProfileId(userId);
    }

    //TODO Sender mail = null
    @KafkaListener(topics = "notifications", groupId = "response")
    @Override
    public void sendNotification(ResponseDto responseDto) {
        Mono.just(responseDto).flatMap(id -> {
            ProfileDto senderProfile = profileServiceFeignClient.getProfileDataByUserId(responseDto.getUserId()).getBody();
            ProfileDto receiverProfile = profileServiceFeignClient.getProfileDataByUserId(responseDto.getOrderUserId()).getBody();
            Notification notification = new Notification();
            notification.setSenderProfileId(senderProfile.getId());
            notification.setReceiverProfileId(receiverProfile.getId());
            notification.setIsRead(false);
            notification.setTime(LocalDateTime.now());
            Mono<Notification> notificationMono = notificationRepository.save(notification);
            return notificationMono;
        }).flatMap(notification -> {
            return findById(notification.getId());
        }).map(notification -> {
            messagingTemplate.convertAndSendToUser(responseDto.getOrderUserName(), "/notifications", notificationMapper.notificationToNotificationDto(notification));
            return notification;
        }).subscribe();
    }

//    @Override
//    public Mono<Notification> sendNotificationOld(int senderId, int receiverId, String token) {
//
//        List<Integer> ids = List.of(senderId, receiverId);
//        return Mono.just(profileServiceFeignClient.checkProfileExistsByIds(ids, token)).flatMap( b -> {
//                    if (b.getBody()) {
//                        Notification notification = new Notification();
//                        notification.setSenderProfileId(senderId);
//                        notification.setReceiverProfileId(receiverId);
//                        notification.setIsRead(false);
//                        notification.setTime(LocalDateTime.now());
//                        return notificationRepository.save(notification);
//                    } else throw new NoSuchElementException("Wrong profile id!");
//
//                }
//        ).flatMap(notification -> {
//            return findById(notification.getId());
//        }).onErrorResume(t -> {
//            return Mono.error(new NoSuchElementException("Wrong profile id!"));
//        });
//    }
}
