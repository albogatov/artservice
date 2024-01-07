package com.example.highload.profile.model.network;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class ReviewDto implements Serializable {

    private int id;
    private int profileId;
    private String userName;
    @NotBlank
    private String text;
}
