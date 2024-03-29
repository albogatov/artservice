package com.example.highload.login.service.impl;

import com.example.highload.login.model.inner.User;
import com.example.highload.login.repos.UserRepository;
import com.example.highload.login.security.util.JwtTokenUtil;
import com.example.highload.login.service.LoginService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements LoginService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtil jwtTokenUtil;
    private final UserRepository userRepository;


    @Override
    public String login(String login, String password, String role) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(login, password));
        return jwtTokenUtil.generateAccessToken(login, role);
    }

    @Override
    public User findByLoginElseNull(String login) {
        return userRepository.findByLogin(login).orElse(null);
    }

}
