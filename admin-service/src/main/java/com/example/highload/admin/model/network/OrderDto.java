package com.example.highload.admin.model.network;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class OrderDto implements Serializable {
    private int id;
    private int userId;
    private String userName;
    private LocalDateTime created;
    @Min(0)
    private int price;
    @NotBlank
    private String description;
}
