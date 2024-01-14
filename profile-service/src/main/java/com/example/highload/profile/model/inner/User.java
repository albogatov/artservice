package com.example.highload.profile.model.inner;

import com.example.highload.profile.model.enums.RoleType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "user", schema = "public")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column(name = "login", nullable = false, unique = true)
    private String login;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @NotBlank
    @Column(name = "hash_password", nullable = false)
    private String hashPassword;

    @ManyToOne
    @JoinColumn(name = "role_id", referencedColumnName = "id", updatable = false)
    private Role role;

    @Column(name = "is_actual", nullable = false)
    private Boolean isActual;

    @Column(name = "when_deleted_time", columnDefinition = "TIMESTAMP")
    private LocalDateTime whenDeletedTime;

    // TODO Think about what to do here
//    @OneToMany(mappedBy = "user")
//    private List<Response> responses;
//
//    @OneToMany(mappedBy = "user")
//    private List<ClientOrder> orders;
//
//    @OneToMany(mappedBy = "user")
//    private List<Review> reviews;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        for (RoleType role : RoleType.values()) {
            authorities.add(new SimpleGrantedAuthority(role.name()));
        }
        return authorities;
    }

    @Override
    public String getPassword() {
        return hashPassword;
    }

    @Override
    public String getUsername() {
        return login;
    }

    @Override
    public boolean isAccountNonExpired() {
        return isActual;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "";
    }
}
