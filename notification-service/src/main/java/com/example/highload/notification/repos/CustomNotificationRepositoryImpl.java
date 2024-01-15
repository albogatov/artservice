package com.example.highload.notification.repos;

import com.example.highload.notification.mapper.NotificationDBMapper;
import com.example.highload.notification.model.inner.Notification;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class CustomNotificationRepositoryImpl implements CustomNotificationRepository{

    private final DatabaseClient client;

    private final String selectQuery = """
            select
            notification.id as n_id,
            notification.sender_profile_id as n_spid,
            notification.receiver_profile_id as n_rpid,
            notification.is_read as n_read,
            notification.time as n_time,
            s_profile.mail as sp_mail,
            r_profile.mail as rp_mail
            from notification join profile as s_profile on notification.sender_profile_id = s_profile.id
            join profile as r_profile on notification.receiver_profile_id = r_profile.id
            """;

    private final String updateQuery = """
            update
            notification set is_read = true where id = :id
            """;

    public CustomNotificationRepositoryImpl(DatabaseClient client) {
        this.client = client;
    }

    @Override
    public Mono<Notification> fetchById(Integer id) {
        NotificationDBMapper mapper = new NotificationDBMapper();
        String selectById = String.format("%s where notification.id = :notificationId limit 1;", selectQuery);
        return client
                .sql(selectById)
                .bind("notificationId", id)
                .map(mapper::apply)
                .first();
    }

    @Override
    public Mono<Notification> setRead(Integer id) {
        Mono<Notification> notification = fetchById(id);
        NotificationDBMapper mapper = new NotificationDBMapper();
        return client
                .sql(updateQuery)
                .bind("id", id)
                .fetch().first().thenReturn(notification.map(n -> {
                    n.setIsRead(true);
                    return n;
                })).block();
    }

    @Override
    public Flux<Notification> fetchAllByReceiverProfileId(Integer id) {
        NotificationDBMapper mapper = new NotificationDBMapper();
        String selectByReceiverId = String.format("%s where notification.receiver_profile_id = :profileId;", selectQuery);
        return client
                .sql(selectByReceiverId)
                .bind("profileId", id)
                .map(mapper::apply)
                .all();
    }

    @Override
    public Flux<Notification> fetchAllNotReadByReceiverProfileId(Integer id) {
        NotificationDBMapper mapper = new NotificationDBMapper();
        String selectByReceiverId = String.format("%s where notification.receiver_profile_id = :profileId and notification.is_read = false;", selectQuery);
        return client
                .sql(selectByReceiverId)
                .bind("profileId", id)
                .map(mapper::apply)
                .all();
    }

}
