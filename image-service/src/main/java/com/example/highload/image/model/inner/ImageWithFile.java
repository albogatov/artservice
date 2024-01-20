package com.example.highload.image.model.inner;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ImageWithFile {
    private int id;
    private String url;
    private MultipartFile image;
}
