package com.example.highload.model.network;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.io.Serializable;

@Data
public class ProfileDto implements Serializable {

    private int id;
    private int userId;
    @NotBlank
    private String name;
    private String experience;
    private String education;
    private String about;
    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{1,4}$")
    private String mail;
}
