package com.example.highload.notification.model.inner;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "notification", schema = "public")
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    //TODO Add profile here or replace with Int
    @ManyToOne
    @JoinColumn(name = "sender_profile_id", referencedColumnName = "id")
    private Profile senderProfile;

    @ManyToOne
    @JoinColumn(name = "receiver_profile_id", referencedColumnName = "id")
    private Profile receiverProfile;

    @Column(name = "is_read", nullable = false)
    private Boolean isRead;

    @Column(name = "time", columnDefinition = "TIMESTAMP", nullable = false)
    private LocalDateTime time;

}
