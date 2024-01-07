package com.example.highload.login.service;

import com.example.highload.login.model.inner.User;


public interface LoginService {

    String login(String login, String password);

    User findByLoginElseNull(String login);
}
