package com.example.highload.login.model.inner;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.util.List;

@Data
@Entity
@Table(name = "profile", schema = "public")
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", updatable = false)
    private User user;

    @NotBlank
    @Column(name = "name", nullable = false)
    private String name;
    @Column(name = "experience")
    private String experience;
    @Column(name = "education")
    private String education;
    @Column(name = "about")
    private String about;

    @NotBlank
    @Pattern(regexp = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{1,4}$")
    @Column(name = "mail", nullable = false)
    private String mail;

    @Override
    public String toString() {
        return "";
    }
}
