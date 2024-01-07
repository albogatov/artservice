package com.example.highload.admin.model.network;

import com.example.highload.adminmodel.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Data
public class UserDto implements Serializable {

    private int id;
    @NotBlank
    @Size(min = 1, max = 50)
    private String login;
    @NotBlank
    private String password;
    private RoleType role;

}
