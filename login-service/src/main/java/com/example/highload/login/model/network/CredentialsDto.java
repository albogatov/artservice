package com.example.highload.login.model.network;

import lombok.Data;

import java.io.Serializable;

@Data
public class CredentialsDto implements Serializable {

    private String login;
    private String token;

}
