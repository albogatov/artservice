package com.example.highload.notification.model.network;

import lombok.Data;

import java.io.Serializable;

@Data
public class ResponseDto implements Serializable {

    private int id;
    private int userId;
    private String userName;
    private int orderId;
    private int orderUserId;
    private String orderUserName;
    private String text;
    private boolean isApproved;
}
