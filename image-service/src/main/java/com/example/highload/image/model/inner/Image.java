package com.example.highload.image.model.inner;

import com.example.highload.image.model.inner.ImageObject;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Entity
@Table(name = "image", schema = "public")
public class Image {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "url")
    private String url;

    @OneToOne(mappedBy = "image")
    private ImageObject imageObject;

}
