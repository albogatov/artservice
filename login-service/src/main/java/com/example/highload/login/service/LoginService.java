package com.example.highload.login.service;

import com.example.highload.login.model.inner.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;


public interface LoginService {

    String login(String login, String password);

    User findByLoginElseNull(String login);

}
