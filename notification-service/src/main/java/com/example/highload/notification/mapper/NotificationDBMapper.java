package com.example.highload.notification.mapper;

import com.example.highload.notification.model.inner.Notification;
import com.example.highload.notification.model.inner.Profile;
import io.r2dbc.spi.Row;

import java.time.LocalDateTime;
import java.util.function.BiFunction;

public class NotificationDBMapper implements BiFunction<Row, Object, Notification> {

    @Override
    public Notification apply(Row row, Object o) {

        Integer id = row.get("n_id", Integer.class);
        Integer sp_id = row.get("n_spid", Integer.class);
        Integer rp_id = row.get("n_rpid", Integer.class);
        Boolean is_read = row.get("n_read", Boolean.class);
        LocalDateTime localDateTime = row.get("n_time", LocalDateTime.class);

        String sp_email = row.get("sp_mail", String.class);
        String rp_email = row.get("rp_mail", String.class);

        Profile senderProfile = new Profile();
        senderProfile.setId(sp_id);
        senderProfile.setMail(sp_email);

        Profile receiverProfile = new Profile();
        receiverProfile.setId(rp_id);
        receiverProfile.setMail(rp_email);

        Notification notification = new Notification();
        notification.setId(id);
        notification.setSenderProfile(senderProfile);
        notification.setSenderProfile(receiverProfile);
        notification.setSenderProfileId(sp_id);
        notification.setSenderProfileId(rp_id);
        notification.setIsRead(is_read);
        notification.setTime(localDateTime);

        return notification;

    }
}
