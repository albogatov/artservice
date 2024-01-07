package com.example.highload.notification.model.network;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class NotificationDto implements Serializable {

    private int id;
    private int receiverId;
    private int senderId;
    private boolean isRead;
    private LocalDateTime time;
    @NotBlank
    private String senderMail;
}
