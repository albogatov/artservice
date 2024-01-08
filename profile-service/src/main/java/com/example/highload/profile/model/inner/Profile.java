package com.example.highload.profile.model.inner;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.ToString;

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

    @OneToOne
    @JoinColumn(name = "image_id", referencedColumnName = "id")
    private Image image;

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

    // TODO What here?
    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER)
    private List<ImageObject> images;

    @OneToMany(mappedBy = "receiverProfile")
    private List<Notification> receivedNotifications;

    @OneToMany(mappedBy = "senderProfile")
    private List<Notification> sentNotifications;

    @Override
    public String toString() {
        return "";
    }
}
