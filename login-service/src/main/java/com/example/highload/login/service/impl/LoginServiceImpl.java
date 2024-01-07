package com.example.highload.login.service.impl;

import com.example.highload.login.model.inner.User;
import com.example.highload.login.repos.UserRepository;
import com.example.highload.login.secuity.util.JwtTokenUtil;
import com.example.highload.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;


    @Override
    public String login(String login, String password) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        return jwtTokenUtil.generateAccessToken(login);
    }

    @Override
    public User findByLoginElseNull(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

}
