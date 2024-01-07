package com.example.highload.model.enums;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;

public enum RoleType implements GrantedAuthority, Serializable {
    ARTIST,
    CLIENT,
    ADMIN;

    @Override
    public String getAuthority() {
        return name();
    }

}
