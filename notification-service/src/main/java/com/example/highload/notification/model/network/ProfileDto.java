package com.example.highload.notification.model.network;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProfileDto implements Serializable {

    private int id;
    private int userId;
    private String mail;
}
