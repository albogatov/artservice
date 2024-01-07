package com.example.highload.order.model.network;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class JwtRequest implements Serializable {
    private String login;
    private String password;
    private String role;
}
