package com.example.highload.order.model.inner;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "response", schema = "public")
public class Response {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
    @ManyToOne
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private Integer orderId;

    @Column(name = "text")
    private String text;

    @Column(name = "is_approved", nullable = false)
    private Boolean isApproved;
}
