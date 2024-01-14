package com.example.highload.notification.model.inner;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Table(name = "notification", schema = "public")
public class Notification {

    @Id
    private Integer id;

    private Profile senderProfile;

    private Profile receiverProfile;

    @Column("sender_profile_id")
    private Integer senderProfileId;

    @Column("receiver_profile_id")
    private Integer receiverProfileId;

    @Column("is_read")
    private Boolean isRead;

    @Column("time")
    private LocalDateTime time;

}
