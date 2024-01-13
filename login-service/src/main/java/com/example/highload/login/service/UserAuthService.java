package com.example.highload.login.service;

import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserAuthService {

    UserDetailsService userDetailsService();

}
