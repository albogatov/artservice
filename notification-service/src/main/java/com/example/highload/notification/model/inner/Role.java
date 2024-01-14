package com.example.highload.notification.model.inner;

import com.example.highload.notification.model.enums.RoleType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table(name = "role", schema = "public")
public class Role {

    @Id
    private Integer id;

    @Column("name")
    private RoleType name;
}
