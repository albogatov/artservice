package com.example.highload.image.model.network;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.Serializable;

@Data
public class ImageDto implements Serializable {

    private int id;
    private String url;
    private MultipartFile image;

}
