package com.example.highload.notification.model.inner;

import com.example.highload.notification.model.enums.RoleType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Table(name = "user", schema = "public")
public class User implements UserDetails {

    @Id
    private Integer id;

    @NotBlank
    @Size(min = 1, max = 50)
    @Column("login")
    private String login;

    @NotBlank
    @Column("hash_password")
    private String hashPassword;

//    @ManyToOne
//    @JoinColumn(name = "role_id", referencedColumnName = "id", updatable = false)
//    private Role role;

    @Column("role_id")
    private Integer roleId;

    @Column("is_actual")
    private Boolean isActual;

    @Column("when_deleted_time")
    private LocalDateTime whenDeletedTime;


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
