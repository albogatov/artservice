package com.example.highload.order.model.network;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;

@Data
public class TagDto implements Serializable {

    private int id;
    @NotBlank
    private String name;
}
