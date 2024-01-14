package com.example.highload.image.model.network;

import com.example.highload.order.model.enums.OrderStatus;
import com.example.highload.order.model.network.TagDto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
