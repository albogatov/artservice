package com.example.highload.profile.model.network;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class ImageDto implements Serializable {

    private int id;
    @NotBlank
    private String url;

}
